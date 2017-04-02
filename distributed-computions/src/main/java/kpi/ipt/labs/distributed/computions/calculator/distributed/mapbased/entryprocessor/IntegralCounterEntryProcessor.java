package kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.entryprocessor;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.Interval;
import kpi.ipt.labs.distributed.computions.calculator.local.IntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;

import java.io.Serializable;
import java.util.Map;

public class IntegralCounterEntryProcessor implements EntryProcessor<Interval, Object>, Serializable {

    private final IntegralCalculator calculator;
    private final RealFunction function;

    public IntegralCounterEntryProcessor(IntegralCalculator calculator, RealFunction function) {
        this.calculator = calculator;
        this.function = function;
    }

    @Override
    public Double process(Map.Entry<Interval, Object> entry) {
        Interval interval = entry.getKey();
        System.out.println("Process interval: " + interval);

        return calculator.calculate(function, interval.getLeftBound(), interval.getRightBound());
    }

    @Override
    public EntryBackupProcessor<Interval, Object> getBackupProcessor() {
        return null;
    }
}
