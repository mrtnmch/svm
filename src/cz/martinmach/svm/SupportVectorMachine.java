package cz.martinmach.svm;


import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

/**
 * Created by mmx on 8.1.17.
 */
public class SupportVectorMachine {


    private RealVector w;
    private double b;
    private List<RealVector> negative;
    private List<RealVector> positive;

    private final static double[][] TRANSFORMS = {
            {1, 1},
            {-1, 1},
            {-1, -1},
            {1, -1},
    };
    private double max;
    private double min;

    public enum Classification {
        POSITIVE,
        NEGATIVE,
    }

    public SupportVectorMachine() {

    }

    public void train(List<RealVector> negative, List<RealVector> positive) {
        this.negative = negative;
        this.positive = positive;

        HashMap<Double, OptDictItem> optDict = new LinkedHashMap<>();
        List<Double> allData = new ArrayList<Double>();
        List<RealVector> merged = new ArrayList<>(negative);
        merged.addAll(positive);

        this.max = Double.MIN_VALUE;
        this.min = Double.MAX_VALUE;

        for (RealVector vector : merged) {
            double[] array = vector.toArray();
            for (double item : array) {
                allData.add(item);

                if (item > max) {
                    max = item;
                } else if (item < min) {
                    min = item;
                }
            }
        }

        Double[] stepSizes = {
                this.max * 0.1,
                this.max * 0.01,
                this.max * 0.001,
        };

        double bRangeMultiple = 5;
        double bMultiple = 5;
        double latestOptimum = this.max * 10;
        boolean optimized;

        for (double step : stepSizes) {
            RealVector w2 = new ArrayRealVector(new double[]{latestOptimum, latestOptimum});
            optimized = false;

            while (!optimized) {
                List<Double> steps = this.stepper(
                        -1 * (this.max * bRangeMultiple),
                        this.max * bRangeMultiple,
                        step * bMultiple
                );

                for (double s : steps) {
                    for (double[] transformation : TRANSFORMS) {
                        double[] wt = this.arrayScale(transformation, w2.toArray());
                        RealVector wtv = new ArrayRealVector(wt);
                        boolean foundOption = true;

                        for (RealVector xi : negative) {

                            if (!((-1) * wtv.dotProduct(xi) + s >= 1)) {
                                foundOption = false;
                            }
                        }

                        for (RealVector xi : positive) {

                            if (wtv.dotProduct(xi) + s >= 1) {
                                foundOption = false;
                            }
                        }

                        if(foundOption) {
                            OptDictItem item = new OptDictItem();
                            item.b = s;
                            item.wt = wtv;
                            double norm = wtv.getNorm();
                            optDict.put(norm, item);
                        }
                    }
                }

                if(w2.toArray()[0] < 0) {
                    optimized = true;
                    System.out.println("Optimized a step.");
                } else {
                    w2 = this.addToVector(w2, -step);
                }
            }

            OptDictItem choice = this.getChoice(optDict);
            this.w = choice.wt;
            this.b = choice.b;
            latestOptimum = w2.toArray()[0] + step * 2;
        }
    }

    private OptDictItem getChoice(HashMap<Double, OptDictItem> optDict) {
        Double min = new ArrayList<>(optDict.keySet()).get(0);
        for (Double key : optDict.keySet()) {
            if(key < min) {
                min = key;
            }
        }

        return optDict.get(min);
    }

    private RealVector addToVector(RealVector w, double v) {
        double[] array = w.toArray();
        for (int i = 0; i < array.length; i++) {
            array[i] += v;
        }
        return new ArrayRealVector(array);
    }

    protected List<Double> stepper(double from, double to, double step) {
        List<Double> ret = new ArrayList<>();

        for (double i = from; i <= to; i += step) {
            ret.add(i);
        }

        return ret;
    }

    protected double[] arrayScale(double[] array, double[] scale) {
        double[] ret = Arrays.copyOf(array, array.length);
        for (int i = 0; i < ret.length; i++) {
            ret[i] = ret[i] * scale[i];
        }
        return ret;
    }


    public Classification classify(RealVector feature) {
        double result = feature.dotProduct(this.w) + this.b;
        double[] pos = feature.toArray();
        System.out.println("Test: " + pos[0] + ":" + pos[1] + " -> "  + result);
        return result < 0 ? Classification.NEGATIVE : Classification.POSITIVE;
    }

    protected class OptDictItem {
        public RealVector wt;
        public double b;
    }
}
