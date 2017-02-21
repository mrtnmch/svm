package cz.martinmach.svm;


import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

/**
 * Created by Martin Mach <me@martinmach.cz> on 8.1.17.
 * SVM implementation (2d, mostly for edu purposes).
 */
public class SupportVectorMachine {
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
        HashMap<Double, Option> options = new LinkedHashMap<>();
        List<RealVector> merged = new ArrayList<>(negative);
        merged.addAll(positive);

        this.max = this.findMax(merged);
        this.min = this.findMin(merged);

        Double[] stepSizes = this.generateStepSizes(precisionSteps, this.max);

        double previousOptimum = this.max * 10;
        boolean isOptimized;

        for (double step : stepSizes) {
            RealVector wTest = new ArrayRealVector(new double[]{previousOptimum, previousOptimum});
            isOptimized = false;

            while (!isOptimized) {
                findOption(negative, positive, options, step, wTest);

                if (wTest.getEntry(0) < 0) {
                    isOptimized = true;
                } else {
                    wTest = this.addToVector(wTest, -step);
                }
            }

            Option minimalOption = this.getMinimalOption(options);
            this.w = minimalOption.getW();
            this.b = minimalOption.getB();
            previousOptimum = this.w.getEntry(0) + step * 2;
        }
    }

    private void findOption(List<RealVector> negative, List<RealVector> positive, HashMap<Double, Option> options, double step, RealVector wTest) {
        List<Double> steps = this.stepper(
                -1 * (this.max * RANGE_MULTIPLE),
                this.max * RANGE_MULTIPLE,
                step * B_MULTIPLE
        );

        for (double singleStep : steps) {
            for (double[] transformation : TRANSFORMS) {
                RealVector wTestScaled = this.vectorScale(wTest, transformation);
                boolean validOption = true;

                for (RealVector neg : negative) {
                    if (!tryFit(wTestScaled, neg, singleStep, -1)) {
                        validOption = false;
                        break;
                    }
                }

                if (validOption) {
                    for (RealVector pos : positive) {
                        if (!tryFit(wTestScaled, pos, singleStep, 1)) {
                            validOption = false;
                            break;
                        }
                    }
                }

                if (validOption) {
                    Option item = new Option(wTestScaled, singleStep);
                    double norm = wTestScaled.getNorm();
                    options.put(norm, item);
                }
            }
        }
    }

    private boolean tryFit(RealVector wTest, RealVector vector, double margin, int scale) {
        return scale * (wTest.dotProduct(vector) + margin) >= 1;
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

    private Option getMinimalOption(HashMap<Double, Option> options) throws SolutionNotFoundException {
        if (options.size() == 0) {
            throw new SolutionNotFoundException();
        }

        Double min = new ArrayList<>(options.keySet()).get(0);
        for (Double key : options.keySet()) {

            if (key < min) {
                min = key;
            }
        }

        return options.get(min);
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
