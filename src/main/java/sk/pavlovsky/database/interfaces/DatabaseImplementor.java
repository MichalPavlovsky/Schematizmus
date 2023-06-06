package sk.pavlovsky.database.interfaces;

public interface DatabaseImplementor {
    void connect();
    void close();
    void insertData(int i, String function);
}
