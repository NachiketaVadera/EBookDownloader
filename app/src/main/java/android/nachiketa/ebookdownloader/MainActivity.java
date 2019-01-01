package android.nachiketa.ebookdownloader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.vadera.nachiketa.pen_paper.AndroidReadWrite;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            new AndroidReadWrite().saveToExternalDir("eBooks", "history.txt", "");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView textView = findViewById(R.id.tvDisplay);
        textView.setText(new Global().getRandomQuote());
    }

    public void execute(View view) {
        EditText etQuery = findViewById(R.id.etQuery);
        RadioButton radBook = findViewById(R.id.radBookName);
        RadioButton radAuthor = findViewById(R.id.radAuthor);

        if (!etQuery.getText().toString().equals("")) {
            String choice;
            if (radBook.isChecked())
                choice = "book";
            else
                choice = "author";
            Intent intent = new Intent(this, DownloadActivity.class);
            intent.putExtra("searchQuery", etQuery.getText().toString());
            intent.putExtra("searchBy", choice);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Woah! You gotta give me something to work with", Toast.LENGTH_LONG).show();
            etQuery.setFocusable(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("History");
        menu.add("Recommendations");
        menu.add("Exit");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "History":
                startActivity(new Intent(getApplicationContext(), History.class));
                break;
            case "Recommendations":
                FancyToast.makeText(this, "Coming Soon!", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

// TODO : Change UI
// TODO : Add menu
// TODO : Optimize Libgen Parsing