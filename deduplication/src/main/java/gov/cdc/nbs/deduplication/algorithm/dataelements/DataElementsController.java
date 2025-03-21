package gov.cdc.nbs.deduplication.algorithm.dataelements;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;

@RestController
@RequestMapping("/api/deduplication/configuration/data-elements")
public class DataElementsController {

    private final DataElementsService service;

    public DataElementsController(final DataElementsService service) {
        this.service = service;
    }

    @GetMapping
    public DataElements get() {
        return service.getCurrentDataElements();
    }

    @PostMapping
    public DataElements save(@RequestBody DataElements dataElements) {
        return service.save(dataElements);
    }
}
