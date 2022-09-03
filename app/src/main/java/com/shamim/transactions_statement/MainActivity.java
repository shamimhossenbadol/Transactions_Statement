package com.shamim.transactions_statement;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private List<Transactions> transactionsList;
    private List<Payments> paymentsArrayList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TransactionsAdapter adapter;
    private TextInputEditText numberField, tra_amountField, pay_amountField;
    private int number, tra_amount, pay_amount, total_tra_amount, total_pay_amount, total_tra_id, total_pay_id;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" Transactions Statement");
            getSupportActionBar().setLogo(R.drawable.ic_taka_sign);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.purple_500)));
        }

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getStatements();
    }

    @SuppressLint("SetTextI18n")
    private void getStatements() {
        total_tra_amount = 0;
        total_pay_amount = 0;
        total_tra_id = 0;
        total_pay_id = 0;
        transactionsList = new ArrayList<>();
        paymentsArrayList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("transactions").orderBy("id", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        transactionsList.add(snapshot.toObject(Transactions.class));
                        transactionsList.sort((o1, o2) -> 0);
                        Map<String, Object> map = snapshot.getData();
                        if (map != null) {
                            try {
                                total_tra_id++;
                                total_tra_amount = total_tra_amount + Integer.parseInt(Objects.requireNonNull(map.get("amount")).toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    FirebaseFirestore.getInstance().collection("payments").orderBy("id", Query.Direction.DESCENDING)
                            .get().addOnSuccessListener(queryPaymentSnapshots -> {
                                for (DocumentSnapshot snapshot : queryPaymentSnapshots) {
                                    paymentsArrayList.add(snapshot.toObject(Payments.class));
                                    Map<String, Object> payments_data = snapshot.getData();
                                    if (payments_data != null) {
                                        try {
                                            total_pay_id++;
                                            total_pay_amount = total_pay_amount + Integer.parseInt(Objects.requireNonNull(payments_data.get("amount")).toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    TextView transactions = findViewById(R.id.transactions);
                                    TextView payments = findViewById(R.id.payments);
                                    TextView due = findViewById(R.id.due);
                                    transactions.setText(new DecimalFormat("#,##,###").format(total_tra_amount));
                                    payments.setText(new DecimalFormat("#,##,###").format(total_pay_amount));
                                    int due_amount = total_tra_amount - total_pay_amount;
                                    due.setText(new DecimalFormat("#,##,###").format(due_amount));
                                    findViewById(R.id.pay).setOnClickListener(v -> {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                        View pay_view = getLayoutInflater().inflate(R.layout.all_payments, null);
                                        builder.setView(pay_view);
                                        RecyclerView recyclerView = pay_view.findViewById(R.id.pay_recyclerview);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                        recyclerView.setAdapter(new PaymentAdapter(paymentsArrayList));
                                        builder.create().show();
                                    });
                                }
                            });
                    adapter = new TransactionsAdapter(transactionsList);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_new) {
            AlertDialog.Builder tra_builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.add_transaction, null);
            tra_builder.setView(view);
            AlertDialog tra_alertDialog = tra_builder.create();
            numberField = view.findViewById(R.id.number);
            tra_amountField = view.findViewById(R.id.amount);
            view.findViewById(R.id.submit).setOnClickListener(v1 -> {
                String number_text = Objects.requireNonNull(numberField.getText()).toString();
                String amount_text = Objects.requireNonNull(tra_amountField.getText()).toString();
                if (!number_text.isEmpty() && !amount_text.isEmpty()) {
                    try {
                        number = Integer.parseInt(number_text);
                        tra_amount = Integer.parseInt(amount_text);
                        int new_tra_id = total_tra_id + 1;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.getDefault());
                        String date = dateFormat.format(Calendar.getInstance().getTime());
                        String time = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault()).format(new Date());
                        FirebaseFirestore.getInstance().collection("transactions").document()
                                .set(new Transactions(new_tra_id, number, tra_amount, date, time)).addOnSuccessListener(unused -> {
                                    tra_alertDialog.dismiss();
                                    getStatements();
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            tra_alertDialog.show();
        }
        if (item.getItemId() == R.id.add_payment) {
            AlertDialog.Builder pay_builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.add_payment, null);
            pay_builder.setView(view);
            AlertDialog pay_alertDialog = pay_builder.create();
            pay_amountField = view.findViewById(R.id.pay_amount);
            view.findViewById(R.id.submit).setOnClickListener(v1 -> {
                String amount_text = Objects.requireNonNull(pay_amountField.getText()).toString();
                if (!amount_text.isEmpty()) {
                    try {
                        pay_amount = Integer.parseInt(amount_text);
                        int new_pay_id = total_pay_id + 1;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String date = dateFormat.format(Calendar.getInstance().getTime());
                        FirebaseFirestore.getInstance().collection("payments").document()
                                .set(new Payments(new_pay_id, date, pay_amount)).addOnSuccessListener(unused -> {
                                    pay_alertDialog.dismiss();
                                    getStatements();
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            pay_alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> implements Filterable {
        private final List<Transactions> transactionsList;
        private List<Transactions> mDataFiltered;

        public TransactionsAdapter(List<Transactions> transactionsList) {
            this.transactionsList = transactionsList;
            this.mDataFiltered = transactionsList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.id.setText(mDataFiltered.get(position).getDate() + "   |   " + mDataFiltered.get(position).getTime());
            holder.date.setText(mDataFiltered.get(position).getDate().toString().substring(0, 2));
            holder.cardView.setOnClickListener(v -> {
                if (holder.id.getVisibility() == View.VISIBLE) {
                    holder.id.setVisibility(View.GONE);
                } else {
                    holder.id.setVisibility(View.VISIBLE);
                }
            });
            holder.number.setText("+880" + mDataFiltered.get(position).getNumber());
            String amount = new DecimalFormat("#,##,###").format(mDataFiltered.get(position).getAmount());
            holder.amount.setText(amount);
        }

        @Override
        public int getItemCount() {
            return mDataFiltered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (constraint.toString().isEmpty()) {
                        mDataFiltered = transactionsList;
                    } else {
                        List<Transactions> filteredTransactions = new ArrayList<>();
                        for (Transactions newRow : mDataFiltered) {
                            if (newRow.getNumber() == Integer.parseInt(constraint.toString())) {
                                filteredTransactions.add(newRow);
                            }
                        }
                        mDataFiltered = filteredTransactions;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filterResults;
                    filterResults.count = mDataFiltered.size();
                    return filterResults;
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView id;
            private final TextView date;
            private final TextView number;
            private final TextView amount;
            private final MaterialCardView cardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                id = itemView.findViewById(R.id.id);
                date = itemView.findViewById(R.id.date);
                number = itemView.findViewById(R.id.number);
                amount = itemView.findViewById(R.id.amount);
                cardView = itemView.findViewById(R.id.card);
            }
        }
    }

    public static class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder> {
        private final List<Payments> paymentsArrayList;

        public PaymentAdapter(List<Payments> paymentsArrayList) {
            this.paymentsArrayList = paymentsArrayList;
        }

        @NonNull
        @Override
        public PaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PaymentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.payment, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PaymentHolder holder, int position) {
            holder.date.setText(paymentsArrayList.get(position).getDate().toString().substring(0, 2));
            holder.full_date.setText(paymentsArrayList.get(position).getDate().toString());
            String amount = new DecimalFormat("#,##,###").format(paymentsArrayList.get(position).getAmount());
            holder.amount.setText(amount);
        }

        @Override
        public int getItemCount() {
            return paymentsArrayList.size();
        }

        public static class PaymentHolder extends RecyclerView.ViewHolder {
            private final TextView date;
            private final TextView full_date;
            private final TextView amount;

            public PaymentHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                full_date = itemView.findViewById(R.id.date_full);
                amount = itemView.findViewById(R.id.amount);
            }
        }
    }
}