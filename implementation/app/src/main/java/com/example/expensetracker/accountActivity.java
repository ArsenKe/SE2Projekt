package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class accountActivity extends AppCompatActivity implements IDataBase {

private String accountName;
private TextView accountOutput;
private TextView balanceOutput;
private DBHelper mydb;
private ArrayList<Account> accountData;
private ArrayList<Transaction> transactionData;
private Integer deleteID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();
        accountName = intent.getStringExtra("accountName");

        mydb = new DBHelper(accountActivity.this);
        readDB();

        accountOutput = (TextView) findViewById(R.id.accountNameView);
        balanceOutput = (TextView) findViewById(R.id.totalBudgetTextView);
        getAccountData();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewTransactions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        CustomAdapterTransaction customAdapter = new CustomAdapterTransaction(transactionData);
        recyclerView.setAdapter(customAdapter);

    }

    public void saveData(String accountName, String transType, String category, Double value) {
        mydb.addTransaction(accountName, transType, category, value);
        getAccountData();
    }

    public void updateData(String accountType, String newAccountType, double value) {
        mydb.updateAccount(accountType, newAccountType, value);
        getAccountData();
    }

    public void deleteData(Integer transID) {
        mydb.deleteTransaction(transID);
        getAccountData();
    }

    public void addTransaction(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Add Transaction");
        alertDialogBuilder.setMessage("Enter transaction:");
        alertDialogBuilder.setCancelable(false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        List<String> s = new ArrayList<String>();
        for(String str : mydb.getAllCategories()) {
            s.add(str);
        }
        final ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s);

        List<String> t = new ArrayList<String>();
        t.add("Income");
        t.add("Expense");
        final ArrayAdapter<String> adp2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, t);

        final Spinner transTypes = new Spinner(this);
        transTypes.setAdapter(adp2);
        layout.addView(transTypes);

        final Spinner categories = new Spinner(this);
        categories.setAdapter(adp);
        layout.addView(categories);

        final EditText inputValue = new EditText(this);
        inputValue.setHint("Value");
        layout.addView(inputValue);

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String transType = transTypes.getSelectedItem().toString();
                String category = categories.getSelectedItem().toString();
                String value = inputValue.getText().toString();
                saveData(accountName, transType, category, Double.parseDouble(value));
                Intent intent = new Intent(getApplicationContext(), accountActivity.class);
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"You clicked on Cancel",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void deleteTransaction(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        if(deleteID != null) {
            alertDialogBuilder.setTitle("Delete Transaction");
            alertDialogBuilder.setMessage("Are you sure?");
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    deleteData(deleteID);
                    Intent intent = new Intent(getApplicationContext(), accountActivity.class);
                    startActivity(intent);
                }
            });

            alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(),"You clicked on Cancel",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            alertDialogBuilder.setTitle("No Transaction Selected");
            alertDialogBuilder.setMessage("Select Transaction");
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateTransaction(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Account Name");
        alertDialogBuilder.setMessage("Enter value:");
        alertDialogBuilder.setCancelable(false);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        List<String> s = new ArrayList<String>();

        ArrayList<Account> accountData = getAccountData();
        for(Account a : accountData) {
            s.add(a.getAccountType());
        }

        final ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, s);

        final Spinner input = new Spinner(this);
        input.setAdapter(adp);
        layout.addView(input);

        final EditText nameChanged = new EditText(this);
        nameChanged.setHint("Change Account Name");
        layout.addView(nameChanged);

        final EditText valueInput = new EditText(this);
        valueInput.setHint("Change Value");
        layout.addView(valueInput);

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                   String accountType = input.getSelectedItem().toString();
                   String newAccountType = nameChanged.getText().toString();
                   String inputValue = valueInput.getText().toString();

                   updateData(accountType, newAccountType, Double.parseDouble(inputValue));
                Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"You clicked on Cancel",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void readDB() {
        accountData = mydb.getAllAccounts();
        transactionData = mydb.getAllTransactions();
    }

    public ArrayList<Account> getAccountData() {
        ArrayList<Account> accountData = mydb.getAllAccounts();
        double balance = 0;
        Account account = new Account("test", 0.0);

        for(Account a : accountData) {
            balance += a.getValue();
            if(a.getAccountType().equals(accountName)) account = a;
        }

        Double accountValue = account.getValue();

        balanceOutput.setText("Account balance: " + accountValue);
        balanceOutput.setFocusable(false);
        balanceOutput.setClickable(false);

        accountOutput.setText(accountName);
        accountOutput.setFocusable(false);
        accountOutput.setClickable(false);

        return accountData;
    }

    public void backHome(View view) {
        Intent intent = new Intent(accountActivity.this, HomeFragment.class);
        startActivity(intent);
    }

    public void getItemForDelete(View view) {
        TextView transID;

        transID = (TextView) view.findViewById(R.id.transID);

        deleteID = Integer.parseInt(transID.getText().toString());

    }

}