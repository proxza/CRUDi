import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EditController {
    private Item item; // Оригинальный предмет
    private Item oldItem = new Item(0, // Пустой предмет "копия" для заполнения старыми значениями (для вывода в истории разницы значений)
            0,
            null,
            null,
            0,
            null,
            null,
            null,
            null);
    private Logic logic = new Logic();

    private String backUpCompany = "";

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


    public void setItem(Item item) {
        this.item = item;

        logic.fillCompany();
        editCompany.setItems(logic.getCompanyList());

        editNumber.setText(item.getItemNumber().toString());
        editName.setText(item.getItemName());
        editDesc.setText(item.getItemDesc());
        editPrice.setText(item.getItemPrice().toString());
        editBuy.setText(item.getItemBuy());
        editCrash.setText(item.getItemCrash());
        editPlace.setText(item.getItemPlace());
        editCompany.setValue(item.getItemCompany());

        backUpCompany = item.getItemCompany(); // старое показание компании

        // kostil
        // копия делается для отображения измененных полей в истории
        oldItem.setItemNumber(item.getItemNumber());
        oldItem.setItemName(item.getItemName());
        oldItem.setItemDesc(item.getItemDesc());
        oldItem.setItemPrice(item.getItemPrice());
        oldItem.setItemBuy(item.getItemBuy());
        oldItem.setItemCrash(item.getItemCrash());
        oldItem.setItemPlace(item.getItemPlace());
        oldItem.setItemCompany(item.getItemCompany());

    }

    // Обработчик нажатия на кнопку
    public void closeDialog(ActionEvent event) {
        try {
            // Обращаться нужно на прямую к cdata иначе не обновится инфа в таблице после нажатия кнопки
            MainController.cData.clearItemList();
            MainController.cData.fillItemFromDB(backUpCompany);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Закрываем ноду (диалоговое окно)
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    // Обработчик нажатия на кнопку
    public void saveThis(ActionEvent event) {

        // Проверка полей на пустоту
        if (editNumber.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не указали инвентаризационный номер!");
            return;
        } else if (editName.getText().isEmpty()) {
            Alerts.showInfoDialog("Информация", "Вы не указали название предмета!");
            return;
        }

        // Заполняем наш оригинальный объект введенными данными
        item.setItemNumber(Integer.parseInt(editNumber.getText()));
        item.setItemName(editName.getText());
        item.setItemDesc(editDesc.getText());
        item.setItemPrice(Integer.parseInt(editPrice.getText()));
        item.setItemBuy(editBuy.getText());
        item.setItemCrash(editCrash.getText());
        item.setItemPlace(editPlace.getText());
        item.setItemCompany(editCompany.getValue());

        try {
            // Упдейтим наши обновленные данные
            logic.updateItem(item, oldItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeDialog(event);
    }


}
