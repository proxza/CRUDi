import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class EditUsersController implements Initializable {
    private Logic logic = new Logic();
    private int userId;

    // Лист для вывода групп пользователя
    private ObservableList<String> groupBoxSelect = FXCollections.observableArrayList(
            "Гость",
            "Пользователь",
            "Редактор",
            "Модератор",
            "Администратор"
    );

    @FXML
    private ComboBox<String> userListBox;
    @FXML
    private ComboBox<String> groupBox = new ComboBox();
    @FXML
    private TextField userNameTxt;
    @FXML
    private TextField userPassTxt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logic.fillUsersList(); // Заполняем лист имеющихся пользователей
        userListBox.setItems(logic.getUsersList());

        groupBox.setItems(groupBoxSelect);
    }


    // Заполняем форму выбранным пользователем
    public void setUsers(ActionEvent event) {
        logic.getUsersListToDialog(userListBox.getValue());
        userId = logic.userId;
        userNameTxt.setText(logic.userName);
        groupBox.getSelectionModel().select(logic.userGroup);
    }


    // Обработка нажатия кнопки
    public void btnAddPressed(ActionEvent event) {

        // Проверка полей на пустоту
        if (userNameTxt.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не ввели имя!");
            return;
        } else if (userPassTxt.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не ввели пароль!");
            return;
        } else if (groupBox.getValue() == null) {
            Alerts.showInfoDialog("Информация", "Вы не ввели группу!");
            return;
        }

        // Шифруем пароль в md5
        String md5pass = DigestUtils.md5Hex(userPassTxt.getText());

        // Добавляем пользователя
        logic.addUser(userNameTxt.getText(), md5pass, groupBox.getSelectionModel().getSelectedIndex());
        closeUsersDialog(event);
    }


    // Обработка нажатия кнопки
    public void btnDeletePressed(ActionEvent event) {

        // Проверка на пустое поле
        if (userNameTxt.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не выбрали пользователя!");
            return;
        }

        // Окно подтверждения удаления юзера
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Вы уверены что хотите удалить пользователя - " + userNameTxt.getText() + "?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление будет безвозвратным!");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            logic.deleteUser(userNameTxt.getText());
            closeUsersDialog(event);
        } else {
            return;
        }
    }


    // Обработка нажатия на кнопку
    public void btnSavePress(ActionEvent event) {

        // Проверка на пустое поле
        if (userNameTxt.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не выбрали пользователя!");
            return;
        }

        // Проверка на пустой пароль и шифровка
        String md5pass = "";
        if (!userPassTxt.getText().isEmpty()) {
            md5pass = DigestUtils.md5Hex(userPassTxt.getText());
        } else {
            md5pass = "";
        }


        logic.updateUser(userId, userNameTxt.getText(), md5pass, groupBox.getSelectionModel().getSelectedIndex());
        closeUsersDialog(event);
    }


    // Обработка закрытия
    public void closeUsersDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }
}
