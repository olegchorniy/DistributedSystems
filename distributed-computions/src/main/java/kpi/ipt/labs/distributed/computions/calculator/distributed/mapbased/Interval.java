package kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased;

import java.io.Serializable;
import java.util.Formatter;

public class Interval implements Serializable {

    private double leftBound;
    private double rightBound;

    public Interval() {
    }

    public Interval(double leftBound, double rightBound) {
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public double getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(double leftBound) {
        this.leftBound = leftBound;
    }

    public double getRightBound() {
        return rightBound;
    }

    public void setRightBound(double rightBound) {
        this.rightBound = rightBound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interval interval = (Interval) o;

        return Double.compare(interval.leftBound, leftBound) == 0 &&
                Double.compare(interval.rightBound, rightBound) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;

        temp = Double.doubleToLongBits(leftBound);
        result = (int) (temp ^ (temp >>> 32));

        temp = Double.doubleToLongBits(rightBound);
        result = 31 * result + (int) (temp ^ (temp >>> 32));

        return result;
    }

    @Override
    public String toString() {
        return new Formatter()
                .format("[%.3f, %.3f]", leftBound, rightBound)
                .toString();
    }
}
