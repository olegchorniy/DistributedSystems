package kpi.ipt.labs.distributed.twophasecommit.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {

    private final List<TransactionalResource> enlistedResources = new ArrayList<>();

    public void enlistResource(TransactionalResource resource) {
        this.enlistedResources.add(resource);
    }

    public void commit() {
        try {
            doCommit();
        } finally {
            TransactionHolder.clearTransaction();
        }
    }

    private void doCommit() {
        //1. prepare
        try {
            for (TransactionalResource resource : enlistedResources) {
                resource.prepare(generateTransactionId());
            }
        } catch (Exception e) {
            doRollback();
            throw new RuntimeException(e);
        }

        //2. commit
        for (TransactionalResource resource : enlistedResources) {
            try {
                resource.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //3. close
        for (TransactionalResource resource : enlistedResources) {
            try {
                resource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void rollback() {
        try {
            doRollback();
        } finally {
            TransactionHolder.clearTransaction();
        }
    }

    private void doRollback() {
        for (TransactionalResource resource : enlistedResources) {
            try {
                resource.rollback();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
