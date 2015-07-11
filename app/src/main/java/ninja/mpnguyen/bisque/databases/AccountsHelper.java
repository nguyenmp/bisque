package ninja.mpnguyen.bisque.databases;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;

import ninja.mpnguyen.chowders.things.html.Auth;

public class AccountsHelper extends Observable {
    public static AccountsHelper observable = new AccountsHelper();

    public static class State {
        public final Account activeAccount;
        public final Account[] accounts;

        public State(Account activeAccount, Account[] accounts) {
            this.activeAccount = activeAccount;
            this.accounts = accounts;
        }
    }

    private AccountsHelper() {
        super();
    }

    public void notifyChanged() {
        setChanged();
        notifyObservers();
    }

    public static Account[] getAccounts(Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<Account, Integer> dao = databaseHelper.getAccountDao();
            List<Account> accounts = dao.queryForAll();
            databaseHelper.close();
            return accounts.toArray(new Account[accounts.size()]);
        }
    }

    public static Account getAccount(long id, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<Account, Integer> dao = databaseHelper.getAccountDao();
            PreparedQuery<Account> query = dao.queryBuilder()
                    .where().idEq((int) id).prepare();
            Account auth = dao.queryForFirst(query);
            databaseHelper.close();
            return auth;
        }
    }

    public static Account createAccount(Auth auth, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            Account account = new Account(auth);
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<Account, Integer> dao = databaseHelper.getAccountDao();

            int create = dao.create(account);

            observable.notifyChanged();
            databaseHelper.close();
            return create == 1 ? account : null;
        }
    }

    public static boolean updateAccount(Account account, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            Dao<Account, Integer> dao = databaseHelper.getAccountDao();
            int updateRows = dao.update(account);
            observable.notifyChanged();
            databaseHelper.close();
            return updateRows == 1;
        }
    }

    private static final String ACTIVE_ACCOUNT_ID_KEY = "ninja.mpnguyen.bisque.databases.AcountsHelper.ACTIVE_ACCOUNT_ID_KEY";

    public static Account getActiveAccount(Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean accountSelected = preferences.contains(ACTIVE_ACCOUNT_ID_KEY);
            if (accountSelected) {
                int accountID = preferences.getInt(ACTIVE_ACCOUNT_ID_KEY, -1);
                if (accountID == -1) {
                    // Account ID is invalid or value is of wrong type
                    return null;
                } else {
                    // We got the account!
                    return AccountsHelper.getAccount(accountID, context);
                }
            } else {
                // No account was selected, we are anonymous
                return null;
            }
        }
    }

    public static void setActiveAccount(Account account, Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putInt(ACTIVE_ACCOUNT_ID_KEY, account.id).apply();
            observable.notifyChanged();
        }
    }

    public static State getState(Context context) throws SQLException {
        synchronized (DatabaseHelper.dbLock) {
            Account activeAccount = getActiveAccount(context);
            Account[] accounts = getAccounts(context);
            return new State(activeAccount, accounts);
        }
    }

    @DatabaseTable
    public static class Account implements Serializable {

        @DatabaseField(generatedId = true)
        public int id;

        @DatabaseField
        public String email;

        @DatabaseField
        public String username;

        @DatabaseField
        public String cookie;

        public Account() {
            // Do nothing for ORMLite
        }

        public Account(Auth other) {
            this.email = other.email;
            this.username = other.username;
            this.cookie = other.cookie;
        }

        public Account(Account other) {
            this.email = other.email;
            this.username = other.username;
            this.cookie = other.cookie;
        }

        public Account(String email, String username, String cookie) {
            this.email = email;
            this.username = username;
            this.cookie = cookie;
        }
    }
}
