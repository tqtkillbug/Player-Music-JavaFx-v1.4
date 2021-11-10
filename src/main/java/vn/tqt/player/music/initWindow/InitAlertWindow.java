package vn.tqt.player.music.initWindow;

import javafx.scene.control.Alert;

public class InitAlertWindow {
    public static void initAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
