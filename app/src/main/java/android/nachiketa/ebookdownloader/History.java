package android.nachiketa.ebookdownloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class History extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    List<String> history = null;
    DB historyDB = null;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        try {
            historyDB = DBFactory.open(getApplicationContext(), "history");
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

        ListView listHistory = findViewById(R.id.list_history);
        history = new ArrayList<>();

        fillHistoryList();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history);
        listHistory.setAdapter(arrayAdapter);
        listHistory.setOnItemLongClickListener(this);
        listHistory.setOnItemClickListener(this);

        if (history.isEmpty()) {
            showNoHistory();
        }
    }

    private void fillHistoryList() {
        String[] values = null;
        try {
            values = historyDB.findKeys(" ");
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        history.addAll(Arrays.asList(values));
    }

    private void showNoHistory() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText("You have no history,\nJon Snow!");
        dialog.setConfirmText("Go Back");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final String text = history.get(position);

        final SweetAlertDialog confirmDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        confirmDialog.setTitleText("Are you sure you want to delete?");
        confirmDialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                try {
                    historyDB.del(text);
                    confirmDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    confirmDialog.setTitleText("Deleted!");
                    confirmDialog.setContentText("The history you deleted is now history!");
                    confirmDialog.showCancelButton(false);
                    arrayAdapter.notifyDataSetChanged();
                    confirmDialog.setConfirmButton("Okay", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            confirmDialog.dismissWithAnimation();
                        }
                    });
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
            }
        });
        confirmDialog.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                confirmDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                confirmDialog.setTitleText("Aborted");
                confirmDialog.setContentText("Your file is safe from the void that is deletion");
                confirmDialog.showCancelButton(false);
                confirmDialog.setConfirmButton("Okay", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        confirmDialog.dismissWithAnimation();
                    }
                });
            }
        });
        confirmDialog.show();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String book = history.get(position).trim();
        final SweetAlertDialog sureDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
            sureDialog.setTitleText("Search?")
                    .setContentText("Do you want to search for this book again?")
                    .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sureDialog.dismissWithAnimation();
                        startActivity(new Intent(getApplicationContext(), DownloadActivity.class).putExtra("searchQuery", book).putExtra("searchBy", "book"));
                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sureDialog.dismissWithAnimation();
                    }
                });
            sureDialog.show();
    }

    // options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Delete All History");
        menu.add("Exit");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Delete All History":
                deleteAllHistory();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllHistory() {
        final SweetAlertDialog confirmDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        confirmDialog.setTitleText("Delete All History?");
        confirmDialog.setConfirmButton("I'm sure", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                try {
                    historyDB.destroy();
                    confirmDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    confirmDialog.setTitleText("Deleted!");
                    confirmDialog.setContentText("This won't be traced back to you! ;)");
                    confirmDialog.showCancelButton(false);
                    arrayAdapter.notifyDataSetChanged();
                    confirmDialog.setConfirmButton("Take Me Back", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            confirmDialog.dismissWithAnimation();
                        }
                    });
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
            }
        });
        confirmDialog.setCancelButton("Nooooo", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                confirmDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                confirmDialog.setTitleText("Aborted");
                confirmDialog.setContentText("All hail the saviour of download history!");
                confirmDialog.showCancelButton(false);
                confirmDialog.setConfirmButton("Okay", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        confirmDialog.dismissWithAnimation();
                    }
                });
            }
        });
        confirmDialog.show();
    }
}
