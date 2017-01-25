package cz.martinmach;

import java.util.List;

/**
 * Created by mmx on 25.1.17.
 */
public class TrainingData {
    private List<List<Double>> positive;
    private List<List<Double>> negative;

    public TrainingData(List<List<Double>> positive, List<List<Double>> negative){
        this.positive = positive;
        this.negative = negative;
    }

    public List<List<Double>> getPositive() {
        return positive;
    }

    public List<List<Double>> getNegative() {
        return negative;
    }
}
