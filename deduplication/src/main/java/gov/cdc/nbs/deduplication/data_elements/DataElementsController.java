package gov.cdc.nbs.deduplication.data_elements;

import gov.cdc.nbs.deduplication.data_elements.dto.DataElementsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deduplication")
public class DataElementsController {

    private final DataElementsService service;

    @Autowired
    public DataElementsController(DataElementsService service) {
        this.service = service;
    }

    // POST method to save the data element configuration
    @PostMapping("/save-data-elements")
    public ResponseEntity<String> saveDataElementConfiguration(@RequestBody DataElementsDTO dataElementsDTO) {
        try {
            // Call the service to save the configuration
            service.saveDataElementConfiguration(dataElementsDTO);
            return ResponseEntity.ok("Data element configuration saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving configuration: " + e.getMessage());
        }
    }
}
