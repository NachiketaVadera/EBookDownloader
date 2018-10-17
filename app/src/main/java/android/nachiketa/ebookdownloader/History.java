package android.nachiketa.ebookdownloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class History extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView textView = (TextView) findViewById(R.id.tvHistory);
        textView.setText(new Global().loadFromExternalDir("eBooks", "history.txt"));
    }
}
