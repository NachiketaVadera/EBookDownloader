package android.nachiketa.ebookdownloader;

import android.vadera.nachiketa.pen_paper.AndroidReadWrite;

import java.util.Random;

class Global {
    String getRandomQuote() {
        String[] quotes = new AndroidReadWrite().loadFromRaw("quotes.txt").split("~");
        return quotes[new Random().nextInt(37)];
    }
}
