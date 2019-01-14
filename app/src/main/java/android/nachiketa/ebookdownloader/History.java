package android.nachiketa.ebookdownloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.vadera.nachiketa.pen_paper.AndroidReadWrite;
import android.widget.TextView;

public class History extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView textView = (TextView) findViewById(R.id.tvHistory);
        String oldText = textView.getText().toString();
        String text = oldText + new AndroidReadWrite().loadFromExternalDir("eBooks", "history.txt");
        textView.setText(text);
    }
}
