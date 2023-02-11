package org.ml;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class DecisionTreeClassifier {
    private List<List<Double>> dataset;
    private List<String> features;
    private Node root;
    private final Integer minimumNumberOfSamples;
    private final Integer maximumDepth;

    public DecisionTreeClassifier(Integer minimumNumberOfSamples, Integer maximumDepth) {
        this.minimumNumberOfSamples = minimumNumberOfSamples;
        this.maximumDepth = maximumDepth;
    }

    public void fit(List<List<Double>> data, List<String> features) {
        this.dataset = data;
        this.features = features;
        this.root = this.createTree(data, 0);
        System.out.println("Fit function finished! The root node is [" + this.features.get(this.root.getFeatureIndex()) + " < " + this.root.getValue() + "] with a Gini impurity of " + this.root.getGiniValue());
        this.traverseTree(this.root, 0);
    }

    private Node createTree(List<List<Double>> data, int depth) {
        Node node = new Node();

        Pair<String, Pair<Double, Double>> feature = getRootFeature(data);

        node.setFeatureIndex(this.features.indexOf(feature.getFirst()));
        node.setGiniValue(feature.getSecond().getFirst());
        node.setValue(feature.getSecond().getSecond());
        node.setData(data);
        Pair<List<List<Double>>, List<List<Double>>> pair = this.getSplit(data, feature.getSecond().getSecond(), this.features.indexOf(feature.getFirst()));

        if (data.size() < this.minimumNumberOfSamples || depth == this.maximumDepth) {
            node.setLeaf(true);
            return node;
        }

        node.setLeftNode(createTree(pair.getFirst(), depth + 1));
        node.setRightNode(createTree(pair.getSecond(), depth + 1));

        return node;
    }

    public void traverseTree(Node root, int depth) {
        if (root.getLeaf()) {
            System.out.println("[" + this.features.get(root.getFeatureIndex()) + " < " + root.getValue() + "]");
            Integer positiveSamples = this.getNumberOfPositiveSamples(root.getData());
            System.out.println("Positive: " + positiveSamples);
            System.out.println("Negative: " + (root.getData().size() - positiveSamples));
        } else {
            System.out.println("[" + this.features.get(root.getFeatureIndex()) + " < " + root.getValue() + "]");
        }

        Node leftNode = root.getLeftNode();
        Node rightNode = root.getRightNode();

        if (leftNode != null)
            traverseTree(root.getLeftNode(), depth + 1);

        if (rightNode != null)
            traverseTree(root.getRightNode(), depth + 1);
    }

    public boolean predict(List<Double> sample) {
        Node current = this.root;

        while (!current.getLeaf()) {
            if (sample.get(current.getFeatureIndex()) < current.getValue()) {
                current = current.getLeftNode();
            } else {
                current = current.getRightNode();
            }
        }

        List<List<Double>> data = current.getData();
        return this.getNumberOfPositiveSamples(data) >= this.getNumberOfNegativeSamples(data);
    }

    public void holdoutEvaluation(List<List<Double>> testingData) {
        double trueNegatives = 0.0, truePositives = 0.0, falseNegatives = 0.0, falsePositives = 0.0;

        for (List<Double> patient : testingData) {
            int outcomeIndex = patient.size() - 1;
            boolean result = this.predict(patient);

            if (patient.get(outcomeIndex) == 1 && result) {
                truePositives++;
            } else if (patient.get(outcomeIndex) == 1 && !result) {
                falseNegatives++;
            } else if (patient.get(outcomeIndex) == 0 && result) {
                falsePositives++;
            } else {
                trueNegatives++;
            }
        }

        System.out.println("TP: " + truePositives);
        System.out.println("TN: " + trueNegatives);
        System.out.println("FP: " + falsePositives);
        System.out.println("FN: " + falseNegatives);
        double accuracy = Metrics.accuracy(truePositives, trueNegatives, falsePositives, falseNegatives);
        System.out.println("Accuracy: " + accuracy);
        System.out.println("Precision: " + Metrics.precision(truePositives, falsePositives));
        System.out.println("Sensitivity/Recall/True Positive Rate: " + Metrics.sensitivity(truePositives, falseNegatives));
        System.out.println("Specificity: " + Metrics.specificity(trueNegatives, falsePositives));
        System.out.println("F1 score: " + Metrics.f1Score(truePositives, falsePositives, falseNegatives));
        System.out.println("Classification error: " + (1 - accuracy));
    }

    public void crossValidation(int numberOfFolds) {

    }

    private Pair<String, Pair<Double, Double>> getRootFeature(List<List<Double>> data) {
        ArrayList<List<Double>> mutableDataset = new ArrayList<>(data);
        Pair<Double, Double> minimumGiniImpurityAndSplitValue = new Pair<>(1.0, 1.0);
        String featureWithLowestGiniImpurity = null;

        for (int featureIndex = 0; featureIndex < features.size() - 1; featureIndex++) {
             int finalFeatureIndex = featureIndex;
             mutableDataset.sort(Comparator.comparing(firstList -> firstList.get(finalFeatureIndex)));

             Pair<Double, Double> giniImpurity = computeGiniImpurityForOneFeature(mutableDataset, featureIndex);

             if (giniImpurity.getFirst() < minimumGiniImpurityAndSplitValue.getFirst()) {
                 minimumGiniImpurityAndSplitValue.setFirst(giniImpurity.getFirst());
                 minimumGiniImpurityAndSplitValue.setSecond(giniImpurity.getSecond());
                 featureWithLowestGiniImpurity = features.get(featureIndex);
             }
        }

        return new Pair<>(featureWithLowestGiniImpurity, minimumGiniImpurityAndSplitValue);
    }

    private Pair<Double, Double> computeGiniImpurityForOneFeature(List<List<Double>> data, Integer featureIndex) {
        List<Double> midpoints = new ArrayList<>();

        for (int index = 0; index < data.size() - 1; index++) {
            midpoints.add((data.get(index).get(featureIndex) + data.get(index + 1).get(featureIndex)) / 2);
        }

        double minimumGiniImpurity = 1.0, splitValue = -1.0;

        for (Double midpoint : midpoints) {
            double giniImpurity = computeGiniImpurityForTheSplitValue(data, midpoint, featureIndex);
            if (giniImpurity == -1)
                continue;

            if (giniImpurity < minimumGiniImpurity) {
                minimumGiniImpurity = giniImpurity;
                splitValue = midpoint;
            }
        }

        return new Pair<>(minimumGiniImpurity, splitValue);
    }

    private Double giniImpurity(Integer positiveSamples, Integer negativeSamples) {
        Double totalSamples = (double) (positiveSamples + negativeSamples);
        Double result = 1 - (positiveSamples/totalSamples) * (positiveSamples/totalSamples) - (negativeSamples/totalSamples) * (negativeSamples/totalSamples);

        return result.isNaN() ? -1 : result;
    }

    private Double totalGiniImpurity(Integer firstLeafSamples, Integer secondLeafSamples, Double giniImpurityLeft, Double giniImpurityRight) {
        Integer totalSamples = firstLeafSamples + secondLeafSamples;
        return ((double) firstLeafSamples / (double) totalSamples) * giniImpurityLeft + ((double) secondLeafSamples / (double) totalSamples) * giniImpurityRight;
    }

    private Pair<List<List<Double>>, List<List<Double>>> getSplit(List<List<Double>> data, double splitValue, Integer featureIndex) {
        List<List<Double>> leftSamples = data.stream()
                .filter(values -> values.get(featureIndex) < splitValue)
                .toList();

        List<List<Double>> rightSamples = data.stream()
                .filter(values -> values.get(featureIndex) >= splitValue)
                .toList();

        return new Pair<>(leftSamples, rightSamples);
    }

    private Double computeGiniImpurityForTheSplitValue(List<List<Double>> data, Double midpoint, Integer featureIndex) {
        Pair<List<List<Double>>, List<List<Double>>> splitData = this.getSplit(data, midpoint, featureIndex);

        Integer leftPositiveSamples = this.getNumberOfPositiveSamples(splitData.getFirst());
        Integer leftNegativeSamples = this.getNumberOfNegativeSamples(splitData.getFirst());
        Integer rightPositiveSamples = this.getNumberOfPositiveSamples(splitData.getSecond());
        Integer rightNegativeSamples = this.getNumberOfNegativeSamples(splitData.getSecond());

        Double giniImpurityLeftLeaf = this.giniImpurity(leftPositiveSamples, leftNegativeSamples);
        Double giniImpurityRightLeaf = this.giniImpurity(rightPositiveSamples, rightNegativeSamples);

        if (giniImpurityLeftLeaf == -1 || giniImpurityRightLeaf == -1)
            return -1.0;

        return this.totalGiniImpurity(
                leftPositiveSamples + leftNegativeSamples,
                rightPositiveSamples + rightNegativeSamples,
                giniImpurityLeftLeaf,
                giniImpurityRightLeaf
        );
    }

    private Integer getNumberOfPositiveSamples(List<List<Double>> samples) {
        int outcomeLabelIndex = this.features.size() - 1;
        return Math.toIntExact(samples.stream()
                .filter(values -> values.get(outcomeLabelIndex) == 1)
                .count());
    }

    private Integer getNumberOfNegativeSamples(List<List<Double>> samples) {
        int outcomeLabelIndex = this.features.size() - 1;
        return Math.toIntExact(samples.stream()
                .filter(values -> values.get(outcomeLabelIndex) == 0)
                .count());
    }
}
