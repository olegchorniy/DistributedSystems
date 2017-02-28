package kpi.ipt.labs.distributed.twophasecommit.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {

    private final List<TransactionResource> registeredResources = new ArrayList<>();

    public void enlistResource(TransactionResource resource) {
        this.registeredResources.add(resource);
        resource.begin(generateTransactionId());
    }

    public void delistResource(TransactionResource resource) {
        resource.end();
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
            for (TransactionResource resource : registeredResources) {
                resource.prepare();
            }
        } catch (Exception e) {
            doRollback();
            throw new RuntimeException(e);
        }

        //2. commit
        for (TransactionResource resource : registeredResources) {
            try {
                resource.commit();
            } catch (Exception e) {
                System.out.println("*********** While commit ***********");
                e.printStackTrace();
            }
        }

        //3. close
        for (TransactionResource resource : registeredResources) {
            try {
                resource.close();
            } catch (Exception e) {
                System.out.println("*********** While close ***********");
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
        for (TransactionResource resource : registeredResources) {
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
