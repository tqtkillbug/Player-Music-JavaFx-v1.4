package vn.tqt.player.music.services.loginservice;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.Locale;

public class RandomKeyGenerated {
   public static String randomString(){
       return RandomStringUtils.random(7, true, false).toUpperCase(Locale.ROOT);
   }
}
