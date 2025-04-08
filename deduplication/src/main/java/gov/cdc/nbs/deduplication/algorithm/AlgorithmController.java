package gov.cdc.nbs.deduplication.algorithm;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.algorithm.dataelements.DataElementsService;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmExport;
import gov.cdc.nbs.deduplication.algorithm.pass.PassService;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.AlgorithmException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;

@RestController
@RequestMapping("/configuration")
public class AlgorithmController {

    private final PassService passService;
    private final DataElementsService dataElementsService;
    private final ObjectMapper mapper;

    public AlgorithmController(
            final PassService passService,
            final DataElementsService dataElementsService,
            final ObjectMapper mapper) {
        this.passService = passService;
        this.dataElementsService = dataElementsService;
        this.mapper = mapper;
    }

    @GetMapping()
    public Algorithm get() {
        return passService.getCurrentAlgorithm();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {
        AlgorithmExport export = new AlgorithmExport(
                dataElementsService.getCurrentDataElements(),
                passService.getCurrentAlgorithm());

        byte[] body;

        try {
            body = mapper.writeValueAsString(export).getBytes();
        } catch (JsonProcessingException e) {
            throw new AlgorithmException("Failed to export algorithm");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("deduplication_config_" + LocalDateTime.now().toString() + ".json")
                                .build()
                                .toString())
                .body(body);
    }

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Algorithm importAlgorithm(@RequestPart(value = "file") MultipartFile file) {
        try {
            AlgorithmExport algorithmExport = mapper.readValue(file.getBytes(), AlgorithmExport.class);
            dataElementsService.save(algorithmExport.dataElements());
            passService.saveAlgorithm(algorithmExport.algorithm());
            return algorithmExport.algorithm();
        } catch (IOException e) {
            throw new AlgorithmException("Failed to import algorithm");
        }

    }
}
