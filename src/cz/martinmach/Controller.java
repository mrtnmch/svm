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
    private final List<RealVector> test;

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
        this.test = test;


        SupportVectorMachine svm = new SupportVectorMachine();
        svm.train(negative, positive);
        for (RealVector t : test) {
            double[] pos = t.toArray();
            System.out.println("Test: " + pos[0] + ":" + pos[1] + " -> "  + svm.classify(t));
        }
    }

    public List<RealVector> getNegative() {
        return negative;
    }

    public List<RealVector> getPositive() {
        return positive;
    }

    public List<RealVector> getTest() {
        return test;
    }
}
