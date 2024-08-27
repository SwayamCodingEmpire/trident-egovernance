package com.trident.egovernance.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvService {
    private final String CSV_URL = "https://docs.google.com/spreadsheets/d/1nOYjhqsAeITYeRVxuMEDeUPogAETJk-lCuzx-eWZ0a8/pub?gid=0&single=true&output=csv";
    public List<String[]> fetchAndFilterCsvData(String filterValue) throws Exception {
        URL url = new URL(CSV_URL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (Reader reader = new InputStreamReader(connection.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> allData = csvReader.readAll();
            // Assuming the first row contains headers
            String[] headers = allData.get(0);
            int jobTitleIndex = findIndex(headers, "Job Title");

            return allData.stream()
                    .skip(1) // Skip header row
                    .filter(row -> row.length > jobTitleIndex && row[jobTitleIndex].equalsIgnoreCase(filterValue))
                    .collect(Collectors.toList());
        } catch (CsvException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
    }

    private int findIndex(String[] headers, String header) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(header)) {
                return i;
            }
        }
        return -1; // Not found
    }
}
