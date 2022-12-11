/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class InMemoryAccountDAO extends SQLiteOpenHelper implements AccountDAO {

    private static final String DB_NAME = "200093M";
    private static final String TABLE_NAME = "AccountDetails";
    private static final String ACCOUNT_NO = "accountNo";
    private static final String BANK_NAME = "bankName";
    private static final String ACCOUNT_HOLDER_NAME = "accountHolderName";
    private static final String BALANCE = "balance";
    private static final int DB_VERSION = 1;


 
    
    public InMemoryAccountDAO(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query =" Create Table "+ TABLE_NAME+ "("
                +ACCOUNT_NO+ "TEXT primary key,"
                +BANK_NAME+ "TEXT,"
                + ACCOUNT_HOLDER_NAME +"TEXT,"
                + BALANCE+ "NUMERIC)";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public List<String> getAccountNumbersList() {

        SQLiteDatabase dataBase1 = this.getReadableDatabase();
        Cursor cursor1 = dataBase1.rawQuery(" SELECT * FROM " + TABLE_NAME, null);
        ArrayList<String> AccountNames = new ArrayList<String>();
        if (cursor1.moveToFirst()) {
            do {
                AccountNames.add(cursor1.getString(1));
            } while (cursor1.moveToNext());
            return AccountNames;
        }
        dataBase1.close();
        return null;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase dataBase2 = this.getReadableDatabase();

        Cursor cursor2 = dataBase2.rawQuery(" SELECT * FROM " + TABLE_NAME, null);

        ArrayList<Account> AccountList = new ArrayList<Account>();
        if (cursor2.moveToFirst()) {
            do {
                AccountList.add(new Account(cursor2.getString(1),
                        cursor2.getString(2),
                        cursor2.getString(3),
                        cursor2.getDouble(4)));
            } while (cursor2.moveToNext());


        }
        dataBase2.close();
        return AccountList;
    }
    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase dataBase2 = this.getReadableDatabase();
        Cursor cursor2 = dataBase2.rawQuery(" SELECT * FROM " + TABLE_NAME + " WHERE  ACCOUNT_NO  ==  accountNo",null);
        if (cursor2.getString(1) != null) {
            return (Account) cursor2;
        }
        dataBase2.close();
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(BANK_NAME,account.getBankName());
        values.put(ACCOUNT_NO,account.getAccountNo());
        values.put(BALANCE,account.getBalance());
        values.put(ACCOUNT_HOLDER_NAME,account.getAccountHolderName());

        database.insert(TABLE_NAME, null, values);
        database.close();

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor2 = database.rawQuery(" SELECT "+ACCOUNT_NO + " FROM " + TABLE_NAME + " WHERE  ACCOUNT_NO  ==  accountNo",null);

        if (cursor2 == null) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        database.delete(TABLE_NAME, ACCOUNT_NO + "=" + accountNo, null);
        database.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor2 = database.rawQuery(" SELECT * FROM " + TABLE_NAME + " WHERE  ACCOUNT_NO  ==  accountNo",null);

        if (cursor2==null) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = (Account) cursor2;
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        ContentValues values = new ContentValues();

        values.put(ACCOUNT_NO,accountNo);
        values.put(BANK_NAME,account.getBankName());
        values.put(ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        values.put(BALANCE,account.getBalance());

        database.insert(TABLE_NAME, null, values);
        database.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists " + TABLE_NAME);
    }
}
