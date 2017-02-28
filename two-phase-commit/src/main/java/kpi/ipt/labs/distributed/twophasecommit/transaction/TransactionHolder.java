package kpi.ipt.labs.distributed.twophasecommit.transaction;

public class TransactionHolder {

    private static final ThreadLocal<Transaction> currentTransaction = new ThreadLocal<>();

    public static Transaction beginTransaction() {
        Transaction currentTx = new Transaction();
        currentTransaction.set(currentTx);

        return currentTx;
    }

    public static void clearTransaction() {
        currentTransaction.remove();
    }

    public static Transaction currentTransaction() {
        Transaction currentTx = currentTransaction.get();
        if (currentTx == null) {
            throw new IllegalStateException("There is no transaction bound to current thread");
        }

        return currentTx;
    }
}
