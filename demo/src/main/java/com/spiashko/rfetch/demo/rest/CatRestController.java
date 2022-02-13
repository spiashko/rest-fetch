package com.spiashko.rfetch.demo.rest;

import com.fasterxml.jackson.annotation.JsonView;
import com.spiashko.rfetch.demo.cat.Cat;
import com.spiashko.rfetch.demo.cat.CatRepository;
import com.spiashko.rfetch.demo.crudbase.View;
import com.spiashko.rfetch.jpa.smart.FetchSmartTemplate;
import com.spiashko.rfetch.parser.RfetchSupport;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CatRestController {

    private final TransactionTemplate transactionTemplate;
    private final FetchSmartTemplate fetchSmartTemplate;
    private final CatRepository repository;
    private final BeforeRequestActionsExecutor beforeRequestActionsExecutor;

    @JsonView(View.Retrieve.class)
    @GetMapping("/cats")
    public List<Cat> findAll(
            @RequestParam(value = "filter", required = false) String rsqlFilter,
            @RequestParam(value = "include", required = false) String rfetchInclude
    ) {
        beforeRequestActionsExecutor.execute(rsqlFilter, rfetchInclude, Cat.class, View.Retrieve.class);

        return transactionTemplate.execute(s -> {
            List<Cat> result = repository.findAll(RSQLJPASupport.rsql(rsqlFilter));
            fetchSmartTemplate.enrichList(RfetchSupport.compile(rfetchInclude, Cat.class), result);
            return result;
        });
    }


}
