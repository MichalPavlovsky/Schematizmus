package sk.pavlovsky.utils;

import com.mysql.cj.conf.PropertyKey;

import java.sql.*;
public class Database {

    private Connection con;

    public void connect() throws Exception{

        if(con != null) return;

        try {
            Class.forName(Databases.API);
        } catch (ClassNotFoundException e) {
            throw new Exception("No database");
        }

        String connectionURL = Databases.JDBC_URL;

        con = DriverManager.getConnection(connectionURL, Databases.USERNAME, Databases.PASSWORD);
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from people");
        while(resultSet.next()) {
            System.out.println(resultSet.getString("firstname"));
        }
    }
    public void insertData(int id, String firstName) throws SQLException {
        String query = "INSERT INTO `people` (`id`, `firstname`) VALUES (?, ?)";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, firstName);
            statement.executeUpdate();
        }
    }


    public void close(){
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}


