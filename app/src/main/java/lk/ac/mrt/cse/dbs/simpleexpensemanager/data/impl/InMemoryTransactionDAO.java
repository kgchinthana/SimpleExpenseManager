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

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.DateFormat;
//import android.icu.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class InMemoryTransactionDAO implements TransactionDAO {

    private static final String pattern = "yyyy-MM-dd";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private DataBaseHelper dbHelper;



    public InMemoryTransactionDAO(Context context){
        dbHelper= new DataBaseHelper(context);

    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String date1 = simpleDateFormat.format(date);
        dbHelper.addEntriesTransactionDetailsTable( date1, accountNo, expenseType,amount);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        List<Transaction> transactions = new ArrayList<>();
        Cursor cursor = dbHelper.sendEntriesTransactionDetailsTable();


        if (cursor.moveToFirst() && cursor != null) {
            do {

                try {
                    Date date =simpleDateFormat.parse(cursor.getString(0));
                    transactions.add(new Transaction(date,cursor.getString(1),ExpenseType.valueOf(cursor.getString(2)),cursor.getDouble(3)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());


        }
        cursor.close();
        return transactions;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        List<Transaction> transactionsNew = getAllTransactionLogs();


        int size = transactionsNew.size();
        if (size <= limit) {

            return transactionsNew;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactionsNew.subList(size-limit, size);
    }

}
