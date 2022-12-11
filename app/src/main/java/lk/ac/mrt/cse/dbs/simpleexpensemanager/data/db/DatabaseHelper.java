package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//DatabaseHelper Class
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "my_db_200040B";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//      SQL Query Statement for creating Accounts Table
        String queryCreateAccountTable =
                "CREATE TABLE IF NOT EXISTS " + DatabaseContract.ACCOUNT_TABLE + " (" +
                        DatabaseContract.COLUMN_ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                        DatabaseContract.COLUMN_BANK_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.COLUMN_ACCOUNT_HOLDER_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.COLUMN_BALANCE + " REAL NOT NULL " +
                        "CHECK(" + DatabaseContract.COLUMN_BALANCE + ">= 0)" +
                        ")";
        db.execSQL(queryCreateAccountTable);
//      SQL Query Statement for creating Transactions Table
        String queryCreateTransactionTable =
                "CREATE TABLE IF NOT EXISTS " + DatabaseContract.TRANSACTION_TABLE + " (" +
                        DatabaseContract.COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.COLUMN_DATE + " TEXT NOT NULL, " +
                        DatabaseContract.COLUMN_ACCOUNT_NO + " TEXT NOT NULL, " +
                        DatabaseContract.COLUMN_EXPENSE_TYPE + " TEXT NOT NULL, " +
                        DatabaseContract.COLUMN_AMOUNT + " REAL NOT NULL, " +
                        "FOREIGN KEY(" + DatabaseContract.COLUMN_ACCOUNT_NO + ") REFERENCES " + DatabaseContract.ACCOUNT_TABLE + "(" + DatabaseContract.COLUMN_ACCOUNT_NO + ")" +
                        ")";

        db.execSQL(queryCreateTransactionTable);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        String queryDeleteAccountTable= "DROP TABLE IF EXISTS "+DatabaseContract.ACCOUNT_TABLE;
        String queryDeleteTransactionsTable= "DROP TABLE IF EXISTS "+DatabaseContract.TRANSACTION_TABLE;

        db.execSQL(queryDeleteAccountTable);
        db.execSQL(queryDeleteTransactionsTable);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
