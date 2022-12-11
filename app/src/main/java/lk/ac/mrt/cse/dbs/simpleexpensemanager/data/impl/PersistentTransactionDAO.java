package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DatabaseContract;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final DatabaseHelper databaseHelper;

    public PersistentTransactionDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());

        String query = "SELECT " + DatabaseContract.COLUMN_BALANCE + " FROM " + DatabaseContract.ACCOUNT_TABLE + " WHERE " + DatabaseContract.COLUMN_ACCOUNT_NO + " =?;";
        Cursor cursor = db.rawQuery(query, new String[]{accountNo});
        if (cursor.moveToFirst()){
            double balance = cursor.getDouble(0);
            cursor.close();
            //Log Transaction if it is an income or possible expense(if sufficient funds are available)
            if (expenseType == ExpenseType.INCOME || balance - amount >= 0){
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.COLUMN_DATE, dateFormat.format(date));
                values.put(DatabaseContract.COLUMN_ACCOUNT_NO, accountNo);
                values.put(DatabaseContract.COLUMN_EXPENSE_TYPE, String.valueOf(expenseType));
                values.put(DatabaseContract.COLUMN_AMOUNT, amount);
                db.insert(DatabaseContract.TRANSACTION_TABLE, null, values);
            }
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.TRANSACTION_TABLE + " ORDER BY " + DatabaseContract.COLUMN_TRANSACTION_ID + " desc" ;
        Cursor cursor = db.rawQuery(query, null);
        return getTransactionList(cursor);
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseContract.TRANSACTION_TABLE + " ORDER BY " + DatabaseContract.COLUMN_TRANSACTION_ID + " desc LIMIT ?";
        Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(limit)});
        return getTransactionList(cursor);
    }
    //method to return what is contained in the cursor as a List of Transactions
    private List<Transaction> getTransactionList(Cursor cursor){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        List<Transaction> transactionList = new ArrayList<>();

        while (cursor.moveToNext()){
            Date date = new Date();
            try {
                date = dateFormat.parse(cursor.getString(1));

            }
            catch(ParseException e ) {
                e.printStackTrace();
            }
            String accountNo = cursor.getString(2);
            ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(3));
            double amount = cursor.getDouble(4);
            Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
            transactionList.add(transaction);
        }
        cursor.close();
        return transactionList;
    }
}


