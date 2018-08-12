import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private String baseFolder = "";

    @FXML
    private Button btnGetFolder;
    @FXML
    private Label folderLabel;

    public void initialize(URL location, ResourceBundle resources) {
        getSettingsFromFile();
    }

    // Метод получения настроек из файла
    private void getSettingsFromFile() {

        try {
            // Считываем файл настроек
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(SQLConnection.settingsFile), "cp1251"));
            String line;
            while ((line = bf.readLine()) != null) {
                baseFolder = line;
            }


            // Если файл пуст то выводим сообщение
            if (baseFolder.isEmpty()) {
                folderLabel.setText("Выберите папку с базами!");
            } else {
                folderLabel.setText("Папка для хранения баз: " + baseFolder);
            }

            bf.close(); // Закрываем поток
        } catch (IOException e) {
            folderLabel.setText("Папка с базами не найдена!");
        }

    }


    // Обработка кнопки для выбора папки
    @FXML
    private void getNewDbFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDir = directoryChooser.showDialog(null);

        if (selectedDir == null) {
            folderLabel.setText("Папка не выбрана");
        } else {
            baseFolder = selectedDir.getAbsolutePath();

            try {
                // Записываем путь к папке с базами в файл настроек и выводим в Label
                BufferedWriter bw = new BufferedWriter(new FileWriter(SQLConnection.settingsFile));
                bw.write(baseFolder);
                folderLabel.setText("Папка для хранения баз: " + baseFolder);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void closeSettings(ActionEvent event) {

        // Подтверждение о том что программа будет закрыта
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Для применения настроек, перезапустите приложение!", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Приложение будет автоматически закрыто!");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            // Закрываем нод
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
            System.exit(0);
        } else {
            return;
        }
    }
}
