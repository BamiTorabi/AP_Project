package server;

import java.sql.*;

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

    public ResultSet getResult(String tableName, String ID, String[] conditions, String[] columns) throws SQLException {
        String message = "SELECT ";
        if (columns.length == 0)
            message += "*";
        else{
            message += "universityID";
            for (String column : columns)
                message += ", " + column;
        }
        message += " FROM " + databaseName + "." + tableName;
        message += " WHERE " + (tableName.equals("Courses") ? "courseID" : "universityID") + "=" + ID;
        if (conditions.length > 0){
            for (int i = 0; i < conditions.length; i++){
                message += " AND " + conditions[i] + " ";
            }
        }
        message += ";";
        //System.err.println("mysql> " + message);
        Statement statement = connection.createStatement();
        return statement.executeQuery(message);
    }

    public void updateTable(String tableName, String ID, String[] conditions, String fieldName, String newValue) throws SQLException {
        String message = "UPDATE " + tableName;
        message += " SET " + fieldName + " = \"" + newValue + "\"";
        message += " WHERE " + (tableName.equals("Courses") ? "courseID" : "universityID") + "=\"" + ID + "\"";
        if (conditions.length > 0){
            for (int i = 0; i < conditions.length; i++){
                message += " AND " + conditions[i] + " ";
            }
        }
        message += ";";
        System.err.println("mysql> " + message);
        Statement statement = connection.createStatement();
        statement.executeUpdate(message);
    }

}
