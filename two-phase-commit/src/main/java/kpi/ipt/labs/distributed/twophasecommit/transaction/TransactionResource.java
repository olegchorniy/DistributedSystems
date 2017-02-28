package kpi.ipt.labs.distributed.twophasecommit.transaction;

//TODO: add exceptions
public interface TransactionResource extends AutoCloseable {

    void begin(String transactionId);

    void end();

    void prepare();

    void commit();

    void rollback();
}
