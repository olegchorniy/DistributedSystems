package kpi.ipt.labs.distributed.computions.calculator.local;

import java.io.Serializable;

public interface IntegralCalculator extends Serializable {

    double calculate(RealFunction function, double from, double to);
}
