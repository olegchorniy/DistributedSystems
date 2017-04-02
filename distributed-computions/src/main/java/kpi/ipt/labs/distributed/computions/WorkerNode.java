package kpi.ipt.labs.distributed.computions;

import com.hazelcast.core.Hazelcast;

public class WorkerNode {
    public static void main(String[] args) {
        Hazelcast.newHazelcastInstance();
    }
}
