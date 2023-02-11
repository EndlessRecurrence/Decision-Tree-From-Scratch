package org.ml;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        DataProcessingUtils reader = new DataProcessingUtils("../datasets/pima-indians-diabetes.csv");
        List<String> features = reader.getFeatures();
        List<List<Double>> dataset = reader.getData();

        for (String feature: features) {
            System.out.println(feature);
        }

        Pair<List<List<Double>>, List<List<Double>>> dataSplit = DataProcessingUtils.splitData(dataset, 0.9, 0.1);

        DecisionTreeClassifier model = new DecisionTreeClassifier(10, 3);
        model.fit(dataSplit.getFirst(), features);
        model.holdoutEvaluation(dataSplit.getSecond());
    }
}