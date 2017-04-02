package kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.AbstractMapBasedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.Interval;
import kpi.ipt.labs.distributed.computions.calculator.local.IntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;
import kpi.ipt.labs.distributed.computions.calculator.local.RectangleIntegralCalculator;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MapBasedIntegralCalculator extends AbstractMapBasedIntegralCalculator {

    private static final String INTEGRAL_CALCULATOR_EXECUTOR = "integral-calculator-executor";

    private final IExecutorService executorService;

    public MapBasedIntegralCalculator(HazelcastInstance hazelcast) {
        super(hazelcast);
        this.executorService = hazelcast.getExecutorService(INTEGRAL_CALCULATOR_EXECUTOR);
    }

    @Override
    protected double calculateAndAggregateResults(RealFunction function, IMap<Interval, Object> tasksMap) {
         /* Submit task to all members for performing calculations over
         their local key sets and returning aggregated result. */

        IntegralCalculator calculator = new RectangleIntegralCalculator();
        Callable<Double> calculatorCallable = new MapBasedCalculatorCallable(calculator, function, tasksMap.getName());
        Map<Member, Future<Double>> partialResultFutures = executorService.submitToAllMembers(calculatorCallable);

        return partialResultFutures.values()
                .stream()
                .mapToDouble(MapBasedIntegralCalculator::getResult)
                .sum();
    }

    private static <V> V getResult(Future<V> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
