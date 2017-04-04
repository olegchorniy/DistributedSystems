package kpi.ipt.labs.distributed.computions.calculator.distributed.targeted;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import kpi.ipt.labs.distributed.computions.calculator.distributed.DistributedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.Interval;
import kpi.ipt.labs.distributed.computions.calculator.local.IntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;
import kpi.ipt.labs.distributed.computions.calculator.local.RectangleIntegralCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public class CallbackTargetedIntegralCalculator implements DistributedIntegralCalculator {

    private static final String INTEGRAL_CALCULATOR_EXECUTOR = "integral-calculator-executor";
    private static final double INTERVAL_PER_NODE = 1.5;

    private final IExecutorService executorService;

    public CallbackTargetedIntegralCalculator(HazelcastInstance hazelcast) {
        this.executorService = hazelcast.getExecutorService(INTEGRAL_CALCULATOR_EXECUTOR);
    }

    @Override
    public double calculateSync(RealFunction function, double from, double to) {
        IntegralCalculator calculator = new RectangleIntegralCalculator();

        List<Interval> intervals = splitToIntervals(from, to);

        DoubleAdder integralSum = new DoubleAdder();
        LongAdder failsCounter = new LongAdder();

        CountDownLatch latch = new CountDownLatch(intervals.size());

        for (Interval interval : intervals) {

            Callable<Double> callable = new IntegralCalculatorCallable(calculator, function,
                    interval.getLeftBound(), interval.getRightBound());

            executorService.submitToKeyOwner(callable, interval, new ExecutionCallback<Double>() {
                @Override
                public void onResponse(Double response) {

                    System.out.println("[Callback based] Partial result received: [" + response + "]");

                    integralSum.add(response);
                    latch.countDown();
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    failsCounter.increment();
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (failsCounter.sum() != 0L) {
            throw new IllegalStateException("Errors occurred during integral calculation");
        }

        return integralSum.sum();
    }

    private List<Interval> splitToIntervals(double from, double to) {
        List<Interval> intervals = new ArrayList<>();

        double leftBound = from;
        double rightBound = Math.min(to, leftBound + INTERVAL_PER_NODE);

        while (leftBound < to) {

            intervals.add(new Interval(leftBound, rightBound));

            leftBound = rightBound;
            rightBound = Math.min(to, leftBound + INTERVAL_PER_NODE);
        }

        return intervals;
    }
}
