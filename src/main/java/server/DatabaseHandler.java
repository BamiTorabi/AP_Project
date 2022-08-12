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

    public ResultSet getResult(String tableName, String[] conditions, String[] columns) throws SQLException {
        String message = "SELECT ";
        if (columns.length == 0)
            message += "*";
        else{
            for (String column : columns)
                message += (column.equals(columns[0]) ? "" : ", ") + column;
        }
        message += " FROM " + databaseName + "." + tableName;
        //message += " WHERE " + (tableName.equals("Courses") ? "courseID" : "universityID") + "=" + ID;
        if (conditions.length > 0){
            for (int i = 0; i < conditions.length; i++){
                message += (i == 0 ? " WHERE " : " AND ") + conditions[i] + " ";
            }
        }
        message += ";";
        //System.err.println("mysql> " + message);
        Statement statement = connection.createStatement();
        return statement.executeQuery(message);
    }

    public String[] getColumnNames(String tableName) throws SQLException {
        String message = "SELECT * FROM " + tableName + ";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(message);
        ResultSetMetaData metaData = resultSet.getMetaData();
        String[] columnNames = new String[metaData.getColumnCount()];
        for (int i = 1; i <= metaData.getColumnCount(); i++)
            columnNames[i - 1] = metaData.getColumnName(i);
        return columnNames;
    }

    public ResultSet getResult(String tableName, String[] conditions, String[] columns, String[] joins, String[] additionalCommands) throws SQLException {
        String message = "SELECT ";
        if (columns.length == 0)
            message += "*";
        else{
            for (String column : columns)
                message += (column.equals(columns[0]) ? "" : ", ") + column;
        }
        message += " FROM " + databaseName + "." + tableName;
        if (joins != null)
            for (String join : joins) {
                message += " JOIN " + databaseName + "." + join;
            }
        //message += " WHERE " + (tableName.equals("Courses") ? "courseID" : "universityID") + "=" + ID;
        if (conditions != null && conditions.length > 0){
            for (int i = 0; i < conditions.length; i++){
                message += (i == 0 ? " WHERE " : " AND ") + conditions[i] + " ";
            }
        }
        if (additionalCommands != null)
            for (String command : additionalCommands){
                message += " " + command;;
            }
        message += ";";
        //System.err.println("mysql> " + message);
        Statement statement = connection.createStatement();
        return statement.executeQuery(message);
    }

    public void addCompleteRow(String tableName, String[] values) throws SQLException{
        String message = "INSERT INTO " + tableName;
        message += " VALUES (";
        for (String value : values){
            message += (value.equals(values[0]) ? "" : ", ") + value;
        }
        message += ");";
        System.out.println("mysql> " + message);
        Statement statement = connection.createStatement();
        statement.executeUpdate(message);
    }

    public void addRowWithID(String tableName, String[] values, String[] columns) throws SQLException{
        String message = "INSERT INTO " + tableName;
        for (String column : columns){
            message += (column.equals(columns[0]) ? " (" : ", ") + column;
        }
        message += ") VALUES (";
        for (String value : values){
            message += (value.equals(values[0]) ? "" : ", ") + value;
        }
        message += ");";
        System.out.println("mysql> " + message);
        Statement statement = connection.createStatement();
        statement.executeUpdate(message);
    }

    public void deleteCompleteRow(String tableName, String[] conditions) throws SQLException{
        String message = "DELETE FROM " + tableName;
        if (conditions != null && conditions.length > 0){
            for (int i = 0; i < conditions.length; i++){
                message += (i == 0 ? " WHERE " : " AND ") + conditions[i] + " ";
            }
        }
        message += ";";
        System.out.println("mysql> " + message);
        Statement statement = connection.createStatement();
        statement.executeUpdate(message);
    }

    public void updateTable(String tableName, String[] values, String[] conditions) throws SQLException {
        String message = "UPDATE " + tableName;
        message += " SET ";
        for (String value : values){
            message += (value.equals(values[0]) ? "" : ", ") + value;
        }
        if (conditions.length > 0){
            for (int i = 0; i < conditions.length; i++){
                message += (i == 0 ? " WHERE " : " AND ") + conditions[i] + " ";
            }
        }
        message += ";";
        System.err.println("mysql> " + message);
        Statement statement = connection.createStatement();
        statement.executeUpdate(message);
    }

    public void close(){
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {

            }
        }
    }

}
