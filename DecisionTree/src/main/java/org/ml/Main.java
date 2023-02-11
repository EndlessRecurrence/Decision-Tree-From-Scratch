package org.ml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataProcessingUtils reader = new DataProcessingUtils("../datasets/pima-indians-diabetes.csv");
        List<String> features = reader.getFeatures();
        List<List<Double>> dataset = reader.getData();

        for (String feature: features) {
            System.out.println(feature);
        }

        int numberOfFolds = 10;
        List<Pair<List<List<Double>>, List<List<Double>>>> dataSplit = DataProcessingUtils.splitDataForCrossValidation(dataset, numberOfFolds);
        List<Double> averageMetrics = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

        for (int index = 0; index < numberOfFolds; index++) {
            System.out.println("=======================================================================");
            System.out.println("Results for test fold " + index + ": ");
            DecisionTreeClassifier model = new DecisionTreeClassifier(1, 2);
            model.fit(dataSplit.get(index).getFirst(), features);
            List<Double> evaluationMetrics = model.holdoutEvaluation(dataSplit.get(index).getSecond());

            for (int metricIndex = 0; metricIndex < averageMetrics.size(); metricIndex++)
                averageMetrics.set(metricIndex, averageMetrics.get(metricIndex) + evaluationMetrics.get(metricIndex));

            System.out.println("Accuracy: " + evaluationMetrics.get(0));
            System.out.println("Precision: " + evaluationMetrics.get(1));
            System.out.println("Sensitivity/Recall/True Positive Rate: " + evaluationMetrics.get(2));
            System.out.println("Specificity: " + evaluationMetrics.get(3));
            System.out.println("F1 score: " + evaluationMetrics.get(4));
            System.out.println("Classification error: " + evaluationMetrics.get(5));
            System.out.println("=======================================================================");
        }

        double meanAccuracy = averageMetrics.get(0) / (double)numberOfFolds;
        double meanClassificationError = averageMetrics.get(5) / (double)numberOfFolds;

        System.out.println("Mean accuracy: " + meanAccuracy);
        System.out.println("Mean precision: " + (averageMetrics.get(1) / (double)numberOfFolds));
        System.out.println("Mean sensitivity/recall/TPR: " + (averageMetrics.get(2) / (double)numberOfFolds));
        System.out.println("Mean specificity: " + (averageMetrics.get(3) / (double)numberOfFolds));
        System.out.println("Mean F1 score: " + (averageMetrics.get(4) / (double)numberOfFolds));
        System.out.println("Mean classification error: " + meanClassificationError);

        // https://machinelearningmastery.com/confidence-intervals-for-machine-learning/
        // Classification accuracy or classification error is a proportion or a ratio. It describes the proportion of
        // correct or incorrect predictions made by the model. Each prediction is a binary decision that could be
        // correct or incorrect. Technically, this is called a Bernoulli trial, named for Jacob Bernoulli. The proportions
        // in a Bernoulli trial have a specific distribution called a binomial distribution. Thankfully, with large sample
        // sizes (e.g. more than 30), we can approximate the distribution with a Gaussian. Therefore, we use the
        // binomial proportion confidence interval formula to compute a 95% confidence interval for the model's accuracy.

        double numberOfSamplesInOneFold = (dataset.size() / (double) numberOfFolds);
        double offsetFor90 = 1.64 * Math.sqrt((meanAccuracy * (1 - meanAccuracy)) / numberOfSamplesInOneFold);
        double offsetFor95 = 1.96 * Math.sqrt((meanAccuracy * (1 - meanAccuracy)) / numberOfSamplesInOneFold);
        double offsetFor98 = 2.33 * Math.sqrt((meanAccuracy * (1 - meanAccuracy)) / numberOfSamplesInOneFold);
        double offsetFor99 = 2.58 * Math.sqrt((meanAccuracy * (1 - meanAccuracy)) / numberOfSamplesInOneFold);

        System.out.println("Confidence intervals for the mean accuracy of the model:");
        System.out.println("90% confidence level: [" + (meanAccuracy - offsetFor90) + "," + (meanAccuracy + offsetFor90) + "]");
        System.out.println("95% confidence level: [" + (meanAccuracy - offsetFor95) + "," + (meanAccuracy + offsetFor95) + "]");
        System.out.println("98% confidence level: [" + (meanAccuracy - offsetFor98) + "," + (meanAccuracy + offsetFor98) + "]");
        System.out.println("99% confidence level: [" + (meanAccuracy - offsetFor99) + "," + (meanAccuracy + offsetFor99) + "]");
    }
}