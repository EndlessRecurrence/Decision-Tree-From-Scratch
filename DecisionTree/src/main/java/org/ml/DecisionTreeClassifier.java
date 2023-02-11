package org.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class DecisionTreeClassifier {
    private List<List<Double>> data;
    private List<String> features;
    private Node root;
    private double minimumGiniCriterion;
    private Integer minimumNumberOfSamples;
    private Integer maximumDepth;

    public DecisionTreeClassifier(double minimumGiniCriterion, Integer minimumNumberOfSamples, Integer maximumDepth) {
        this.minimumGiniCriterion = minimumGiniCriterion;
        this.minimumNumberOfSamples = minimumNumberOfSamples;
        this.maximumDepth = maximumDepth;
    }

    public void fit(List<List<Double>> data, List<String> features) {
        this.data = data;
        this.features = features;
        this.root = this.createNode(data);
        this.buildTree(this.root, 0);
        System.out.println("Fit function finished!");
        System.out.println(this.root.getFeatureIndex());
        System.out.println(this.root.getValue());
        System.out.println(this.root.getGiniValue());
    }

    private Node createNode(List<List<Double>> data) {
        Node node = new Node();
        Pair<String, Pair<Double, Double>> feature = getRootFeature(data);
        node.setFeatureIndex(this.features.indexOf(feature.getFirst()));
        node.setGiniValue(feature.getSecond().getFirst());
        node.setValue(feature.getSecond().getSecond());

        return node;
    }

    private void buildTree(Node root, int currentDepth) {
    }

    private Pair<String, Pair<Double, Double>> getRootFeature(List<List<Double>> data) {
        ArrayList<List<Double>> mutableDataset = new ArrayList<>(data);
        Pair<Double, Double> minimumGiniImpurityAndSplitValue = new Pair<>(1.0, 1.0);
        String featureWithLowestGiniImpurity = null;

        for (int featureIndex = 0; featureIndex < features.size() - 1; featureIndex++) {
             int finalFeatureIndex = featureIndex;
             mutableDataset.sort((firstList, secondList) -> firstList.get(finalFeatureIndex).compareTo(secondList.get(finalFeatureIndex)));

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
            double giniImpurity = computeGiniImpurityForTheSplitValue(midpoint, featureIndex);
            System.out.println("Total gini impurity: " + giniImpurity);
            if (giniImpurity == -1)
                continue;

            if (giniImpurity < minimumGiniImpurity) {
                minimumGiniImpurity = giniImpurity;
                splitValue = midpoint;
            }
        }

        System.out.println("For feature no. " + featureIndex.toString() + " and split value " + splitValue + ", we have" +
                "a Gini coefficient of " + minimumGiniImpurity);

        return new Pair<>(minimumGiniImpurity, splitValue);
    }

    private Double giniImpurity(Integer positiveSamples, Integer negativeSamples) {
        Double totalSamples = (double) (positiveSamples + negativeSamples);
        Double result = 1 - (positiveSamples/totalSamples) * (positiveSamples/totalSamples) - (negativeSamples/totalSamples) * (negativeSamples/totalSamples);
        if (result.isNaN()) {
            System.out.println(positiveSamples);
            System.out.println(negativeSamples);
            System.out.println(totalSamples);
        }
        return result.isNaN() ? -1 : result;
    }

    private Double totalGiniImpurity(Integer firstLeafSamples, Integer secondLeafSamples, Double giniImpurityLeft, Double giniImpurityRight) {
        Integer totalSamples = firstLeafSamples + secondLeafSamples;
        return ((double) firstLeafSamples / (double) totalSamples) * giniImpurityLeft + ((double) secondLeafSamples / (double) totalSamples) * giniImpurityRight;
    }

    private Double computeGiniImpurityForTheSplitValue(Double midpoint, Integer featureIndex) {
        List<List<Double>> leftSamples = this.data.stream()
                .filter(values -> values.get(featureIndex) < midpoint)
                .toList();

        List<List<Double>> rightSamples = this.data.stream()
                .filter(values -> values.get(featureIndex) >= midpoint)
                .toList();

        Integer leftPositiveSamples = this.getNumberOfPositiveSamples(leftSamples);
        Integer leftNegativeSamples = this.getNumberOfNegativeSamples(leftSamples);
        Integer rightPositiveSamples = this.getNumberOfPositiveSamples(rightSamples);
        Integer rightNegativeSamples = this.getNumberOfNegativeSamples(rightSamples);

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
