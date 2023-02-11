package org.ml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatasetReader {
    private final String filepath;

    public DatasetReader(String filepath) {
        this.filepath = filepath;
    }

    public List<String> getFeatures() {
        List<String> features = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(this.filepath))) {
            String line;
            if ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                features = Arrays.asList(values);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return features;
    }

    public List<List<Double>> getData() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(this.filepath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this.convertToDoubles(records);
    }

    private List<List<Double>> convertToDoubles(List<List<String>> data) {
        return data.stream()
                .map(listOfStrings -> listOfStrings.stream().map(Double::parseDouble).collect(Collectors.toList()))
                .toList();
    }
}
