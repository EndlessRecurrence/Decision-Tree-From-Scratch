package org.ml;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        DatasetReader reader = new DatasetReader("../datasets/pima-indians-diabetes.csv");
        List<String> features = reader.getFeatures();
        List<List<Double>> dataset = reader.getData();

        for (String feature: features) {
            System.out.println(feature);
        }

        DecisionTreeClassifier model = new DecisionTreeClassifier(0.2, 10, 5);
        model.fit(dataset, features);
    }
}