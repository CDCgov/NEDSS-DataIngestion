package gov.cdc.nbs.deduplication.algorithm.pass;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.Pass;

@RestController
@RequestMapping("/api/deduplication/configuration")
public class PassController {

    private final PassService passService;

    public PassController(final PassService passService) {
        this.passService = passService;
    }

    @GetMapping()
    public Algorithm get() {
        return passService.getCurrentAlgorithm();
    }

    @PostMapping("/pass")
    public Algorithm save(@RequestBody Pass pass) {
        return passService.save(pass);
    }

    @PutMapping("/pass/{id}")
    public Algorithm update(@PathVariable("id") Long id, @RequestBody Pass pass) {
        return passService.update(id, pass);
    }

    @DeleteMapping("/pass/{id}")
    public Algorithm delete(@PathVariable("id") Long id) {
        return passService.delete(id);
    }
}
