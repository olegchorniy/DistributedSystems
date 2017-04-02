package kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.entryprocessor;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.AbstractMapBasedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.Interval;
import kpi.ipt.labs.distributed.computions.calculator.local.IntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;
import kpi.ipt.labs.distributed.computions.calculator.local.RectangleIntegralCalculator;

import java.util.Map;

public class EntryProcessorIntegralCalculator extends AbstractMapBasedIntegralCalculator {

    public EntryProcessorIntegralCalculator(HazelcastInstance hazelcast) {
        super(hazelcast);
    }

    @Override
    protected double calculateAndAggregateResults(RealFunction function, IMap<Interval, Object> tasksMap) {
        /* Use entry processor to calculate partial integrals on each node. */

        IntegralCalculator calculator = new RectangleIntegralCalculator();
        IntegralCounterEntryProcessor integralCalculateProcessor = new IntegralCounterEntryProcessor(calculator, function);

        Map<Interval, Object> partialResults = tasksMap.executeOnEntries(integralCalculateProcessor);

        return partialResults.values()
                .stream()
                .mapToDouble(EntryProcessorIntegralCalculator::cast)
                .sum();
    }

    private static double cast(Object object) {
        return (double) object;
    }
}
