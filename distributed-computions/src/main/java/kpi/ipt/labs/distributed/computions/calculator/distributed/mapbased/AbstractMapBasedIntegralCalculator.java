package kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import kpi.ipt.labs.distributed.computions.calculator.distributed.DistributedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;

import java.io.Serializable;
import java.util.UUID;

abstract public class AbstractMapBasedIntegralCalculator implements DistributedIntegralCalculator {

    // @formatter:off
    private static final Serializable STUB_VALUE = new Serializable() {};
    // @formatter:on

    private final HazelcastInstance hazelcast;

    public AbstractMapBasedIntegralCalculator(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    @Override
    public double calculateSync(RealFunction function, double from, double to) {
        /* 1. Prepare distributed map containing all sub-intervals to calculate integral on. */
        IMap<Interval, Object> tasksMap = prepareTasksMap(from, to);

        /* 2. Perform calculations over node-local entries and aggregate results. */
        try {
            return calculateAndAggregateResults(function, tasksMap);
        } finally {
            tasksMap.destroy();
        }
    }

    protected abstract double calculateAndAggregateResults(RealFunction function, IMap<Interval, Object> tasksMap);

    private IMap<Interval, Object> prepareTasksMap(double from, double to) {
        String mapName = UUID.randomUUID().toString();
        IMap<Interval, Object> tasksMap = hazelcast.getMap(mapName);

        double leftBound = from;
        double rightBound = Math.min(to, leftBound + INTERVAL_PER_NODE);

        while (leftBound < to) {
            tasksMap.put(new Interval(leftBound, rightBound), STUB_VALUE);

            leftBound = rightBound;
            rightBound = Math.min(to, leftBound + INTERVAL_PER_NODE);
        }

        return tasksMap;
    }
}
