package gov.cdc.nbs.deduplication.algorithm.dataelements;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;
import gov.cdc.nbs.deduplication.algorithm.pass.PassService;

@RestController
@RequestMapping("/configuration/data-elements")
@PreAuthorize("hasAuthority('MERGE-PATIENT')")
public class DataElementsController {

    private final DataElementsService service;
    private final PassService passService;

    public DataElementsController(
            final DataElementsService service,
            final PassService passService) {
        this.service = service;
        this.passService = passService;
    }

    @GetMapping
    public DataElements get() {
        return service.getCurrentDataElements();
    }

    @PostMapping
    public DataElements save(@RequestBody DataElements dataElements) {
        DataElements updated = service.save(dataElements);
        passService.saveDibbsAlgorithm();
        return updated;

    }
}
