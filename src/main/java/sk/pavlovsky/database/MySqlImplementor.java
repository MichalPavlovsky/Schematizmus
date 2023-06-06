package sk.pavlovsky.database;

import sk.pavlovsky.database.interfaces.DatabaseImplementor;

import java.sql.*;

public class MySqlImplementor implements DatabaseImplementor {
    private Connection con;

    @Override
    public void connect() {
        if (con != null) return;
        try {
            Class.forName(Databases.API);
            String connectionURL = Databases.JDBC_URL;
            con = DriverManager.getConnection(connectionURL, Databases.USERNAME, Databases.PASSWORD);
            Statement statement = con.createStatement();
            statement.close();
        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void insertData(int id, String firstName) {
        String query = "INSERT INTO `funkcia` (`ID`,`NAZOV`) VALUES (?, ?)";

        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.setString(2, firstName);
            statement.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Issue with insert Data");
        }
    }
}
