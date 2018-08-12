import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public static Logic cData = new Logic();
    private ResourceBundle resourceBundle;
    private ObservableList<Item> backupList; // Лист для бэкапа в него списка предметов, используется для поиска по листу

    @FXML
    Button btnEdit;
    @FXML
    Button btnDelete;
    @FXML
    Button btnAdd;
    @FXML
    Button btnUsers;
    @FXML
    Button btnCompany;
    @FXML
    Button btnSettings;
    @FXML
    Button btnHistoryItem;
    @FXML
    Button btnDeleted;
    @FXML
    Button btnPrint;

    @FXML
    TextField txtSearch;

    @FXML
    ComboBox<String> companyBox;

    @FXML
    private TableView mainTableView;
    @FXML
    private TableColumn<Item, Integer> idView;
    @FXML
    private TableColumn<Item, Integer> invNumberView;
    @FXML
    private TableColumn<Item, String> itemNameView;
    @FXML
    private TableColumn<Item, String> itemDescView;
    @FXML
    private TableColumn<Item, Integer> itemPriceView;
    @FXML
    private TableColumn<Item, String> itemBuyView;
    @FXML
    private TableColumn<Item, String> itemCrashView;
    @FXML
    private TableColumn<Item, String> itemPlaceView;
    @FXML
    private Label itemsCounter;
    @FXML
    private Label countPriceOfAllItem;


    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        this.resourceBundle = resources; // ??? needed?

        // Прописываем иконку для кнопки Принтера
        Image imgPrint = new Image(getClass().getResourceAsStream("print.png"));
        btnPrint.setGraphic(new ImageView(imgPrint));

        // Чекаем группу пользователя и скрываем не нужные функции от него
        setVisibleToGroups(cData.loginnedUserGroupId);

        // Заполняем фосиы
        cData.fillCompany();

        // Привязываем столбцы в таблице
        idView.setCellValueFactory(new PropertyValueFactory<>("id"));
        invNumberView.setCellValueFactory(new PropertyValueFactory<>("itemNumber"));
        itemNameView.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemDescView.setCellValueFactory(new PropertyValueFactory<>("itemDesc"));
        itemPriceView.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        itemBuyView.setCellValueFactory(new PropertyValueFactory<>("itemBuy"));
        itemCrashView.setCellValueFactory(new PropertyValueFactory<>("itemCrash"));
        itemPlaceView.setCellValueFactory(new PropertyValueFactory<>("itemPlace"));

        // Центрируем столбцы
        idView.setStyle("-fx-alignment: CENTER;");
        invNumberView.setStyle("-fx-alignment: CENTER;");
        itemPriceView.setStyle("-fx-alignment: CENTER;");
        itemBuyView.setStyle("-fx-alignment: CENTER;");
        itemCrashView.setStyle("-fx-alignment: CENTER;");

        // Заполняем таблицу
        companyBox.setItems(cData.getCompanyList());

        // Запуск слушателя для дин.отображения количества предметов в таблице
        counterListener();

        // Делаем кнопки требующие выделения конкретных предметов неактивными
        setEnableSelectedBtn();

        // Слушатель для двойного клика по предмету в таблице
        doubleClickSelectedItem();

    }


    // Проверка груп айди и выдача прав
    public void setVisibleToGroups(int id) {
        if (id == 0) { // Guests group
            btnEdit.setVisible(false);
            btnAdd.setVisible(false);
            btnDelete.setVisible(false);
            btnUsers.setVisible(false);
            btnCompany.setVisible(false);
            btnHistoryItem.setVisible(false);
            btnDeleted.setVisible(false);
            btnAdd.setVisible(false);
            btnPrint.setVisible(false);
        }
        if (id == 1) { // Users group
            btnCompany.setVisible(false);
            btnUsers.setVisible(false);
            btnDelete.setVisible(false);
            btnEdit.setVisible(false);
            btnHistoryItem.setVisible(false);
            btnDeleted.setVisible(false);
            btnAdd.setVisible(false);
            btnPrint.setVisible(false);
        } else if (id == 2) { // Writer group
            btnCompany.setVisible(false);
            btnUsers.setVisible(false);
            btnDelete.setVisible(false);
            btnHistoryItem.setVisible(false);
            btnDeleted.setVisible(false);
            btnAdd.setVisible(false);
            btnPrint.setVisible(false);
        } else if (id == 3) { // Moderator group
            btnUsers.setVisible(false);
            btnCompany.setVisible(false);
            btnHistoryItem.setVisible(false);
            btnDeleted.setVisible(false);
            btnPrint.setVisible(false);
        } else if (id == 7) { // Admin group
            // Взято с запасом на случай если понадобятся другие группы
        }
    }


    // Заполнение списка офисов
    public void setCompanyBox(ActionEvent event) {
        cData.clearItemList();
        try {
            cData.fillItemFromDB(companyBox.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Вносим копию листа в бэкап для поиска по нему
        backupList = FXCollections.observableArrayList();
        backupList.addAll(cData.getItemList());

        mainTableView.setItems(cData.getItemList());
    }


    // Главный метод диалоговых окон который раскидывает методы по нажатым кнопкам
    public void showDialog(ActionEvent actionEvent) {

        Item selectedItem = (Item) mainTableView.getSelectionModel().getSelectedItem(); // Получаем выделенный предмет
        Object source = actionEvent.getSource(); // Получаем объект кнопки
        Button clickedBtn = (Button) source;

        switch (clickedBtn.getId()) {
            case "btnCompany": // Если нажата кнопка Офисы
                showEditCompanyDialog();
                break;

            case "btnUsers": // Если нажата кнопка Пользователи
                showEditUsersDialog();
                break;

            case "btnSettings": // Если нажата кнопка Настройки
                showSettingsDialog();
                break;

            case "btnAdd": // Если нажата кнопка Добавить
                showAddDialog();
                break;

            case "btnEdit": // Если нажата кнопка Изменить
                // Если предмет не выбран - возвращаемся
                // По логике в данной проверке уже нет смысла так как есть метод активирующий
                // кнопку только если выбран предмет.
                // В будущем можно удалить
                if (!ifItemSelected(selectedItem)) {
                    return;
                }
                showEditDialog();
                break;

            case "btnDelete": // Если нажата кнопка Удалить
                // Если предмет не выбран - возвращаемся
                // По логике в данной проверке уже нет смысла так как есть метод активирующий
                // кнопку только если выбран предмет.
                // В будущем можно удалить
                if (!ifItemSelected(selectedItem)) {
                    return;
                } else {
                    // Окошко подтверждения удаления предмета
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Вы уверены что хотите удалить " + selectedItem.getItemName() + " (" + selectedItem.getItemNumber() + ") ?", ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Подтверждение удаления");
                    alert.setHeaderText("Удаление будет безвозвратным!");
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.YES) {
                        cData.deleteItem(selectedItem, companyBox.getValue());
                    } else {
                        return;
                    }
                }
                break;

            case "btnHistoryItem": // Если нажата кнопка Истории
                // Если предмет не выбран - возвращаемся
                // По логике в данной проверке уже нет смысла так как есть метод активирующий
                // кнопку только если выбран предмет.
                // В будущем можно удалить
                if (!ifItemSelected(selectedItem)) {
                    return;
                }
                showHistoryItemDialog();
                break;

            case "btnDeleted": // Если нажата кнопка Удаленные
                showDeleteHistoryDialog();
                break;
        }

    }


    // Метод создающий окно с формой для добавления предметов
    public void showAddDialog() {
        try {
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/AddItem.fxml").openStream());

            primaryStage.initOwner(null); // Делаем окно модальным
            primaryStage.initModality(Modality.APPLICATION_MODAL); // Делаем окно модальным
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("/icon.png"));
            primaryStage.setTitle("Добавить предмет");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Метод создающий окно с формой для редактирования офисов
    public void showEditCompanyDialog() {
        try {
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/EditCompany.fxml").openStream());

            primaryStage.initOwner(null); // Делаем окно модальным
            primaryStage.initModality(Modality.APPLICATION_MODAL); // Делаем окно модальным
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("/icon.png"));
            primaryStage.setTitle("Редактирование офисов");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Метод создающий окно с формой для редактирование пользователей
    public void showEditUsersDialog() {
        try {
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/EditUsers.fxml").openStream());

            primaryStage.initOwner(null); // Делаем окно модальным
            primaryStage.initModality(Modality.APPLICATION_MODAL); // Делаем окно модальным
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("/icon.png"));
            primaryStage.setTitle("Редактирование пользователей");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Метод создающий окно с формой для редактирования предметов
    public void showEditDialog() {
        try {

            Item selectedItem = (Item) mainTableView.getSelectionModel().getSelectedItem(); // Получаем выделенный предмет

            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/crudiDialog.fxml").openStream());

            primaryStage.initOwner(null); // Делаем окно модальным
            primaryStage.initModality(Modality.APPLICATION_MODAL); // Делаем окно модальным
            EditController editController = loader.getController();
            editController.setItem(selectedItem);
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("/icon.png"));
            primaryStage.setTitle("Редактирование предмета");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Метод создающий окно с формой настроек
    public void showSettingsDialog() {
        try {
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/crudiSettings.fxml").openStream());

            primaryStage.initOwner(null); // Делаем окно модальным
            primaryStage.initModality(Modality.APPLICATION_MODAL); // Делаем окно модальным
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("/icon.png"));
            primaryStage.setTitle("Настройки приложения");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Метод создающий окно с историей предмета
    public void showHistoryItemDialog() {
        try {

            Item selectedItem = (Item) mainTableView.getSelectionModel().getSelectedItem();

            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/HistoryItem.fxml").openStream());

            primaryStage.initOwner(null); // Делаем окно модальным
            primaryStage.initModality(Modality.APPLICATION_MODAL); // Делаем окно модальным
            HistoryItemController historyItemController = loader.getController();
            historyItemController.setItem(selectedItem);
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("/icon.png"));
            primaryStage.setTitle("История предмета");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Метод создающий окно с историей удаленных предметов
    public void showDeleteHistoryDialog() {
        try {
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/DeleteHistory.fxml").openStream());

            primaryStage.initOwner(null); // Делаем окно модальным
            primaryStage.initModality(Modality.APPLICATION_MODAL); // Делаем окно модальным
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("/icon.png"));
            primaryStage.setTitle("История удаленных предметов");
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Метод обработки поиска (нажатие кнопки Поиск)
    public void btnSearchPress(ActionEvent event) {
        cData.getItemList().clear(); // Очищаем таблицу

        for (Item item : backupList) {
            // Проверка
            if (item.getItemNumber().toString().equals(txtSearch.getText()) ||
                    item.getItemName().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
                    item.getItemDesc().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
                    item.getItemBuy().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
                    item.getItemPrice().toString().equals(txtSearch.getText()) ||
                    item.getItemCrash().toLowerCase().contains(txtSearch.getText().toLowerCase()) ||
                    item.getItemPlace().toLowerCase().contains(txtSearch.getText().toLowerCase())) {

                cData.getItemList().add(item); // Добавляем в таблицу найденное

            }
        }
    }


    // Метод проверки на выделение предмета в таблице
    public boolean ifItemSelected(Item selectedItem) {
        if (selectedItem == null) {
            Alerts.showErrorDialog("ОШИБКА!", "Не выбран предмет из списка!");
            return false;
        }
        return true;
    }


    // Слушатель (счетчик) предметов в таблице
    public void counterListener() {
        cData.getItemList().addListener(new ListChangeListener<Item>() {
            @Override
            public void onChanged(Change<? extends Item> c) {
                itemsCounter.setVisible(true); // Делаем видимым
                countPriceOfAllItem.setVisible(true);
                countPriceOfAllItem.setText("Общая стоимость ТМЦ: " + Item.priceCounter + "грн.");
                // Если Админ - выводим так же и кол.предметов в базе вообще
                if (cData.loginnedUserGroupId >= 4) {
                    itemsCounter.setText("Всего ТМЦ в офисе: " + cData.getItemList().size() + " шт. / Всего в базе: " + cData.getCountOfALlItems() + " предметов");
                } else {
                    itemsCounter.setText("Всего ТМЦ в офисе: " + cData.getItemList().size() + " шт. ");
                }
            }
        });
    }


    // Метод делающий активными кнопки Удалить/Изменить/Историю только при выделенном предмете
    public void setEnableSelectedBtn() {
        mainTableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
                btnHistoryItem.setDisable(false);
            }
        });
    }


    // Метод для принтера
    public void printThisTableView(ActionEvent event){
        Printer printer = Printer.getDefaultPrinter();
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        // Настройки печати таблицы под лист А4 (альбомный)
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
        double scaleX = pageLayout.getPrintableWidth() / mainTableView.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / mainTableView.getBoundsInParent().getHeight();
        Scale scale = new Scale(scaleX, scaleY);
        mainTableView.getTransforms().add(scale);

        if (printerJob != null) {
            boolean successPrintDialog = printerJob.showPrintDialog(null);
            if(successPrintDialog){
                boolean success = printerJob.printPage(pageLayout,mainTableView);
                if (success) {
                    printerJob.endJob();
                }
            }
        }
        mainTableView.getTransforms().remove(scale);
    }


    public void doubleClickSelectedItem(){
        mainTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    //Item selectedItem = (Item) mainTableView.getSelectionModel().getSelectedItem(); // Получаем выделенный предмет
                    showEditDialog();
                }
            }
        });
    }

}
