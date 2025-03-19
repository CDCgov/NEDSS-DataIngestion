package gov.cdc.nbs.deduplication.algorithm;

import gov.cdc.nbs.deduplication.algorithm.dto.ExportConfigRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deduplication")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    // Endpoint to fetch the export configuration
    @PostMapping("/export-configuration")
    public ResponseEntity<byte[]> exportConfiguration(@RequestBody ExportConfigRecord exportConfig) {
        // Generate the export JSON based on the UI data
        byte[] jsonBytes = algorithmService.generateExportJson(exportConfig);

        // Return the JSON file as a downloadable response
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=deduplication_config.json")
                .body(jsonBytes);
    }
}
