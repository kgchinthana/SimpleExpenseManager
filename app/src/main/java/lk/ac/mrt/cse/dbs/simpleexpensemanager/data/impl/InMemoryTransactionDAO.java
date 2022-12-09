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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class InMemoryTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    private static final String DB_NAME = "200093M";
    private static final String TABLE_NAME = "TransactionDetails";
    private static final String ACCOUNT_NO = "accountNo";
    private static final String DATE = "date";
    private static final String EXPENSE_TYPE = "expenseType";
    private static final String AMOUNT = "amount";
    private static final int DB_VERSION = 1;
    private static final Context context = null;

    private final ;

    /*public InMemoryTransactionDAO() {
        transactions = new LinkedList<>();
    }*/
    public InMemoryTransactionDAO{
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query =" Create Table "+ TABLE_NAME+ "("+ACCOUNT_NO+ "TEXT primary key," +DATE+ "TEXT,"+ EXPENSE_TYPE+ "TEXT,"+ AMOUNT +"NUMERIC )";

        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop Table if exists " + TABLE_NAME);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DATE, String.valueOf(date));
        values.put(ACCOUNT_NO,accountNo);
        values.put(EXPENSE_TYPE, String.valueOf(expenseType));
        values.put(AMOUNT, amount);

        database.insert(TABLE_NAME, null, values);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        SQLiteDatabase dataBase2 = this.getReadableDatabase();

        Cursor cursor2 = dataBase2.rawQuery(" SELECT * FROM " + TABLE_NAME, null);


        List<Transaction> transactions = new LinkedList<>();


        if (cursor2.moveToFirst()) {
            do {
                transactions.add(new Transaction(new Date(cursor2.getString(1)),cursor2.getString(2),cursor2.getString(3),cursor2.getDouble(4)));
            } while (cursor2.moveToNext());


        }
        return transactions;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase dataBase2 = this.getReadableDatabase();

        Cursor cursor2 = dataBase2.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        int size = cursor2.getCount();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
