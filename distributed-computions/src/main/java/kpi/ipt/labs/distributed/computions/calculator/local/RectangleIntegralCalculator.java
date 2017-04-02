package kpi.ipt.labs.distributed.computions.calculator.local;

public class RectangleIntegralCalculator implements IntegralCalculator {

    private static final double STEP = 1e-5;

    @Override
    public double calculate(RealFunction function, double from, double to) {
        double result = 0;

        for (double x = from; x <= to; x += STEP) {
            result += STEP * function.apply(x);
        }

        return result;
    }
}