package kpi.ipt.labs.distributed.computions.calculator.distributed.targeted;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import kpi.ipt.labs.distributed.computions.calculator.distributed.DistributedIntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.IntegralCalculator;
import kpi.ipt.labs.distributed.computions.calculator.local.RealFunction;
import kpi.ipt.labs.distributed.computions.calculator.local.RectangleIntegralCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MemberTargetedIntegralCalculator implements DistributedIntegralCalculator {

    private static final String INTEGRAL_CALCULATOR_EXECUTOR = "integral-calculator-executor";
    private static final double INTERVAL_PER_NODE = 1.5;

    private final HazelcastInstance hazelcast;
    private final IExecutorService executorService;
    private final SubmitStrategy submitStrategy;

    public MemberTargetedIntegralCalculator(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
        this.executorService = hazelcast.getExecutorService(INTEGRAL_CALCULATOR_EXECUTOR);
        this.submitStrategy = new MemberSelectorBasedSubmitStrategy(new RoundRobinMemberSelector());
    }

    @Override
    public double calculateSync(RealFunction function, double from, double to) {
        /*
      * 1. data passed into constructor, submit to the owner of random (counter) key
      * 2. data passed into constructor, submit to member chosen in round-robin fashion

        3. create distributed map -> submit to all -> perform calculation over local keySet
        4. EntryProcessor
        */

        IntegralCalculator calculator = new RectangleIntegralCalculator();

        double leftBound = from;
        double rightBound = Math.min(to, leftBound + INTERVAL_PER_NODE);

        ArrayList<Member> members = new ArrayList<>(hazelcast.getCluster().getMembers());
        List<Future<Double>> partialResults = new ArrayList<>();

        while (leftBound < to) {

            Future<Double> resultFuture = submitStrategy.submit(
                    members,
                    executorService,
                    new IntegralCalculatorCallable(calculator, function, leftBound, rightBound)
            );

            partialResults.add(resultFuture);

            leftBound = rightBound;
            rightBound = Math.min(to, leftBound + INTERVAL_PER_NODE);
        }

        return partialResults.stream()
                .mapToDouble(MemberTargetedIntegralCalculator::getResult)
                .sum();
    }

    private static <V> V getResult(Future<V> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /* Submitting strategies */

    private interface SubmitStrategy {

        Future<Double> submit(List<Member> members, IExecutorService executorService, Callable<Double> callable);
    }

    private static class RandomOwnerSubmitStrategy implements SubmitStrategy {

        private final Random random = new Random();

        @Override
        public Future<Double> submit(List<Member> members, IExecutorService executorService, Callable<Double> callable) {
            return executorService.submitToKeyOwner(callable, random.nextInt());
        }
    }

    private interface MemberSelector {
        Member select(List<Member> members);
    }

    private static class RoundRobinMemberSelector implements MemberSelector {

        private int counter = 0;

        @Override
        public Member select(List<Member> members) {
            return members.get(counter++ % members.size());
        }
    }

    private static class SkipLocalMemberSelector implements MemberSelector {

        private final MemberSelector delegate;

        public SkipLocalMemberSelector(MemberSelector delegate) {
            this.delegate = delegate;
        }

        @Override
        public Member select(List<Member> members) {
            if (members.size() == 1 && members.iterator().next().localMember()) {
                throw new IllegalStateException("Cannot skip local members because the only cluster member is local.");
            }

            Member member;

            do {
                member = delegate.select(members);
            } while (member.localMember());

            return member;
        }
    }

    private static class MemberSelectorBasedSubmitStrategy implements SubmitStrategy {

        private final MemberSelector selector;

        private MemberSelectorBasedSubmitStrategy(MemberSelector selector) {
            this.selector = selector;
        }

        @Override
        public Future<Double> submit(List<Member> members, IExecutorService executorService, Callable<Double> callable) {
            return executorService.submitToMember(callable, selector.select(members));
        }
    }
}
