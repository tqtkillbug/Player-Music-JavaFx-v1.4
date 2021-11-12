package vn.tqt.player.music.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;
import vn.tqt.player.music.PlayerApp;
import vn.tqt.player.music.initWindow.InitAlertWindow;
import vn.tqt.player.music.repository.KeyData;
import vn.tqt.player.music.utils.JacksonParser;
import vn.tqt.player.music.services.login.RandomKeyGenerated;
import vn.tqt.player.music.services.login.SendKeyToMail;
import vn.tqt.player.music.services.login.VadidateEmail;
import vn.tqt.player.music.utils.TextFileUtil;

import javax.mail.MessagingException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button demonode;
    @FXML
    private Label alertText;
    @FXML
    private TextField loginKeyField;
    public List<KeyData> listKeyAndMail = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginKeyField.setPromptText("Enter Your Key To Login");
        alertText.setTextFill(Color.web("#FF0000"));
        String dataReader = TextFileUtil.read("data/keymail.json");
        listKeyAndMail = JacksonParser.INSTANCE.toList(dataReader, KeyData.class);
    }

    public void login(ActionEvent event) throws IOException {
        String input = loginKeyField.getText();
        if (checkKey(input) || checkEmail(input)) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(PlayerApp.class.getResource("player-view.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.show();
        } else {
            String titleAlert = "Login Fail";
            String contentAlert = "Key incorrect please re-enter or enter email to receive Key";
            InitAlertWindow.initAlert(titleAlert, contentAlert);
            alertText.setText("Key does not exist,Please enter your email to receive Key");
        }
    }

    public void sendKeyToEmail() throws MessagingException, IOException {
        String key = RandomKeyGenerated.randomString();
        String email = loginKeyField.getText();
        VadidateEmail emailValid = new VadidateEmail();
        if (emailValid.validate(email)) {
            if (checkEmail(email)) {
                String titleAlert = "Login Alert";
                String contentAlert = "Email already exists on the system. Please enter your key";
                InitAlertWindow.initAlert(titleAlert, contentAlert);
            } else {
                while (checkKey(key)) {
                    key = RandomKeyGenerated.randomString();
                }
                SendKeyToMail.send(email, key);
                KeyData newKey = new KeyData(key, email);
                listKeyAndMail.add(newKey);
                String jsonString = JacksonParser.INSTANCE.toJson(listKeyAndMail);
                TextFileUtil.writeFile(jsonString,"data/keymail.json" );
                String titleAlert = "Login Alert";
                String contentAlert = "Please check your email and get your key";
                InitAlertWindow.initAlert(titleAlert, contentAlert);
            }
        } else {
            String titleAlert = "Player alert";
            String contentAlert = "Invalid email";
            InitAlertWindow.initAlert(titleAlert, contentAlert);
        }
    }


    public void showHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(PlayerApp.class.getResource("player-view.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    public boolean checkKey(String key) {
        for (int i = 0; i < listKeyAndMail.size(); i++) {
            if (listKeyAndMail.get(i).getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkEmail(String email) {
        for (int i = 0; i < listKeyAndMail.size(); i++) {
            if (listKeyAndMail.get(i).getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public void demo(ActionEvent event) {
    }
}
