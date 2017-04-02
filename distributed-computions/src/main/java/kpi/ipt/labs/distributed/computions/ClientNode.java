package kpi.ipt.labs.distributed.computions;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import kpi.ipt.labs.distributed.computions.calculator.distributed.DistributedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.callable.MapBasedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.distributed.mapbased.entryprocessor.EntryProcessorIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.distributed.targeted.CallbackTargetedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.distributed.targeted.MemberTargetedIntegralCalculator;

public class ClientNode {
    public static void main(String[] args) {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        DistributedIntegralCalculator[] calculators = {
                new CallbackTargetedIntegralCalculator(instance),
                new MemberTargetedIntegralCalculator(instance),
                new MapBasedIntegralCalculator(instance),
                new EntryProcessorIntegralCalculator(instance)
        };

        for (int i = 0; i < calculators.length; i++) {
            System.out.println("Result " + i + " : " + calculators[i].calculateSync(x -> x * x, 0, 30));
        }

        instance.shutdown();
    }
}
