package android.nachiketa.ebookdownloader;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.vadera.nachiketa.pen_paper.AndroidReadWrite;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class History extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

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

        if (history.isEmpty()) {
            showNoHistory();
        }
    }

    private void fillHistoryList() {
        String[] values = null;
        try {
            values = historyDB.findKeys("eBooks_");
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
}
