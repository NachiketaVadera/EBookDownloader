package android.nachiketa.ebookdownloader;

import android.app.DownloadManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DownloadActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Global readWrite = null;
    ListView listView = null;
    List<String> linkText = null;
    List<String> links = null;
    ArrayAdapter<String> arrayAdapter;
    public static final String NOT_FOUND_MESSAGE = "Sorry! We were unable to find the book";
    private static final String TAG = "nachiketa@ebooks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        readWrite = new Global();

        String quote = null;
        try {
            quote = new Global().getRandomQuote();
        } catch (IOException e) {
            e.printStackTrace();
        }

        listView = findViewById(R.id.expanded_list);
        links = new ArrayList<>();
        linkText = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, linkText);

        linkText.add("Searching... Read this amazing quote:\n" + quote);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);

        performSearch(getIntent().getStringExtra("searchQuery"), getIntent().getStringExtra("searchBy"));
    }

    private void performSearch(final String query, final String mode) {
        final String searchQuery;
        if (mode.equals("book")) {
            searchQuery = "https://www.google.com/search?q=" + query + "epub+vk";
            new Thread(new Runnable() {

                StringBuilder linkBuilder = new StringBuilder();
                StringBuilder linkTextBuilder = new StringBuilder();

                @Override
                public void run() {
                    try {
                        Document document = Jsoup.connect(searchQuery).get();
                        Elements links = document.select("a[href]");

                        for (Element link : links) {
                            Log.i(TAG, "run: linkText: " + link.text());
                            Log.i(TAG, "run: linkURL: " + link.attr("href"));

                            String key = link.attr("href");
                            if (key.startsWith("https://vk.com/wall")) {
                                Document wallDoc = Jsoup.connect(link.attr("href")).get();
                                Elements wallLinks = wallDoc.select("a[href]");

                                for (Element wallLink : wallLinks) {
                                    Log.i(TAG, "run: pageText: " + wallLink.text());
                                    Log.i(TAG, "run: pageLinks: " + wallLink.attr("href"));
                                    if (wallLink.text().toLowerCase().contains(".epub") || wallLink.text().toLowerCase().contains(".pdf") || wallLink.text().toLowerCase().contains(".mobi") || wallLink.text().toLowerCase().contains(".azw3") && wallLink.text().toLowerCase().contains(searchQuery)) {
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
                            linkText.clear();
                            links.clear();

                            String temp;
                            temp = linkBuilder.toString();
                            Log.i(TAG, "run: temp (links) = " + temp);
                            String[] downloadURLs = temp.split("\n");
                            links.addAll(Arrays.asList(downloadURLs));

                            temp = linkTextBuilder.toString();
                            Log.i(TAG, "run: temp (text) = " + temp);
                            String[] displayTexts = temp.split("\n");
                            linkText.addAll(Arrays.asList(displayTexts));

                            if (linkText.get(0).toLowerCase().contains("searching...")) {
                                linkText.remove(0);
                            }

                            arrayAdapter.notifyDataSetChanged();
                            if (linkText.get(0).equals("")) {
                                linkText.add(NOT_FOUND_MESSAGE);
                                linkText.remove(0);
                            }

                        }
                    });
                }
            }).start();
        } else {
            searchQuery = "https://www.google.co.in/search?q=books+by+" + query;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    final StringBuilder bookListBuilder = new StringBuilder();

                    Document document = null;
                    try {
                        document = Jsoup.connect(searchQuery).get();
                        Elements elements = Objects.requireNonNull(document).select("a[class]");

                        for (Element element : elements) {
                            if (element.attr("class").toLowerCase().equals("klitem")) {
                                bookListBuilder.append(element.attr("title")).append("\n");
                            }
                            Log.i(TAG, "run: Book Titles : \n" + bookListBuilder.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final String[] temp = bookListBuilder.toString().split("\n");
                            linkText.addAll(Arrays.asList(temp));
                            linkText.remove(0);
                            arrayAdapter.notifyDataSetChanged();

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Log.i(TAG, "onItemClick: ITEM TO SEARCH : \n" + linkText.get(position));
                                    performSearch(linkText.get(position), "book");
                                    Toast toast = Toast.makeText(DownloadActivity.this, "Searching...", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            });
                        }
                    });
                }
            }).start();
        }
        Log.i(TAG, "performSearch: searchQuery=" + searchQuery);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String downloadURL = links.get(position);
        String file = linkText.get(position);

        Log.i(TAG, "onItemClick: downloadURL : \n" + downloadURL);
        Log.i(TAG, "onItemClick: file : \n" + file);

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

            if (readWrite.saveToExternalDir("eBooks", "history.txt", file + "\n")) {
                Log.i(TAG, "onItemClick: History saved successfully");
            } else {
                Log.e(TAG, "onItemClick: Error while saving history");
                Toast.makeText(this, "Could not save history", Toast.LENGTH_SHORT).show();
            }

            view.setBackgroundColor(Color.GREEN);

        }
    }
}

// TODO : Add libgen as a new source
// TODO : Search only by book or author name
// TODO : Post on reddit
// TODO : Add tags
// TODO : Optimize code
// TODO : Improve Log tags