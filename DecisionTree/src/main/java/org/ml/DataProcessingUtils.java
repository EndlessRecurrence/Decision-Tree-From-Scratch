package org.ml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataProcessingUtils {
    private final String filepath;

    public DataProcessingUtils(String filepath) {
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

    public static Pair<List<List<Double>>, List<List<Double>>> splitData(
            List<List<Double>> data, double trainingDataPercentage, double testingDataPercentage) {
        int numberOfTuples = data.size();
        int numberOfTrainingTuples = (int) Math.round((double)numberOfTuples * trainingDataPercentage);
        int numberOfTestingTuples = (int) Math.round((double)numberOfTuples * testingDataPercentage);

        List<List<Double>> trainingData = new ArrayList<>();
        List<List<Double>> testingData = new ArrayList<>();

        for (int index = 0; index < numberOfTrainingTuples; index++)
            trainingData.add(data.get(index));

        for (int index = numberOfTrainingTuples; index < numberOfTrainingTuples + numberOfTestingTuples; index++)
            testingData.add(data.get(index));

        return new Pair<>(trainingData, testingData);
    }

}
