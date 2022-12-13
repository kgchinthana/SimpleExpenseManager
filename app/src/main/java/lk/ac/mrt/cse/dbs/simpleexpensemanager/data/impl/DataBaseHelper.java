package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "200093M";
    private static final int DB_VERSION = 29;
    private static final String TABLE_NAME_1 = "AccountDetails";
    private static final String TABLE_NAME_2 = "TransactionDetails";
    private static final String ACCOUNT_NO = "accountNo";
    private static final String BANK_NAME = "bankName";
    private static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static final String BALANCE = "balance";
    private static final String DATE = "date";
    private static final String EXPENSE_TYPE = "expenseType";
    private static final String AMOUNT = "amount";

    public DataBaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query1 =" Create Table "+ TABLE_NAME_1+ "("
                +ACCOUNT_NO+ " TEXT primary key , "
                +BANK_NAME+ " TEXT, "
                + ACCOUNT_HOLDER_NAME +" TEXT, "
                + BALANCE+ " NUMERIC  ) ";

        sqLiteDatabase.execSQL(query1);

        String query2 =" Create Table "+ TABLE_NAME_2+ "("
                +ACCOUNT_NO+ " TEXT primary key , "
                +DATE+ " TEXT, "
                + EXPENSE_TYPE+ " TEXT, "
                + AMOUNT +" NUMERIC ) ";

        sqLiteDatabase.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists " + TABLE_NAME_1);
        sqLiteDatabase.execSQL("drop Table if exists " + TABLE_NAME_2);
        onCreate(sqLiteDatabase);
    }

    public void addEntriesAccountDetailTable(Account account){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NO,account.getAccountNo());
        values.put(BANK_NAME,account.getBankName());
        values.put(ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        values.put(BALANCE,account.getBalance());

        database.insert(TABLE_NAME_1, null, values);
        database.close();
    }

    public void addEntriesTransactionDetailsTable(String date, String accountNo, ExpenseType expenseType, double amount){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ACCOUNT_NO,accountNo);
        values.put(DATE, date );
        values.put(EXPENSE_TYPE, String.valueOf(expenseType));
        values.put(AMOUNT, amount);

        database.insert(TABLE_NAME_2, null, values);
        database.close();
    }

    public Cursor sendEntriesAccountDetailTable(){
        SQLiteDatabase dataBase = this.getReadableDatabase();
        Cursor cursor = dataBase.rawQuery(" SELECT * FROM " + TABLE_NAME_1, null);

        return cursor;
    }
    public Cursor sendEntryOfAccountNoOFAccountDetailTable(String accountNo){
        SQLiteDatabase dataBase = this.getReadableDatabase();
        Cursor cursor = dataBase.rawQuery(" SELECT * FROM " + TABLE_NAME_1 + " WHERE "  + ACCOUNT_NO + "==  accountNo",null);
        return cursor;
    }
    public void removeDataBase(String accountNo){
        SQLiteDatabase dataBase = this.getReadableDatabase();

        dataBase.delete(TABLE_NAME_1, ACCOUNT_NO + "=" + accountNo, null);

        dataBase.close();
    }

    public void updateEntriesAccountDetailTable(String accountNo, String bankName, String accountHolderName, double balance) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ACCOUNT_NO,accountNo);
        values.put(BANK_NAME,bankName);
        values.put(ACCOUNT_HOLDER_NAME,accountHolderName);
        values.put(BALANCE,balance);
        database.update(TABLE_NAME_1, values, "accountNo=?", new String[]{accountNo});
        database.close();
    }
    public Cursor sendEntriesTransactionDetailsTable(){
        SQLiteDatabase dataBase = this.getReadableDatabase();
        Cursor cursor1 = dataBase.rawQuery(" SELECT * FROM " +  "TransactionDetails", null);

        return cursor1;
    }
}
