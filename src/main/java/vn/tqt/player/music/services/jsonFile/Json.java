package vn.tqt.player.music.services.jsonFile;

import org.json.simple.parser.ParseException;

import java.io.*;

public class Json {
  public static void writeFile(String jsonString, String filePath) throws IOException {
      File jsonFile = new File(filePath);
      FileWriter jsonWriter = new FileWriter(jsonFile);
      jsonWriter.write(String.valueOf(jsonString));
      jsonWriter.close();
  }
    public static String readFile(String filePath) throws IOException, ParseException {
//        JSONParser parser = new JSONParser();
//        Object obj = parser.parse(new FileReader(filePath));
//        JSONArray array = (JSONArray) obj;
//        for (int i = 0; i < array.size(); i++) {
//            JSONObject jsonObject = (JSONObject) array.get(i);
//            String key = (String) jsonObject.get("key");
//            String email = (String) jsonObject.get("email");
//            KeyData newKey = new KeyData(key,email);
//            list.add(newKey);
//        }
        File fileReader  =  new File(filePath);
        FileReader reader = new FileReader(fileReader);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String datareader = bufferedReader.readLine();
        return datareader;
    }
}
