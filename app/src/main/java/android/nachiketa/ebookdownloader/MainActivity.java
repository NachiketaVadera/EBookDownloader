package android.nachiketa.ebookdownloader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "__envy__";

    private Button btnSearch;
    private Random random;
    private TextView textView;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.btnSearch);
        relativeLayout = findViewById(R.id.relMain);
        random = new Random();
        Log.i(TAG, "onCreate: Executed");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            Log.i(TAG, "onStart: Requested storage permission");
        }

        textView = findViewById(R.id.tvDisplay);
        try {
            textView.setText(new Global().getRandomQuote());
            Log.i(TAG, "onStart: set quote");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "onStart: IO Exception", e);
        }

        btnSearch.setBackgroundColor(Color.rgb(random.nextInt(201),
                random.nextInt(201), random.nextInt(201)));

        relativeLayout.setBackgroundColor(Color.rgb(random.nextInt(101),
                random.nextInt(101), random.nextInt(101)));

        Log.i(TAG, "onStart: Executed");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            try {
                textView.setText(new Global().getRandomQuote());
                Log.i(TAG, "onTouchEvent: set quote");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onTouchEvent: IO Exception", e);
            }
        }
        Log.i(TAG, "onTouchEvent: Executed");
        return super.onTouchEvent(event);
    }

    public void execute(View view) {
        EditText etQuery = findViewById(R.id.etQuery);
        Log.i(TAG, "execute: Query:\n" + etQuery.getText().toString());

        if (!etQuery.getText().toString().equals("")) {
            // String choice;
            // TODO: Add choice
            Intent intent = new Intent(this, DownloadActivity.class);
            intent.putExtra("query", etQuery.getText().toString());
            intent.putExtra("choice", "vk");
            startActivity(intent);
            Log.i(TAG, "execute: Download Activity Launched");
        } else {
            FancyToast.makeText(this, "Don't shoot blanks!", Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
            etQuery.setFocusable(true);
            Log.i(TAG, "execute: Empty Query");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("History");
        menu.add("Recommendations");
        menu.add("Exit");
        Log.i(TAG, "onCreateOptionsMenu: Executed");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "History":
                startActivity(new Intent(getApplicationContext(), History.class));
                break;
            case "Recommendations":
                startActivity(new Intent(this, Recommendations.class));
                break;
            case "Exit":
                System.exit(0);
                break;
        }
        Log.i(TAG, "onOptionsItemSelected: Executed");
        return super.onOptionsItemSelected(item);
    }
}
