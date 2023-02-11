package org.ml;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataProcessingUtils reader = new DataProcessingUtils("../datasets/pima-indians-diabetes.csv");
        List<String> features = reader.getFeatures();
        List<List<Double>> dataset = reader.getData();

        for (String feature: features) {
            System.out.println(feature);
        }

        int numberOfFolds = 5;
        List<Pair<List<List<Double>>, List<List<Double>>>> dataSplit = DataProcessingUtils.splitDataForCrossValidation(dataset, numberOfFolds);

        for (int index = 0; index < numberOfFolds; index++) {
            DecisionTreeClassifier model = new DecisionTreeClassifier(1, 2);
            model.fit(dataSplit.get(index).getFirst(), features);
            model.holdoutEvaluation(dataSplit.get(index).getSecond());
        }

        //model.holdoutEvaluation(dataSplit.getSecond());
    }
}