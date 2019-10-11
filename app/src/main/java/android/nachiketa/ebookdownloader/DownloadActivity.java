package android.nachiketa.ebookdownloader;

import android.app.DownloadManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shashank.sony.fancytoastlib.FancyToast;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DownloadActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String NOT_FOUND_MESSAGE = "Sorry! We were unable to find the book";
    private static final String TAG = "ebkdldr";
    private List<String> linkText = null;
    private List<String> links = null;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        String quote = null;
        try {
            quote = new Global().getRandomQuote();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.expanded_list);
        links = new ArrayList<>();
        linkText = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, linkText);

        linkText.add("Searching... Read this amazing quote:\n\n\n" + quote);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);

        performSearch(getIntent().getStringExtra("query"), Objects.requireNonNull(getIntent().getStringExtra("choice")));
    }

    private void performSearch(final String query, final String mode) {
        final String searchQuery;
        if (mode.equals("vk")) {
            searchQuery = "https://www.google.com/search?q=" + query + "epub+vk";
            Thread sourceVK = new Thread(new Runnable() {

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
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    }

                    runOnUiThread(() -> {
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

                        Log.i(TAG, "calling setUI() from libgen");
                        setUI();
                    });
                }
            });
            sourceVK.start();
        } else if (mode.equals("libgen")) {
            searchQuery = "http://gen.lib.rus.ec/search.php?req=" + query;
            Thread sourceLibgen = new Thread(() -> {
                Document resultPage = null;
                try {
                    resultPage = Jsoup.connect(searchQuery).get();
                } catch (IOException e) {
                    Log.i(TAG, "libgen result page not found:\n\n" + e.getMessage());
                    e.printStackTrace();
                }
                Elements resultPageLinks = null;
                Elements resultPageAnchors = null;
                if (resultPage != null) {
                    resultPageLinks = resultPage.select("a[href]");
                    resultPageAnchors = resultPage.select("a[title]");
                } else {
                    Log.i(TAG, "libgen result page is null");
                }
                if (resultPageLinks != null && resultPageAnchors != null) {

                    linkText.clear();
                    links.clear();

                    Log.i(TAG, "libgen: links and linkText lists cleared");

                    Log.i(TAG, "libgen book titles:\n\n");
                    for (Element a :
                            resultPageAnchors) {

                        String title = a.attr("title");

                        if (title.equals("")) {
                            linkText.add(a.text());
                            Log.i(TAG, a.text() + "\n");
                        }
                    }

                    Log.i(TAG, "libgen download links:\n\n");
                    for (Element href :
                            resultPageLinks) {

                        String link = href.attr("href");

                        if (link.contains("ads") || link.contains("md5") && !link.contains("b-ok.cc")
                                && !link.contains("libgen.me") && !link.contains("bookfi.net")
                                && !link.contains("book/index.php?")) {
                            links.add(link);
                            Log.i(TAG, link + "\n");
                        }
                    }
                }
                runOnUiThread(() -> {
                    Log.i(TAG, "calling setUI() from libgen");
                    setUI();
                });
            });
            sourceLibgen.start();
        }
    }

    private void setUI() {
        if (linkText.get(0).toLowerCase().contains("searching...")) {
            linkText.remove(0);
        }

        arrayAdapter.notifyDataSetChanged();
        if (linkText.get(0).equals("")) {
            linkText.add(NOT_FOUND_MESSAGE);
            linkText.remove(0);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        DB historyDB = null;
        try {
            historyDB = DBFactory.open(getApplicationContext(), "history");
        } catch (SnappydbException e) {
            Log.e(TAG, "ERROR while creating history database");
            e.printStackTrace();
        }

        String downloadURL = links.get(position);
        String file = linkText.get(position);

        Log.i(TAG, "onItemClick: downloadURL : \n" + downloadURL);
        Log.i(TAG, "onItemClick: file : \n" + file);

        if (file.equals(NOT_FOUND_MESSAGE)) {
            FancyToast.makeText(this, "Try searching for another book!", Toast.LENGTH_LONG, FancyToast.INFO, false).show();
        } else if (file.contains("Searching...")) {
            FancyToast.makeText(this, "You found the hidden button! Rejoice!", FancyToast.LENGTH_LONG, FancyToast.CONFUSING, false);
        } else {
            if (Objects.equals(getIntent().getStringExtra("choice"), "libgen")) {
                Document downloadPage = null;
                try {
                    downloadPage = Jsoup.connect(downloadURL).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements links = null;
                if (downloadPage != null) {
                    links = downloadPage.select("a[href]");
                }
                if (links != null) {
                    for (Element link :
                            links) {

                        String href = link.attr("href");

                        if (href.contains(".epub") || href.contains(".pdf") || href.contains(".azw3") || href.contains(".mobi")) {
                            downloadURL = link.attr("href");
                            break;
                        }

                    }
                }
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
            request.setDescription("eBooks : Happy reading!");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setTitle("eBooks: Downloading " + file);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file);
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Objects.requireNonNull(downloadManager).enqueue(request);
            // save to database
            try {
                Objects.requireNonNull(historyDB).put(" " + file + " ", file);
            } catch (SnappydbException e) {
                e.printStackTrace();
            }

            Random random = new Random();

            view.setBackgroundColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
    }
}
