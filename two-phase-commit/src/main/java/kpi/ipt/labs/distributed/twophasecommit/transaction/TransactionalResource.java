package kpi.ipt.labs.distributed.twophasecommit.transaction;

public interface TransactionalResource extends AutoCloseable {

    void prepare(String txId);

    void commit();

    void rollback();
}
