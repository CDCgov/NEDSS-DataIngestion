package gov.cdc.dataingestion.rawmessage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FhirService {

    public void convertFhirBundleToPhdcXML(String fhirJson){
        System.out.println("in service:"+fhirJson);
    }
}
