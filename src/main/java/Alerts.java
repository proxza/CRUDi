import javafx.scene.control.Alert;

public class Alerts {

    public static void showInfoDialog(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.setHeaderText("");
        alert.showAndWait();
    }

    public static void showErrorDialog(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.setHeaderText("");
        alert.showAndWait();
    }
}
