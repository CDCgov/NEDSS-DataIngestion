package gov.cdc.dataingestion.rawmessage.controller;

import com.google.gson.Gson;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.services.interfaces.IEcrMsgQueryService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ELR Reports", description = "ELR reports API")

@RestController
@RequestMapping("/api/reports")
@Slf4j
@RequiredArgsConstructor
public class ElrReportsController {

    @Autowired
    private final RawELRService rawELRService;

//    private IEcrMsgQueryService ecrMsgQueryService;
//    private ICdaMapper mapper;
//
//    @Autowired
//    public ElrReportsController(IEcrMsgQueryService ecrMsgQueryService,
//                                ICdaMapper mapper,
//                                RawELRService rawELRService) {
//        this.ecrMsgQueryService = ecrMsgQueryService;
//        this.mapper = mapper;
//        this.rawELRService = rawELRService;
//    }



    @Operation(
            summary = "Submit a plain text HL7 message",
            description = "Submit a plain text HL7 message with msgType header",
            tags = { "dataingestion", "elr" })
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> save(@RequestBody final String payload, @RequestHeader("msgType") String type) {
            RawERLDto rawERLDto = new RawERLDto();
            rawERLDto.setType(type);
            rawERLDto.setPayload(payload);
            return ResponseEntity.ok(rawELRService.submission(rawERLDto));
    }

    @Operation(
            summary = "Get a report information by id",
            description = "Get a HL7 report by the given id",
            tags = { "dataingestion", "elr" })
    @GetMapping(path = "/{id}")
    public ResponseEntity<RawERLDto> getById(@PathVariable String id) {
        return ResponseEntity.ok(rawELRService.getById(id));
    }

//    @Operation(
//            summary = "TEST ECR COMM")
//    @GetMapping(path = "/test")
//    public ResponseEntity<String> getTestEcrAfterPatch() {
//        Gson gson = new Gson();
//        var result = ecrMsgQueryService.GetSelectedEcrRecord();
//
//
//        try {
//            mapper.tranformSelectedEcrToCDAXml(result);
//
//        } catch (Exception e){
//            var error = e;
//            System.out.println(e.getMessage());
//        }
//        String jsonString = gson.toJson(result);
//
//        return ResponseEntity.ok(jsonString);
//    }
}
