package kpi.ipt.labs.distributed.rabbitmq.common;

public class InMemoryCounter implements MonotonicCounter {

    private int counter;

    public InMemoryCounter() {
        this(1);
    }

    public InMemoryCounter(int initialValue) {
        this.counter = initialValue;
    }

    @Override
    public int getAndIncrement() {
        return counter++;
    }
}
