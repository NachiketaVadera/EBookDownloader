package android.nachiketa.ebookdownloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class Global {

    public String getRandomQuote() throws IOException {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("res/raw/quotes.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        int randomNumber = random.nextInt(37);
        String[] temp = builder.toString().split("~");
        return temp[randomNumber];
    }

}
