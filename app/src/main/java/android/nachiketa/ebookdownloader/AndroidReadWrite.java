package android.nachiketa.ebookdownloader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class AndroidReadWrite {

    public static String readFile(String path) {
        StringBuilder builder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new FileReader(path));
            while (scanner.hasNext()) {
                builder.append(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

}
