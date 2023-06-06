package sk.pavlovsky.database;

public class DatabaseMain {
    public static void main(String[] args) {
        DatabaseBridge bridge = new ConcreteDatabaseBridge(new MySqlImplementor());
        bridge.implementor.connect();
        bridge.implementor.insertData(2,"Spravca farnosti");
        bridge.implementor.close();
    }
}
