import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddController implements Initializable {

    private Item item;
    private Logic logic = new Logic();

    @FXML
    private TextField editNumber;
    @FXML
    private TextField editName;
    @FXML
    private TextField editDesc;
    @FXML
    private TextField editPrice;
    @FXML
    private TextField editBuy;
    @FXML
    private TextField editCrash;
    @FXML
    private TextField editPlace;
    @FXML
    private ComboBox<String> editCompany;
    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnCloseAddItem;


    // Обработчик нажатия на кнопку
    public void closeAddItemDialog(ActionEvent event) {
        try {
            // Если поле с офисом не равно нулю - то в рут окне с таблицей предметов, таблица сначала чиститься, а после заполняется по новой
            // по идентификатору офиса который был выбран
            if (editCompany.getValue() != null) {
                MainController.cData.clearItemList();
                MainController.cData.fillItemFromDB(item.getItemCompany());
            } else {
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Закрываем ноду (диалоговое окно)
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }


    // Обработчик нажатия на кнопку
    public void addItemBtnPress(ActionEvent event) {
        // Проверка полей на пустоту
        if (editNumber.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не ивентаризационный номер предмета!");
            return;
        } else if (editName.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не ввели название предмета!");
            return;
        } else if (editCompany.getValue() == null) {
            Alerts.showInfoDialog("Информация", "Вы не выбрали офис для хранения!");
            return;
        }

        try {
            // Заполняем наш объект Item
            item = new Item(0,
                    Integer.parseInt(editNumber.getText()),
                    editName.getText(),
                    editDesc.getText(),
                    Integer.parseInt(editPrice.getText()),
                    editBuy.getText(),
                    editCrash.getText(),
                    editPlace.getText(),
                    editCompany.getValue());

            // Передаем заполненый объект в метод добавляющий его в БД
            logic.addItem(item);
        } catch (NumberFormatException e) {
            Alerts.showErrorDialog("ОШИБКА ВВОДА", "В полях \"№\" и \"Цена\" разрешены только цифры! \nПроверьте данные которые вы вводите");
            return;
        } catch (BatchUpdateException e) {
            Alerts.showErrorDialog("ОШИБКА БД", "Предмет с таким инвентаризацонным номером уже есть в базе!");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Закрываем окно
        closeAddItemDialog(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logic.fillCompany(); // Заполняем выпадающий список компаний
        editCompany.setItems(logic.getCompanyList()); //
        editPrice.setText("0"); // Ценник по умолчанию для нового предмета ставим 0
        editBuy.setText(logic.getCurrentDate()); // Дату покупки вставляем сегодняшним числом
    }
}
