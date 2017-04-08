package kpi.ipt.labs.distributed.rabbitmq.common;

public interface MonotonicCounter {

    int getAndIncrement();
}
