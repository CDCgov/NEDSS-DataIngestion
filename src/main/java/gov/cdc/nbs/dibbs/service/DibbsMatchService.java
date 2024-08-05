package gov.cdc.nbs.dibbs.service;


import gov.cdc.nbs.dibbs.model.EntityLocatorParticipationDto;
import gov.cdc.nbs.dibbs.model.PersonContainer;
import gov.cdc.nbs.dibbs.model.PostalLocatorDto;
import gov.cdc.nbs.dibbs.model.person.PersonDto;
import gov.cdc.nbs.dibbs.model.person.PersonNameDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Component
@Slf4j
public class DibbsMatchService {
  ObjectMapper mapper = new ObjectMapper();

  public ResponseEntity<JsonNode> match(PersonContainer personContainer) throws IOException, InterruptedException {
    String requestJson = convertPersonToFHIRFormat(personContainer).toString();
    log.info("Request body: " + requestJson);

    HttpClient client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/link-record")) // Ensure this URL is correct
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    JsonNode jsonResponse = mapper.readTree(response.body());

    HttpStatus status = HttpStatus.resolve(response.statusCode());
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return ResponseEntity.status(status).body(jsonResponse);
  }


  private ObjectNode convertPersonToFHIRFormat(PersonContainer personContainer) {
    PersonDto personDto = personContainer.thePersonDto;

    ObjectNode patient = mapper.createObjectNode();
    patient.put("resourceType", "Patient");

    Collection<PersonNameDto> nameDtos = personContainer.thePersonNameDtoCollection;
    setPatientName(patient, nameDtos);

    Collection<EntityLocatorParticipationDto> locatorDtos = personContainer.theEntityLocatorParticipationDtoCollection;
    setPatientAddress(patient, locatorDtos);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = sdf.format(personDto.getBirthTime());
    patient.set("birthDate", mapper.valueToTree(formattedDate));

    ObjectNode entry = mapper.createObjectNode();
    entry.set("resource", patient);
    ObjectNode bundle = mapper.createObjectNode();
    bundle.put("resourceType", "Bundle");
    ArrayNode entries = mapper.createArrayNode();
    entries.add(entry);
    bundle.set("entry", entries);
    ObjectNode finalJson = mapper.createObjectNode();
    finalJson.set("bundle", bundle);
    finalJson.put("use_enhanced", true);
    return finalJson;
  }

  private void setPatientName(ObjectNode patient, Collection<PersonNameDto> nameDtos) {
    if (nameDtos != null) {
      ArrayNode names = mapper.createArrayNode();
      for (PersonNameDto nameDto : nameDtos) {
        ObjectNode name = mapper.createObjectNode();
        name.set("family", mapper.valueToTree(nameDto.getLastNm()));
        ArrayNode givenNames = mapper.createArrayNode();
        givenNames.add(nameDto.getFirstNm());
        name.set("given", givenNames);
        names.add(name);
      }
      patient.set("name", names);
    }
  }

  private void setPatientAddress(ObjectNode patient, Collection<EntityLocatorParticipationDto> locatorDtos) {
    if (locatorDtos != null) {
      ArrayNode addresses = mapper.createArrayNode();
      for (EntityLocatorParticipationDto locatorDto : locatorDtos) {
        ObjectNode address = mapper.createObjectNode();
        PostalLocatorDto postalLocatorDto = locatorDto.getThePostalLocatorDto();
        ArrayNode lines = mapper.createArrayNode();

        StringType addressLine1 = new StringType(postalLocatorDto.getStreetAddr1());
        StringType addressLine2 = new StringType(postalLocatorDto.getStreetAddr2());
        lines.add(addressLine1.getValue());
        lines.add(addressLine2.getValue());

        address.set("line", lines);
        address.set("city", mapper.valueToTree(postalLocatorDto.getCityDescTxt()));
        address.set("state", mapper.valueToTree(postalLocatorDto.getStateCd()));
        address.set("postalCode", mapper.valueToTree(postalLocatorDto.getZipCd()));
        address.set("country", mapper.valueToTree(postalLocatorDto.getCntryDescTxt()));

        addresses.add(address);
      }
      patient.set("address", addresses);
    }
  }



}
