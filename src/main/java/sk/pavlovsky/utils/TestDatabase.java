package sk.pavlovsky.utils;

public class TestDatabase {
    public static void main(String[] args) {
        Database db = new Database();
        try {
            db.connect();
            db.insertData(3,"Lukas");
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }
}

