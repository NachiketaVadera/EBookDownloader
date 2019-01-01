package android.nachiketa.ebookdownloader;

import android.vadera.nachiketa.pen_paper.AndroidReadWrite;

import java.util.Random;

class Global {

//    String getRandomQuote() throws IOException {
//        Random random = new Random();
//        StringBuilder builder = new StringBuilder();
//        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("res/raw/quotes.txt");
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            builder.append(line);
//        }
//        int randomNumber = random.nextInt(37);
//        String[] temp = builder.toString().split("~");
//        return temp[randomNumber];
//    }

    String getRandomQuote() {
        String[] quotes = new AndroidReadWrite().loadFromRaw("quotes.txt").split("~");
        return quotes[new Random().nextInt(37)];
    }
}
