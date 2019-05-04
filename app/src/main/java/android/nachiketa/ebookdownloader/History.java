package android.nachiketa.ebookdownloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.vadera.nachiketa.pen_paper.AndroidReadWrite;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class History extends AppCompatActivity {

    List<String> history = null;
    DB historyDB = null;
    ArrayAdapter<String> arrayAdapter;
    public static final String WAIT_MESSAGE = "Kindly wait for a second!";

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

        history.add(WAIT_MESSAGE);

        fillHistoryList();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history);
        listHistory.setAdapter(arrayAdapter);

        if (history.get(0).equals(WAIT_MESSAGE)) {
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

    }
}
