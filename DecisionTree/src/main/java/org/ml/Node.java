package org.ml;

import java.util.List;

public class Node {
    private Double value;
    private List<List<Double>> data;
    private Boolean isLeaf;
    private Node leftNode;
    private Node rightNode;
    private Double giniValue;
    private Integer featureIndex;

    public Node() {
        this.isLeaf = false;
    }

    public Node(Double value, Boolean isLeaf) {
        this.value = value;
        this.isLeaf = isLeaf;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setLeaf(Boolean leaf) {
        isLeaf = leaf;
    }

    public Double getValue() {
        return value;
    }

    public Boolean getLeaf() {
        return isLeaf;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    public Double getGiniValue() {
        return giniValue;
    }

    public void setGiniValue(Double giniValue) {
        this.giniValue = giniValue;
    }

    public Integer getFeatureIndex() {
        return featureIndex;
    }

    public void setFeatureIndex(Integer featureIndex) {
        this.featureIndex = featureIndex;
    }

    public List<List<Double>> getData() {
        return data;
    }

    public void setData(List<List<Double>> data) {
        this.data = data;
    }
}
