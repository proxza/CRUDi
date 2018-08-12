import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditCompanyController implements Initializable {
    private Logic logic = new Logic();

    @FXML
    private ComboBox<String> company;
    @FXML
    private Button btnCompanyCancel;
    @FXML
    private TextField companyName;
    @FXML
    private TextField companyDesc;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logic.fillCompany(); // Заполняем выпадающий список компаний
        company.setItems(logic.getCompanyList());
    }

    // Обработчик нажатия на кнопку
    public void closeCompanyDialog(ActionEvent event) {

        // в рут окне с таблицей предметов, таблица сначала чиститься, а после заполняется по новой
        // по идентификатору офиса который был выбран
        MainController.cData.clearCompanyList();
        MainController.cData.fillCompany();

        // Закрываем ноду (диалоговое окно)
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }

    // Обработчик который передается нам ИЗ рут окна (выбранный офис)
    public void setCompany(ActionEvent event) {
        // Достаем инфу по офису который был выбран
        logic.getCompanyToDialog(company.getValue());
        companyName.setText(company.getValue());
        companyDesc.setText(logic.companyDesc);
    }

    // Обработчик нажатия на кнопку
    public void btnAddPressed(ActionEvent event) {
        // Проверка полей на пустоту
        if (companyName.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не ввели название офиса!");
            return;
        }

        // Метод добавления офиса в БД
        logic.addCompany(companyName.getText(), companyDesc.getText());
        closeCompanyDialog(event);
    }

    // Обработчик нажатия на кнопку
    public void btnDeletePressed(ActionEvent event) {
        // Проверка полей на пустоту
        if (companyName.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не выбрали офис!");
            return;
        }

        // Подтверждение удаления
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Вы уверены что хотите удалить офис - " + companyName.getText() + "?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление будет безвозвратным!");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            logic.deleteCompany(companyName.getText());
            closeCompanyDialog(event);
        } else {
            return;
        }
    }

    // Обработчик нажатия на кнопку
    public void btnSavePress(ActionEvent event) {
        // Проверка полей на пустоту
        if (companyName.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не выбрали офис!");
            return;
        }

        // Обновляем инфу об офисе в БД
        logic.updateCompany(Integer.parseInt(logic.companyId), companyName.getText(), companyDesc.getText());
        closeCompanyDialog(event);
    }

}
