package android.nachiketa.ebookdownloader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
        }

    }

    public void execute(View view) {
        EditText etBookName = (EditText) findViewById(R.id.etBookName);
        EditText etAuthor = (EditText) findViewById(R.id.etAuthor);

        if (!etBookName.getText().toString().equals("")) {
            if (!etAuthor.getText().toString().equals("")) {
                Intent intent = new Intent(this, DownloadActivity.class);
                intent.putExtra("bookName", etBookName.getText().toString());
                intent.putExtra("author", etAuthor.getText().toString());
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Please enter name of the author", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Please enter the book name", Toast.LENGTH_LONG).show();
        }


    }
}