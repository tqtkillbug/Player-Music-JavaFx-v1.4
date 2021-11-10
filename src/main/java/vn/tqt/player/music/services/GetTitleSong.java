package vn.tqt.player.music.services;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class GetTitleSong {
    public static String get(int indexSong, ArrayList<String> list) {
        String titleSong;
        try {
            String fileLocation = list.get(indexSong);
            InputStream input = new FileInputStream(fileLocation);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();
            titleSong = metadata.get("title");
        } catch (Exception e) {
            e.printStackTrace();
            titleSong = " !Error Song";
        }
        return titleSong;
    }
}
