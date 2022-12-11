package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DatabaseContract;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final DatabaseHelper databaseHelper;

    public PersistentAccountDAO(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query = "SELECT " + DatabaseContract.COLUMN_ACCOUNT_NO + " FROM " + DatabaseContract.ACCOUNT_TABLE;

        //Cursor object to make connection for executing SQL queries
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            accountNumbers.add(cursor.getString(0));
        }
        cursor.close();
        return accountNumbers;    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseContract.ACCOUNT_TABLE;

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            String accountNo = cursor.getString(0);
            String bankName = cursor.getString(1);
            String accountHolderName = cursor.getString(2);
            double balance = cursor.getDouble(3);

            Account account = new Account(accountNo, bankName, accountHolderName, balance);
            accountList.add(account);
        }
        cursor.close();
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
//        Read an account
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseContract.ACCOUNT_TABLE + " WHERE " + DatabaseContract.COLUMN_ACCOUNT_NO + " =?;";
        Cursor cursor = db.rawQuery(query, new String[]{accountNo});
        if (cursor.moveToFirst()){
            Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
            cursor.close();
            return account;
        } else {
            cursor.close();
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

    }

    @Override
    public void addAccount(Account account) {
        //Add the account to the database
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.COLUMN_ACCOUNT_NO, account.getAccountNo());
        values.put(DatabaseContract.COLUMN_BANK_NAME, account.getBankName());
        values.put(DatabaseContract.COLUMN_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        values.put(DatabaseContract.COLUMN_BALANCE, account.getBalance());
        db.insert(DatabaseContract.ACCOUNT_TABLE, null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long delete = db.delete(DatabaseContract.ACCOUNT_TABLE,DatabaseContract.COLUMN_ACCOUNT_NO + " = ?", new String[]{accountNo});
        if (delete == -1) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Account account = this.getAccount(accountNo);

        double updatedBalance;
        ContentValues values = new ContentValues();
        switch (expenseType){
            case EXPENSE:
                if (account.getBalance() - amount < 0){
                    throw new InvalidAccountException("Insufficient Balance. Available balance is " + account.getBalance());
                } else {
                    updatedBalance = account.getBalance() - amount;
                    values.put(DatabaseContract.COLUMN_BALANCE, updatedBalance);
                    long updateExpense = db.update(DatabaseContract.ACCOUNT_TABLE, values,DatabaseContract.COLUMN_ACCOUNT_NO + " = ?", new String[]{accountNo});
                    if (updateExpense == 0) {
                        throw new InvalidAccountException("Database Error");
                    }
                }
                break;

            case INCOME:
                updatedBalance = account.getBalance() + amount;
                values.put(DatabaseContract.COLUMN_BALANCE, updatedBalance);
                long updateIncome = db.update(DatabaseContract.ACCOUNT_TABLE, values,DatabaseContract.COLUMN_ACCOUNT_NO + " = ?", new String[]{accountNo});

                if (updateIncome == 0) {
                    throw new InvalidAccountException("Database Error");
                }
                break;
        }

    }
}

