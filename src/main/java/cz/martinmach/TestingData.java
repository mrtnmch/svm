package cz.martinmach;

import java.util.List;

/**
 * Created by mmx on 25.1.17.
 */
public class TestingData {
    private List<List<Double>> test;

    public TestingData(List<List<Double>> test) {
        this.test = test;
    }

    public List<List<Double>> getTest() {
        return test;
    }
}
