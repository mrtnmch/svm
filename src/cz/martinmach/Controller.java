package cz.martinmach;

import cz.martinmach.svm.SupportVectorMachine;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final List<RealVector> negative;
    private final List<RealVector> positive;
    private final List<RealVector> testNegative = new ArrayList<>();
    private final List<RealVector> testPositive = new ArrayList<>();
    private final List<SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>>> supportVectors;

    public Controller() {
        List<RealVector> negative = new ArrayList<RealVector>() {{
            add(new ArrayRealVector(new double[]{1, 7}));
            add(new ArrayRealVector(new double[]{2, 8}));
            add(new ArrayRealVector(new double[]{3, 7}));
        }};

        List<RealVector> positive = new ArrayList<RealVector>() {{
            add(new ArrayRealVector(new double[]{5, 1}));
            add(new ArrayRealVector(new double[]{6, -1}));
            add(new ArrayRealVector(new double[]{7, 3}));
        }};

        List<RealVector> test = new ArrayList<RealVector>() {{
            add(new ArrayRealVector(new double[]{0, 10}));
            add(new ArrayRealVector(new double[]{1, 3}));
            add(new ArrayRealVector(new double[]{3, 4}));
            add(new ArrayRealVector(new double[]{3, 5}));
            add(new ArrayRealVector(new double[]{5, 5}));
            add(new ArrayRealVector(new double[]{5, 6}));
            add(new ArrayRealVector(new double[]{6, -5}));
            add(new ArrayRealVector(new double[]{5, 8}));
            add(new ArrayRealVector(new double[]{5, 2}));
            add(new ArrayRealVector(new double[]{5, -2}));
        }};

        this.negative = negative;
        this.positive = positive;

        SupportVectorMachine svm = new SupportVectorMachine();
        svm.train(negative, positive);
        this.supportVectors = svm.getSupportVectors();
        for (RealVector t : test) {
            SupportVectorMachine.Classification classification = svm.classify(t);
            if(classification.equals(SupportVectorMachine.Classification.POSITIVE)) {
                this.testPositive.add(t);
            } else {
                this.testNegative.add(t);
            }
        }
    }

    public List<RealVector> getNegative() {
        return negative;
    }

    public List<RealVector> getPositive() {
        return positive;
    }

    public List<RealVector> getTestNegative() {
        return testNegative;
    }

    public List<RealVector> getTestPositive() {
        return testPositive;
    }

    public List<SupportVectorMachine.Tuple<SupportVectorMachine.Tuple<Double>>> getSupportVectors() {
        return this.supportVectors;
    }
}
