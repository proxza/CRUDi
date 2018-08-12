import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLConnection {
    public static final String TMP_DIR = System.getenv("APPDATA") + "\\CRUDiTMP\\"; // Путь к временной папке
    public static File settingsFile = new File(TMP_DIR + "crudiSettings.cfg"); // Имя файла с настройками
    private static String baseFolder = TMP_DIR;

    public static Connection Connector() {
        try {
            // Проверяем, если временной папки нет, создаем
            File createTmpFolder = new File(TMP_DIR);
            if (!createTmpFolder.exists()) {
                createTmpFolder.mkdir();
            }

            // Считываем файл настроек
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFile), "cp1251"));
            String line;
            while ((line = bf.readLine()) != null) {
                baseFolder = line;
            }

            // Подключаем драйвер sqlite и базу
            Class.forName("org.sqlite.JDBC");
            Connection connect = DriverManager.getConnection("jdbc:sqlite:" + baseFolder + "\\crudiBase.db"); // Подключаем файл базы

            return connect;

        } catch (FileNotFoundException ex) {
            return null;
        } catch (Exception ex) {
            Alerts.showInfoDialog("ОШИБКА БАЗЫ", "База данных не найдена! \nВыберите базу в настройках и перегрузите программу!");
            return null;
        }
    }
}
