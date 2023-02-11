package org.ml;

public class Metrics {
    public static double accuracy(double truePositives, double trueNegatives, double falsePositives, double falseNegatives) {
        return (truePositives+trueNegatives)/(truePositives+trueNegatives+falsePositives+falseNegatives);
    }

    public static double precision(double truePositives, double falsePositives) {
        return truePositives/(truePositives+falsePositives);
    }

    public static double sensitivity(double truePositives, double falseNegatives) {
        return truePositives/(truePositives+falseNegatives);
    }

    public static double specificity(double trueNegatives, double falsePositives) {
        return trueNegatives/(trueNegatives+falsePositives);
    }

    public static double f1Score(double truePositives, double falsePositives, double falseNegatives) {
        return 2*truePositives/(2*truePositives+falsePositives+falseNegatives);
    }

    public static double errorMetric(double truePositives, double trueNegatives, double falsePositives, double falseNegatives) {
        return (falsePositives+falseNegatives)/(truePositives+trueNegatives+falsePositives+falseNegatives);
    }
}
