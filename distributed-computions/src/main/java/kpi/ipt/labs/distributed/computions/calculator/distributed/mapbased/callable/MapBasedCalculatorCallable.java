package kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.Interval;
import kpi.ipt.labs.distributed.computions.calculator.local.IntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;

public class MapBasedCalculatorCallable implements Callable<Double>, Serializable, HazelcastInstanceAware {

    private HazelcastInstance hazelcastInstance;

    private final IntegralCalculator calculator;
    private final RealFunction function;
    private String tasksMapName;

    public MapBasedCalculatorCallable(
            IntegralCalculator calculator,
            RealFunction function,
            String tasksMapName
    ) {
        this.calculator = calculator;
        this.function = function;
        this.tasksMapName = tasksMapName;
    }

    @Override
    public Double call() throws Exception {
        Member localMember = hazelcastInstance.getCluster().getLocalMember();
        IMap<Interval, Object> map = hazelcastInstance.getMap(tasksMapName);

        Set<Interval> localKeySet = map.localKeySet();

        double localIntegralValue = localKeySet.stream()
                .peek(interval -> System.out.printf("[%s], interval = %s, %n", localMember, interval))
                .mapToDouble(this::integralOnInterval)
                .sum();

        System.out.printf("[%s], integral over %d intervals = %.3f%n",
                localMember,
                localKeySet.size(),
                localIntegralValue
        );

        return localIntegralValue;
    }

    private double integralOnInterval(Interval interval) {
        return calculator.calculate(function, interval.getLeftBound(), interval.getRightBound());
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
