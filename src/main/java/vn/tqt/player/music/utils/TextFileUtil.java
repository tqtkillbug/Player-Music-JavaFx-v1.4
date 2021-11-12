
package vn.tqt.player.music.utils;

import org.json.simple.parser.ParseException;

import java.io.*;

public class TextFileUtil {
    public static void writeFile(String jsonString, String filePath) {
        try {
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static String read(String filePath) {
        try {
            File fileReader = new File(filePath);
            FileReader reader = new FileReader(fileReader);
            BufferedReader bufferedReader = new BufferedReader(reader);
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

    }
}
