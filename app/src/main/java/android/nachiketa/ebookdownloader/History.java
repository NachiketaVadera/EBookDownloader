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
        String oldText = textView.getText().toString();
        String text = oldText + new Global().loadFromExternalDir("eBooks", "history.txt");
        textView.setText(text);
    }
}
