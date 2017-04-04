package kpi.ipt.labs.distributed.computions.calculator.distributed;

import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;

public interface DistributedIntegralCalculator {

    double INTERVAL_PER_NODE = 20.0d;

    double calculateSync(RealFunction function, double from, double to);
}
