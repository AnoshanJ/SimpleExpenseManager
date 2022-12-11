package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentExpenseManager extends ExpenseManager {
    private DatabaseHelper databaseHelper;

    public PersistentExpenseManager(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
        setup();
    }

    @Override
    public void setup() {
//       Setup the new persistentAccountDAO and persistentTransactionDAO
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(databaseHelper);
        setAccountsDAO(persistentAccountDAO);

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(databaseHelper);
        setTransactionsDAO(persistentTransactionDAO);

//      Add sample data
        Account dummyAcc1 = new Account("200040B", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcc2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);

        persistentAccountDAO.addAccount(dummyAcc1);
        persistentAccountDAO.addAccount(dummyAcc2);


    }
}
