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
public class InMemoryAccountDAO implements AccountDAO {
    private DBHelper dbHelper;

    public InMemoryAccountDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    @Override
    public void addAccount(Account account) {
        dbHelper.addEntriesAccountDetailTable(account);
    }

    @Override
    public List<String> getAccountNumbersList() {
        Cursor cursor = dbHelper.sendEntriesAccountDetailTable();
        ArrayList<String> AccountNames = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                AccountNames.add(cursor.getString(1));
            } while (cursor.moveToNext());
            return AccountNames;
        }
        return null;
    }
    @Override
    public List<Account> getAccountsList() {
        Cursor cursor = dbHelper.sendEntriesAccountDetailTable();

        ArrayList<Account> AccountList = new ArrayList<Account>();
        if (cursor.moveToFirst()) {
            do {
                AccountList.add(new Account(cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)));
            } while (cursor.moveToNext());


        }

        return AccountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = dbHelper.sendEntryOfAccountNoOFAccountDetailTable(accountNo);

        if (cursor != null) {
            return (Account) cursor;
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = dbHelper.sendEntryOfAccountNoOFAccountDetailTable(accountNo);

        if (cursor == null) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        dbHelper.removeDataBase(cursor.getString(1));

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        Cursor cursor = dbHelper.sendEntryOfAccountNoOFAccountDetailTable(accountNo);

        if (cursor==null) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = (Account) cursor;
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        dbHelper.updateEntriesAccountDetailTable(accountNo,account.getBankName(),account.getAccountHolderName(),account.getBalance());


    }


}
