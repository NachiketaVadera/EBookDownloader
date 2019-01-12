package android.nachiketa.ebookdownloader;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;
import java.util.Random;

import static android.content.ContentValues.TAG;

class Global {

    String getRandomQuote() throws IOException {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = Objects.requireNonNull(this.getClass().getClassLoader()).getResourceAsStream("res/raw/quotes.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        int randomNumber = random.nextInt(37);
        String[] temp = builder.toString().split("~");
        return temp[randomNumber];
    }

    boolean saveToExternalDir(String directoryName, String fileName, String data) {
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/" + directoryName);
        if (!directory.exists()) {
            Log.i(TAG, "saveToExternalDir: Directory " + directoryName + " does not exist");
            if (directory.mkdirs()) {
                Log.i(TAG, "saveToExternalDir: Directory " + directoryName + " created");
            } else {
                Log.i(TAG, "saveToExternalDir: Error while creating " + directoryName);
            }
        } else {
            Log.i(TAG, "saveToExternalDir: Directory found");
        }
        File file = new File(directory, fileName);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(Objects.requireNonNull(fileOutputStream));
            writer.write(data);
            writer.close();
            Log.i(TAG, "saveToExternalDir: Saved " + fileName + " in " + directoryName + " successfully");
            return true;
        } catch (IOException e) {
            Log.i(TAG, "saveToExternalDir: Error while saving " + fileName + " in " + directoryName + "\n" + e.getMessage());
            return false;
        }
    }

    String loadFromExternalDir(String directoryName, String fileName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/" + directoryName);
        File file = new File(directory, fileName);

        StringBuilder builder = new StringBuilder();

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(fileInputStream)));
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                builder.append(data);
            }
            bufferedReader.close();
            Log.i(TAG, "loadFromExternalDir: File " + fileName + " loaded successfully");
            return builder.toString();
        } catch (IOException e) {
            Log.i(TAG, "loadFromExternalDir: Error while loading " + fileName + "\n" + e.getMessage());
            return "Error : " + e.getMessage();
        }
    }
}
