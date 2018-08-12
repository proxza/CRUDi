import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteController implements Initializable {
    private Logic logic = new Logic();

    @FXML
    private TextArea txtDeleteHistory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Выводим из базы данные по удаленными предметам
        txtDeleteHistory.setText(logic.getDeletedItems());
    }

    // Обработчик нажатия на кнопку
    @FXML
    private void closeDeleteHistoryDialog(ActionEvent event){
        // Закрываем ноду (диалоговое окно)
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
