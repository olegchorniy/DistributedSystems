package kpi.ipt.labs.distributed.computions;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import kpi.ipt.labs.distributed.computions.calculator.distributed.DistributedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.distributed.targeted.CallbackTargetedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;

public class ClientNode {
    public static void main(String[] args) {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        DistributedIntegralCalculator[] calculators = {
                new CallbackTargetedIntegralCalculator(instance),
                /*new MemberTargetedIntegralCalculator(instance),*/
                /*new MapBasedIntegralCalculator(instance),*/
                /*new EntryProcessorIntegralCalculator(instance)*/
        };

        RealFunction square = x -> Math.pow(Math.sin(x), 2) + Math.pow(Math.cos(x), 2);
        double leftBound = 0.0;
        double rightBound = 8_000.0;

        for (int i = 0; i < calculators.length; i++) {
            System.out.println("Result " + i + " : " + calculators[i].calculateSync(square, leftBound, rightBound));
        }

        instance.shutdown();
    }
}
