package cz.martinmach.svm;


import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

/**
 * Created by mmx on 8.1.17.
 */
public class SupportVectorMachine {


    private List<RealVector> positive;
    private List<RealVector> negative;
    private RealVector w;
    private double b;
    private double min;
    private double max;

    private static final double RANGE_MULTIPLE = 5;
    private static final double B_MULTIPLE = 5;
    private final static double[][] TRANSFORMS = {
            {1, 1},
            {-1, 1},
            {-1, -1},
            {1, -1},
    };

    private PointPair getVector(int multiple) {
        double min = this.min * 0.9;
        double max = this.max * 1.1;

        return new PointPair(min, this.hyperplane(min, multiple), max, this.hyperplane(max, multiple));
    }

    public PointPair getMainVector() {
        return this.getVector(0);
    }

    public PointPair getPositiveVector() {
        return this.getVector(1);
    }

    public PointPair getNegativeVector() {
        return this.getVector(-1);
    }

    private double hyperplane(double x, int v) {
        return (-this.w.getEntry(0) * x - this.b + v) / this.w.getEntry(1);
    }

    public void train(List<RealVector> negative, List<RealVector> positive, int precisionSteps) throws SolutionNotFoundException {
        this.negative = negative;
        this.positive = positive;

        HashMap<Double, Option> optDict = new LinkedHashMap<>();
        List<RealVector> merged = new ArrayList<>(negative);
        merged.addAll(positive);

        this.max = this.findMax(merged);
        this.min = this.findMin(merged);

        Double[] stepSizes = this.generateStepSizes(precisionSteps, this.max);

        double latestOptimum = this.max * 10;
        boolean optimized;

        for (double step : stepSizes) {
            RealVector w2 = new ArrayRealVector(new double[]{latestOptimum, latestOptimum});
            optimized = false;

            while (!optimized) {
                findOption(negative, positive, optDict, step, w2);

                if (w2.getEntry(0) < 0) {
                    optimized = true;
                } else {
                    w2 = this.addToVector(w2, -step);
                }
            }

            Option choice = this.getMinimalOption(optDict);
            this.w = choice.getW();
            this.b = choice.getB();
            latestOptimum = this.w.getEntry(0) + step * 2;
        }
    }

    private void findOption(List<RealVector> negative, List<RealVector> positive, HashMap<Double, Option> optDict, double step, RealVector w2) {
        List<Double> steps = this.stepper(
                -1 * (this.max * RANGE_MULTIPLE),
                this.max * RANGE_MULTIPLE,
                step * B_MULTIPLE
        );

        for (double b2 : steps) {
            for (double[] transformation : TRANSFORMS) {
                RealVector wtv = this.vectorScale(w2, transformation);
                boolean foundOption = true;

                for (RealVector xi : negative) {
                    if (!tryFit(wtv, xi, b2, -1)) {
                        foundOption = false;
                        break;
                    }
                }

                if (foundOption) {
                    for (RealVector xi : positive) {
                        if (!tryFit(wtv, xi, b2, 1)) {
                            foundOption = false;
                        }
                    }
                }

                if (foundOption) {
                    Option item = new Option(wtv, b2);
                    double norm = wtv.getNorm();
                    optDict.put(norm, item);
                }
            }
        }
    }

    private boolean tryFit(RealVector wtv, RealVector xi, double margin, int scale) {
        return scale * (wtv.dotProduct(xi) + margin) >= 1;
    }

    private Double[] generateStepSizes(int precisionSteps, double max) {
        Double[] stepSizes = new Double[precisionSteps];

        for (int i = 1; i <= precisionSteps; i++) {
            stepSizes[i - 1] = Math.pow(0.1, i) * max;
        }

        return stepSizes;
    }

    private double findMin(List<RealVector> merged) {
        double min = Double.MAX_VALUE;

        for (RealVector vector : merged) {
            for (int i = 0; i < vector.getDimension(); i++) {
                if (vector.getEntry(i) < min) {
                    min = vector.getEntry(i);
                }
            }
        }

        return min;
    }

    private double findMax(List<RealVector> merged) {
        double max = Double.MIN_VALUE;

        for (RealVector vector : merged) {
            for (int i = 0; i < vector.getDimension(); i++) {
                if (vector.getEntry(i) > max) {
                    max = vector.getEntry(i);
                }
            }
        }

        return max;
    }

    private Option getMinimalOption(HashMap<Double, Option> optDict) throws SolutionNotFoundException {
        if (optDict.size() == 0) {
            throw new SolutionNotFoundException();
        }

        Double min = new ArrayList<>(optDict.keySet()).get(0);
        for (Double key : optDict.keySet()) {

            if (key < min) {
                min = key;
            }
        }

        return optDict.get(min);
    }

    private RealVector addToVector(RealVector w, double v) {
        RealVector ret = new ArrayRealVector(w.getDimension());

        for (int i = 0; i < w.getDimension(); i++) {
            ret.setEntry(i, w.getEntry(i) + v);
        }

        return ret;
    }

    private List<Double> stepper(double from, double to, double step) {
        List<Double> ret = new ArrayList<>();

        for (double i = from; i <= to; i += step) {
            ret.add(i);
        }

        return ret;
    }

    private RealVector vectorScale(RealVector vector, double[] scale) {
        RealVector ret = new ArrayRealVector(vector.getDimension());

        for (int i = 0; i < vector.getDimension(); i++) {
            ret.setEntry(i, vector.getEntry(i) * scale[i]);
        }

        return ret;
    }


    public Classification classify(RealVector feature) {
        double result = feature.dotProduct(this.w) + this.b;
        return result < 0 ? Classification.NEGATIVE : Classification.POSITIVE;
    }
}
