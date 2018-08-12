import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class EnterController implements Initializable {
    public Logic logic = new Logic();
    @FXML
    private TextField loginName;
    @FXML
    private PasswordField loginPass;
    @FXML
    private Label loginStatus;
    @FXML
    private Button btnLogin;
    @FXML
    private Label txtCrudiLogo;


    public void btnLoginEvent(ActionEvent actionEvent) {
        try {

            // Верификация (проверка логина и пароля)
            if (logic.verifLogin(loginName.getText(), loginPass.getText())) {

                // Закрываем окно авторизации и создаем новое основное
                Stage parentStage = (Stage) btnLogin.getScene().getWindow();
                parentStage.close();

                Stage mainStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("/crudiMain.fxml"));
                mainStage.getIcons().add(new Image("/icon.png"));
                mainStage.setTitle("CRUDi ТМЦ (build 042)");
                mainStage.setScene(new Scene(root));
                mainStage.setWidth(920);
                mainStage.setHeight(550);
                mainStage.setMinWidth(850);
                mainStage.setMinHeight(500);
                mainStage.show();
            } else {
                // Если пароль не верен
                loginStatus.setVisible(true);
                loginStatus.setText("Не правильный логин/пароль!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Обрабатываем кнопку Настройки (на случай если программа не знает где Базы)
    public void btnSettingsPress(ActionEvent event) {
        try {
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/crudiSettings.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.initOwner(null); // Делаем окно модальным
            primaryStage.initModality(Modality.APPLICATION_MODAL); // Делаем окно модальным
            primaryStage.getIcons().add(new Image("/icon.png"));
            primaryStage.setTitle("Настройки приложения");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Делам логотип с тенью :)
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));

        txtCrudiLogo.setEffect(ds);
        txtCrudiLogo.setCache(true);


        // Проверяем коннект к базе, если он есть - активируем кнопку "вход"
        if (!logic.isDbConnected()) {
            btnLogin.setDisable(true);
        }

    }
}
