package cz.martinmach.svm;

/**
 * Created by mmx on 17.2.17.
 */
public class PointPair {
    private Double fromX;
    private Double fromY;
    private Double toX;
    private Double toY;

    public PointPair(Double fromX, Double fromY, Double toX, Double toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public Double getFromX() {
        return fromX;
    }

    public Double getFromY() {
        return fromY;
    }

    public Double getToX() {
        return toX;
    }

    public Double getToY() {
        return toY;
    }
}
