package cz.martinmach.svm;

import org.apache.commons.math3.linear.RealVector;

/**
 * Created by mmx on 17.2.17.
 */
public class Option {
    protected RealVector w;
    protected double b;

    public Option(RealVector w, double b) {
        this.w = w;
        this.b = b;
    }

    public RealVector getW() {
        return this.w;
    }

    public double getB(){
        return this.b;
    }
}
