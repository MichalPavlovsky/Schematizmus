package sk.pavlovsky.database;

import sk.pavlovsky.IdStore;
import sk.pavlovsky.Parish;
import sk.pavlovsky.database.interfaces.DatabaseImplementor;
import sk.pavlovsky.utils.Parser;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public void insertData() {
        String queryEparchy = "INSERT INTO `EPARCHIA` (NAZOV) VALUES (?)";
        String queryPresbyterat = "INSERT INTO `PROTOPRESBYTERAT` (NAZOV, FK_EPARCHIA) VALUES (?, ?)";
        try (
                PreparedStatement stmtEparchy = con.prepareStatement(queryEparchy, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement stmtPresbyterat = con.prepareStatement(queryPresbyterat, Statement.RETURN_GENERATED_KEYS)) {
            createFunctions(con);
            Parser parser = new Parser();
            IdStore idStore = new IdStore();
            HashMap<String, HashMap<String, List<Parish>>> mapOfInformation = parser.runParser();
            for (String keyAllMap : mapOfInformation.keySet()) {
                stmtEparchy.setString(1, keyAllMap);
                stmtEparchy.executeUpdate();
                idStore.setId_eparchy(getID(stmtEparchy));
                for (String dekanat : mapOfInformation.get(keyAllMap).keySet()) {
                    setIntSetStringAndExecute(stmtPresbyterat, dekanat, idStore.getId_eparchy());
                    idStore.setId_dekanat(getID(stmtPresbyterat));
                    List<Parish> listOfParishes = mapOfInformation.get(keyAllMap).get(dekanat);
                    loadParishes(listOfParishes, idStore);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param arrayList      list of parish features for example vypomocny duchovny, kaplani
     * @param stmtOsoba      statement for Osoba who is main priest in Parish
     * @param stmtFeature    especially statement feature of parish
     * @param stmtParish     statement of Parish
     * @param functionOfName we find out id of this function in DB
     * @param parish         object of Parish
     * @throws SQLException in insertData we resolve SQL exception
     */
    public void loadParishFeatures(ArrayList<String> arrayList, PreparedStatement stmtOsoba, PreparedStatement stmtFeature, PreparedStatement stmtParish, String functionOfName, Parish parish) throws SQLException {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (String kaplan : arrayList) {
                setIntSetStringAndExecute(stmtOsoba, kaplan, getIdOfFunction(functionOfName));
                stmtFeature.setInt(1, getID(stmtOsoba));
                stmtFeature.setInt(2, getID(stmtParish));
                stmtFeature.execute();

            }
        }
    }

    /**
     * @param listOfParishes list of Parishes in especially district
     * @param idStore        in idStore we have id of eparchy and district for this list of Parishes
     * @throws SQLException in insertData we resolve SQL exception
     */
    public void loadParishes(List<Parish> listOfParishes, IdStore idStore) throws SQLException {
        String queryOsoba = "INSERT INTO `OSOBA` (NAZOV, FK_FUNKCIA) VALUES (?, ?)";
        String queryParish = "INSERT INTO `FARNOST` (NAZOV, FK_PROTOPRESBYTERAT, FK_OSOBA_SPRAVCA) VALUES (?, ?, ?)";
        String queryVypomocnyDuchovny = "INSERT INTO `VYPOMOCNYDUCHOVNY` (FK_OSOBA, FK_FARNOST) VALUES (?, ?)";
        String queryKaplan = "INSERT INTO `KAPLAN` (FK_OSOBA, FK_FARNOST) VALUES (?, ?)";
        String queryFilialka = "INSERT INTO `FILIALKA` (NAZOV, FK_FARNOST) VALUES (?, ?)";
        PreparedStatement stmtOsoba = con.prepareStatement(queryOsoba, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement stmtParish = con.prepareStatement(queryParish, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement stmtVypomocnyDuchovny = con.prepareStatement(queryVypomocnyDuchovny, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement stmtKaplan = con.prepareStatement(queryKaplan, Statement.RETURN_GENERATED_KEYS);
        PreparedStatement stmtFilialka = con.prepareStatement(queryFilialka, Statement.RETURN_GENERATED_KEYS);

        for (Parish parish : listOfParishes) {
            int id = setIdFunctionOfAdministrator(parish);
            setIntSetStringAndExecute(stmtOsoba, parish.getNameofSpravca(), id);
            stmtParish.setString(1, parish.getNameOfVillage());
            stmtParish.setInt(2, idStore.getId_dekanat());
            stmtParish.setInt(3, getID(stmtOsoba));
            stmtParish.executeUpdate();
            loadParishFeatures(parish.getKaplani(), stmtOsoba, stmtKaplan, stmtParish, "Kaplan", parish);
            loadParishFeatures(parish.getVypomocnyDuchovny(), stmtOsoba, stmtVypomocnyDuchovny, stmtParish, "Vypomocny Duchovny", parish);
            if (parish.getFilialky() != null && !parish.getFilialky().isEmpty()) {
                for (String filialka : parish.getFilialky()) {
                    setIntSetStringAndExecute(stmtFilialka, filialka, getID(stmtParish));
                }
            }
        }
    }

    /**
     * @param parish main priest in parish and setting Farar or Spravca farnosti accordint data in object parish
     * @return get id of function in table FUNKCIA
     */
    public int setIdFunctionOfAdministrator(Parish parish) {
        int id;
        if (parish.getFunctionOfAdministrator() == 2) {
            id = getIdOfFunction("Farar");
        } else if (parish.getFunctionOfAdministrator() == 1) {
            id = getIdOfFunction("Spravca farnosti");
        } else id = getIdOfFunction("Farar");
        return id;
    }

    /**
     * @param statement we find out id of new data which we update to DB
     * @return get ID of new data
     * @throws SQLException in insertData we resolve SQL exception
     */

    public int getID(PreparedStatement statement) throws SQLException {
        int id = 0;
        ResultSet generatedKeys = statement.getGeneratedKeys();
        int index = generatedKeys.findColumn("GENERATED_KEY");
        if (generatedKeys.next()) {
            id = generatedKeys.getInt(index);
        }
        return id;
    }

    /**
     * @param con connection with DB
     * @throws SQLException in insertData we resolve SQL exception
     */

    public void createFunctions(Connection con) throws SQLException {
        List<String> functions = Arrays.asList("Farar", "Spravca farnosti", "Kaplan", "Duchovny Spravca", "naOdpocinku", "Vypomocny Duchovny");
        String queryFunction = "INSERT INTO `FUNKCIA` (NAZOV) VALUES (?)";
        try (PreparedStatement stmtFunction = con.prepareStatement(queryFunction)) {
            for (String function : functions) {
                stmtFunction.setString(1, function);
                stmtFunction.executeUpdate();
            }
        }
    }

    /**
     * @param function we find out id of function from table FUNKCIA
     * @return get ID
     */

    public int getIdOfFunction(String function) {
        String query = "SELECT ID FROM FUNKCIA WHERE NAZOV = ?";
        int functionId = 0;
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, function);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                functionId = rs.getInt("ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return functionId;
    }

    /**
     * @param statement in this statement we set new data
     * @param nazov     first parameter
     * @param id        second parameter
     * @throws SQLException in insertData we resolve SQL exception
     */

    public void setIntSetStringAndExecute(PreparedStatement statement, String nazov, int id) throws SQLException {
        statement.setString(1, nazov);
        statement.setInt(2, id);
        statement.executeUpdate();
    }

    @Override
    public Parish getParishData(String city) {
        String querySelectId = "SELECT FARNOST.ID FROM FARNOST WHERE FARNOST.NAZOV = ?";
        String querySelectParish = "SELECT FARNOST.NAZOV AS FARNOST, EPARCHIA.NAZOV AS EPARCHIA, PROTOPRESBYTERAT.NAZOV AS PROTOPRESBYTERAT, PRIEST.NAZOV AS PRIEST  " +
                "FROM FARNOST " +
                "JOIN PROTOPRESBYTERAT ON FARNOST.FK_PROTOPRESBYTERAT = PROTOPRESBYTERAT.ID " +
                "JOIN EPARCHIA ON PROTOPRESBYTERAT.FK_EPARCHIA= EPARCHIA.ID " +
                "JOIN OSOBA AS PRIEST ON PRIEST.ID = FARNOST.FK_OSOBA_SPRAVCA " +
                "WHERE FARNOST.ID = ?";
        String querySelectFilialka = "SELECT FILIALKA.NAZOV AS FILIALKA FROM FILIALKA JOIN FARNOST ON FARNOST.ID = FILIALKA.FK_FARNOST WHERE FARNOST.ID = ?";
        String querySelectVypomocnyDuchovny = "SELECT OSOBA.NAZOV AS VYPOMOCNY \n" +
                "FROM FARNOST\n" +
                "JOIN VYPOMOCNYDUCHOVNY AS VYPOMOCNYD ON VYPOMOCNYD.FK_FARNOST = FARNOST.ID\n" +
                "JOIN OSOBA ON OSOBA.ID = VYPOMOCNYD.FK_OSOBA\n" +
                "WHERE FARNOST.ID = ?";
        String querySelectKaplan = "SELECT OSOBA.NAZOV AS KAPLAN_OSOBA \n" +
                "FROM FARNOST \n" +
                "JOIN KAPLAN ON KAPLAN.FK_FARNOST = FARNOST.ID\n" +
                "JOIN OSOBA ON OSOBA.ID = KAPLAN.FK_OSOBA\n" +
                "WHERE FARNOST.ID = ?";
        Parish parish = new Parish();
        try {
            int id = 0;
            PreparedStatement stmtSelectId = con.prepareStatement(querySelectId);
            PreparedStatement stmtSelectParish = con.prepareStatement(querySelectParish);
            PreparedStatement stmtSelectFilialka = con.prepareStatement(querySelectFilialka);
            PreparedStatement stmtSelectVypomocny = con.prepareStatement(querySelectVypomocnyDuchovny);
            PreparedStatement stmtSelectKaplan = con.prepareStatement(querySelectKaplan);
            stmtSelectId.setString(1, city);
            ResultSet resultSet = stmtSelectId.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("ID");
            } else System.out.println("Parish not found");
            stmtSelectParish.setInt(1, id);
            ResultSet rs = stmtSelectParish.executeQuery();

            while (rs.next()) {
                parish.setNameOfEparchy(rs.getString("EPARCHIA"));
                parish.setNameOfDistrict(rs.getString("PROTOPRESBYTERAT"));
                parish.setNameofSpravca(rs.getString("PRIEST"));
            }
            storeDataToParish(id, stmtSelectFilialka, parish.getFilialky(), "FILIALKA");
            storeDataToParish(id, stmtSelectKaplan, parish.getKaplani(), "KAPLAN_OSOBA");
            storeDataToParish(id, stmtSelectVypomocny, parish.getVypomocnyDuchovny(), "VYPOMOCNY");
            parish.setNameOfVillage(city);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return parish;
    }

    @Override
    public Connection getConnection() {
        return con;
    }

    private void storeDataToParish(int id, PreparedStatement statement, ArrayList<String> arrayList, String nameOfColumn) throws SQLException {
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            arrayList.add(rs.getString(nameOfColumn));
        }
    }

}
