package android.nachiketa.ebookdownloader;

import android.app.DownloadManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DownloadActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView = null;
    List<String> linkText = null;
    List<String> links = null;
    ArrayAdapter<String> arrayAdapter;
    public static final String NOT_FOUND_MESSAGE = "Sorry! We were unable to find the book";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        String quote = "Kindly wait";
        Random random = new Random();
        int randomNumber = random.nextInt(19);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("quotes.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            String data = total.toString();
            String[] temp = data.split("~");
            quote = temp[randomNumber];

        } catch (IOException e) {
            e.printStackTrace();
        }


        listView = (ListView) findViewById(R.id.expanded_list);
        links = new ArrayList<>();
        linkText = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, linkText);

        linkText.add("Searching... Read this amazing quote:\n" + quote);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);

        performSearch(getIntent().getStringExtra("bookName"), getIntent().getStringExtra("author"));
    }

    private void performSearch(final String bookName, final String author) {
        String baseURL = "https://www.google.com/search?q=";
        String postFix = " epub vk";
        String space = " ";
        final String searchURL = baseURL + bookName + space + author + postFix;
        Log.i("qwerty", searchURL);

        new Thread(new Runnable() {

            StringBuilder linkBuilder = new StringBuilder();
            StringBuilder linkTextBuilder = new StringBuilder();

            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(searchURL).get();
                    Elements links = document.select("a[href]");

                    for (Element link : links) {
                        Log.i("(linkText)", link.text());
                        Log.i("(linkURL)", link.attr("href"));

                        String key = link.attr("href");
                        if (key.startsWith("https://vk.com/wall")) {
                            Document wallDoc = Jsoup.connect(link.attr("href")).get();
                            Elements wallLinks = wallDoc.select("a[href]");

                            for (Element wallLink : wallLinks) {
                                Log.i("(wallt)", link.text());
                                Log.i("(wallu)", link.attr("href"));
                                if (wallLink.text().toLowerCase().contains(".epub") || wallLink.text().toLowerCase().contains(".pdf") || wallLink.text().toLowerCase().contains(".mobi") || wallLink.text().toLowerCase().contains(".azw3") && wallLink.text().toLowerCase().contains(bookName)) {
                                    linkBuilder.append("https://vk.com").append(wallLink.attr("href")).append("\n");
                                    linkTextBuilder.append(wallLink.text()).append("\n");
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    Log.e("qwerty", e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String temp;
                        temp = linkBuilder.toString();
                        Log.i("listlnk", temp);
                        String[] downloadURLs = temp.split("\n");
                        links.addAll(Arrays.asList(downloadURLs));

                        temp = linkTextBuilder.toString();
                        Log.i("listtxt", temp);
                        String[] displayTexts = temp.split("\n");
                        linkText.addAll(Arrays.asList(displayTexts));
                        linkText.remove(0);

                        arrayAdapter.notifyDataSetChanged();
                        if (linkText.get(0).equals("")) {
                            linkText.add(NOT_FOUND_MESSAGE);
                            linkText.remove(0);
                        }

                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String downloadURL = links.get(position);
        String file = linkText.get(position);

        view.setBackgroundColor(Color.GREEN);

        if (file.equals(NOT_FOUND_MESSAGE)) {
            Toast.makeText(this, "Try searching for another book!", Toast.LENGTH_SHORT).show();
        }
        else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.allowScanningByMediaScanner();
            request.setDescription("eBooks : Happy reading!");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,file);
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Objects.requireNonNull(downloadManager).enqueue(request);
            Toast.makeText(DownloadActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
        }
    }
}

