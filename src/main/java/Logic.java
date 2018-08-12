import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Logic {
    private ObservableList<Item> itemList = FXCollections.observableArrayList();
    private ObservableList<String> companyList = FXCollections.observableArrayList();
    private ObservableList<String> usersList = FXCollections.observableArrayList();
    private Connection connection;
    public String companyId;
    public String companyName;
    public String companyDesc;

    public int userId;
    public String userName;
    public String userPass;
    public int userGroup = 0;
    public static int loginnedUserGroupId = 0; // тут храним группу юзера под которым работаем
    public static String loginnedUserName; // Тут храним имя юзера под которым работаем


    public Logic() {
        connection = SQLConnection.Connector();

        // Проверка - если база не подключена (найдена) выдаем мессагу
        if (connection == null) {
            Alerts.showInfoDialog("Информация", "База данных не найдена! \nВыберите базу в настройках");
            return;
        }

    }

    // проверка на коннект
    public boolean isDbConnected() {
        if (connection != null) {
            return true;
        } else {
            return false;
        }
    }


    public boolean verifLogin(String username, String password) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        password = DigestUtils.md5Hex(password);

        String query = "select * from users where username = ? and password = ?";

        try {

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);


            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                loginnedUserName = resultSet.getString("username");
                loginnedUserGroupId = resultSet.getInt("groupid");
                return true;
            } else {
                return false;
            }


        } catch (Exception ex) {
            Alerts.showInfoDialog("ОШИБКА БАЗЫ", "База данных не найдена! \nВыберите базу в настройках и перегрузите программу!");
            return false;
        } finally {
            preparedStatement.close();
            resultSet.close();
        }
    }


    // update
    public void updateItem(Item item, Item old) throws SQLException {
        Statement st = connection.createStatement();

        int number = item.getItemNumber();
        String name = item.getItemName();
        String desc = item.getItemDesc();
        int price = item.getItemPrice();
        String buy = item.getItemBuy();
        String crash = item.getItemCrash();
        String place = item.getItemPlace();
        String idCompany = item.getItemCompany();


        String query = "UPDATE Items SET number = " + number + ", name = '" + name + "', desc = '" + desc + "', price = " + price + ", buy = '" + buy + "', crash = '" + crash + "', place = '" + place + "', idCompany = '" + idCompany + "', whoadd = '" + loginnedUserName + "' WHERE id = " + item.getId();
        st.executeUpdate(query);


        // history update
        String dopinfo = "";
        if (!old.getItemNumber().equals(item.getItemNumber())) {
            dopinfo += "[№" + old.getItemNumber() + " на №" + item.getItemNumber() + "] ";
        }
        if (!old.getItemName().equals(item.getItemName())) {
            dopinfo += "[Название с " + old.getItemName() + " на " + item.getItemName() + "] ";
        }
        if (!old.getItemDesc().equals(item.getItemDesc())) {
            dopinfo += "[Описание с " + old.getItemDesc() + " на " + item.getItemDesc() + "] ";
        }
        if (!old.getItemPrice().equals(item.getItemPrice())) {
            dopinfo += "[Цену с " + old.getItemPrice() + " на " + item.getItemPrice() + "] ";
        }
        if (!old.getItemBuy().equals(item.getItemBuy())) {
            dopinfo += "[Дату покупки с " + old.getItemBuy() + " на " + item.getItemBuy() + "] ";
        }
        if (!old.getItemCrash().equals(item.getItemCrash())) {
            dopinfo += "[Дату списания с " + old.getItemCrash() + " на " + item.getItemCrash() + "] ";
        }
        if (!old.getItemPlace().equals(item.getItemPlace())) {
            dopinfo += "[Местоположение с " + old.getItemPlace() + " на " + item.getItemPlace() + "] ";
        }
        if (!old.getItemCompany().equals(item.getItemCompany())) {
            dopinfo += "[Офис с " + old.getItemCompany() + " на " + item.getItemCompany() + "] ";
        }
        String log = getCurrentDateWithHMS() + " Пользователь " + loginnedUserName + " изменил " + dopinfo;
        toHistory(1, log, item.getId());

        st.close();


    }


    // update user
    public void updateUser(int id, String name, String pass, int group) {
        try {
            Statement st = connection.createStatement();

            String query = "";

            if (pass.isEmpty()) {
                query = "UPDATE Users SET username = '" + name + "', groupid = " + group + " WHERE id = " + id;
            } else {
                query = "UPDATE Users SET username = '" + name + "', password = '" + pass + "', groupid = " + group + " WHERE id = " + id;
            }
            st.executeUpdate(query);


            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // update company
    public void updateCompany(int id, String cName, String cDesc) {
        try {
            Statement st = connection.createStatement();

            String query = "UPDATE Company SET companyName = '" + cName + "', companyDesc = '" + cDesc + "' WHERE id = " + id;
            st.executeUpdate(query);
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // add new item
    public void addItem(Item item) throws SQLException {
        Statement st = connection.createStatement();

        String query = "INSERT INTO Items(number, name, desc, price, buy, crash, place, idCompany, whoadd) VALUES (" + item.getItemNumber() + ", '"
                + item.getItemName() + "', '"
                + item.getItemDesc() + "', "
                + item.getItemPrice() + ", '"
                + item.getItemBuy() + "', '"
                + item.getItemCrash() + "', '"
                + item.getItemPlace() + "', '"
                + item.getItemCompany() + "', '"
                + loginnedUserName + "')";

        st.addBatch(query);
        st.executeBatch();

        // history update
        String log = getCurrentDateWithHMS() + " Пользователь " + loginnedUserName + " добавил предмет - " + item.getItemName() + " (№" + item.getItemNumber() + ")";
        int countId = getCountItems();
        toHistory(2, log, countId);

        st.close();


    }

    // add new company
    public void addCompany(String cName, String cDesc) {
        try {
            Statement st = connection.createStatement();


            String query = "INSERT INTO Company(companyName, companyDesc) VALUES ('" + cName + "', '" + cDesc + "')";
            //st.executeQuery(query);

            st.addBatch(query);
            st.executeBatch();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // add new user
    public void addUser(String name, String pass, int group) {
        try {
            Statement st = connection.createStatement();
            String query = "INSERT INTO Users(username, password, groupid) VALUES ('" + name + "', '" + pass + "', " + group + ")";

            st.addBatch(query);
            st.executeBatch();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    // delete item
    public void deleteItem(Item item, String company) {
        try {
            Statement st = connection.createStatement();
            String query = "DELETE FROM Items WHERE id = " + item.getId();
            st.execute(query);

            // history delete
            String log = getCurrentDateWithHMS() + " Пользователь " + loginnedUserName + " удалил предмет - " + item.getItemName() + " (№" + item.getItemNumber() + ")";
            toHistory(3, log, item.getId());

            clearItemList();
            fillItemFromDB(company);

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete company
    public void deleteCompany(String company) {
        try {
            Statement st = connection.createStatement();
            String query = "DELETE FROM Company WHERE companyName = '" + company + "'";

            String queryItem = "DELETE FROM Items WHERE idCompany = '" + company + "'";

            st.execute(query);
            st.execute(queryItem);

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete user
    public void deleteUser(String name) {
        try {
            Statement st = connection.createStatement();
            String query = "DELETE FROM Users WHERE username = '" + name + "'";

            st.execute(query);

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для принудительной очистки таблицы
    public void clearItemList() {
        itemList.clear();
    }

    public void clearCompanyList() {
        companyList.clear();
    }


    // get db
    public void fillItemFromDB(String idCompany) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Item.priceCounter = 0;

        String query = "select * from Items where idCompany = '" + idCompany + "'";

        try {

            preparedStatement = connection.prepareStatement(query);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                getItemList().add(new Item(resultSet.getInt("id"),
                        resultSet.getInt("number"),
                        resultSet.getString("name"),
                        resultSet.getString("desc"),
                        resultSet.getInt("price"),
                        resultSet.getString("buy"),
                        resultSet.getString("crash"),
                        resultSet.getString("place"),
                        resultSet.getString("idCompany")));
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            preparedStatement.close();
            resultSet.close();
        }
    }


    // get company
    public void fillCompany() {
        try {
            Statement st = connection.createStatement();
            String query = "SELECT * FROM Company";

            ResultSet resultSet = st.executeQuery(query);

            while (resultSet.next()) {
                getCompanyList().add(resultSet.getString("companyName"));

            }

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // get company for dialog add/edit/delete
    public void getCompanyToDialog(String name) {
        try {
            Statement st = connection.createStatement();
            String query = "SELECT * FROM Company WHERE companyName = '" + name + "'";

            ResultSet resultSet = st.executeQuery(query);

            while (resultSet.next()) {
                companyId = resultSet.getString("id");
                companyName = resultSet.getString("companyName");
                companyDesc = resultSet.getString("companyDesc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // get users List
    public void fillUsersList() {
        try {
            Statement st = connection.createStatement();
            String query = "SELECT * FROM Users";

            ResultSet resultSet = st.executeQuery(query);

            while (resultSet.next()) {
                getUsersList().add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // get users List for dialog add/edit/delete
    public void getUsersListToDialog(String name) {
        try {
            Statement st = connection.createStatement();
            String query = "SELECT * FROM Users WHERE username = '" + name + "'";

            ResultSet resultSet = st.executeQuery(query);

            while (resultSet.next()) {
                userId = resultSet.getInt("id");
                userName = resultSet.getString("username");
                userPass = resultSet.getString("password");
                userGroup = resultSet.getInt("groupid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // getter
    public ObservableList<String> getCompanyList() {
        return companyList;
    }

    // Getter
    public ObservableList<Item> getItemList() {
        return itemList;
    }

    // getter
    public ObservableList<String> getUsersList() {
        return usersList;
    }


    // Временный метод на получение сегодняшней даты (для новых предметов)
    public String getCurrentDate() {
        DateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        Date today = Calendar.getInstance().getTime();
        return dt.format(today);
    }

    // Временный метод на получение сегодняшней даты ДЛЯ ИСТОРИИ
    public String getCurrentDateWithHMS() {
        DateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        return dt.format(today);
    }

    public void toHistory(int workId, String log, int itemId) {
        try {

            Statement st = connection.createStatement();
            String query = "";

            // 1 - update item
            // 2 - insert item
            // 3 - delete item
            // 4 - add user
            // 5 - update user
            // 6 - delete user
            // 7 - add company
            // 8 - update company
            // 9 - delete company

            query = "INSERT INTO History(id_item, log, id_work) VALUES ('" + itemId + "', '" + log + "', " + workId + ")";

            st.addBatch(query);

            st.executeBatch();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод получения истории предмета
    public String getHistoryItem(int itemId) {
        String result = "";
        try {
            Statement st = connection.createStatement();
            String query = "SELECT * FROM History WHERE id_item = '" + itemId + "'";

            ResultSet resultSet = st.executeQuery(query);

            while (resultSet.next()) {
                result += resultSet.getString("log") + "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    // Очередной костыль чтобы посчитать сколько всего в предметов в базе
    private int getCountItems() {
        int result = 0;
        try {
            Statement st = connection.createStatement();
            String query = "SELECT * FROM Items ORDER BY id DESC LIMIT 1";

            ResultSet resultSet = st.executeQuery(query);

            while (resultSet.next()) {
                result = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return result;
    }

    // Ещё один запрос для получения количества всех предметов в базе (для Админа)
    public int getCountOfALlItems() {
        int result = 0;
        try {
            Statement st = connection.createStatement();
            String query = "SELECT COUNT(*) FROM Items";

            ResultSet resultSet = st.executeQuery(query);

            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return result;
    }

    // Метод получения удаленных предметов
    public String getDeletedItems() {
        String result = "";
        try {
            Statement st = connection.createStatement();
            String query = "SELECT * FROM History WHERE id_work = 3";

            ResultSet resultSet = st.executeQuery(query);

            while (resultSet.next()) {
                result += resultSet.getString("log") + "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
