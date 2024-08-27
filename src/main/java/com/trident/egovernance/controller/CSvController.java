package com.trident.egovernance.controller;

import com.trident.egovernance.service.CsvService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/new-api")
public class CSvController {
    private final CsvService csvService;

    public CSvController(CsvService csvService) {
        this.csvService = csvService;
    }

    public List<String[]> getFilteredData(@PathVariable String filterValue) throws Exception {
        try{
            return csvService.fetchAndFilterCsvData(filterValue);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error while fetching data");
        }

    }
}
