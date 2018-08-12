import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class HistoryItemController {
    private Item item;
    private Logic logic = new Logic();

    @FXML
    private TextArea txtHistoryItem;
    @FXML
    private Label labelOfHistoryItem;


    // Получаем предмет и выводим его историю
    public void setItem(Item item) {
        this.item = item;
        labelOfHistoryItem.setText("История предмета: " + item.getItemName() + " (№" + item.getItemNumber() + ")");
        txtHistoryItem.setText(logic.getHistoryItem(item.getId()));
    }

    // Обработка нажатия кнопки
    @FXML
    private void closeHistoryDialog(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
