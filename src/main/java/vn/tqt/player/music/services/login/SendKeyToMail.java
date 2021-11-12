package vn.tqt.player.music.services.login;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

public class SendKeyToMail {
    public static void send(String email, String key) throws MessagingException, UnsupportedEncodingException {
       // Tai khoan nay la demo khong co co van de gi
        final String fromEmail = "tqtplayer@gmail.com";
        final String password = "vzuteoukojadodlw";
        String toEmail = email;
        final String subject = "TQT PLAYER KEY";
        final String body = "Hello Đây Là Key Sử Dụng App TQT Player\nVui Lòng Không Chia Sẻ Cho Bất Cứ Ai! \nKey: " + key + "\nADMIN: TienTran" ;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        MimeMessage msg = new MimeMessage(session);
        //set message headers
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        msg.setFrom(new InternetAddress(fromEmail, "TQT Player Admin"));
        msg.setReplyTo(InternetAddress.parse(fromEmail, false));
        msg.setSubject(subject, "UTF-8");
        msg.setText(body, "UTF-8");
        msg.setSentDate(new Date());
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        Transport.send(msg);
    }
}

