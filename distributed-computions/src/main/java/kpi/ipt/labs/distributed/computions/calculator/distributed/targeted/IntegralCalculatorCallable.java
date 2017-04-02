package kpi.ipt.labs.distributed.computions.calculator.distributed.targeted;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import kpi.ipt.labs.distributed.computions.calculator.local.IntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class IntegralCalculatorCallable implements Callable<Double>, Serializable, HazelcastInstanceAware {

    private final IntegralCalculator calculator;
    private final RealFunction function;
    private final double from;
    private final double to;
    private HazelcastInstance hazelcastInstance;

    public IntegralCalculatorCallable(
            IntegralCalculator calculator,
            RealFunction function,
            double from, double to
    ) {
        this.calculator = calculator;
        this.function = function;
        this.from = from;
        this.to = to;
    }

    @Override
    public Double call() throws Exception {
        double integralValue = calculator.calculate(function, from, to);

        System.out.printf("[%s], integral from %.3f to %.3f = %.3f%n",
                hazelcastInstance.getCluster().getLocalMember(),
                from, to, integralValue);

        return integralValue;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
