package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {

    private static DatabaseHandler db = null;
    private Connection connection = null;
    private final String address = "localhost:3306";
    private final String user = "root";
    private final String password = "MySQL!69775";
    private final String databaseName = "eduInfo";

    private DatabaseHandler(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + address +
                            "/" + databaseName +
                            "?user=" + user +
                            "&password=" + password
            );
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseHandler getInstance(){
        if (db == null)
            db = new DatabaseHandler();
        return db;
    }

}
