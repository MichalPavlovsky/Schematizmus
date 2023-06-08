package sk.pavlovsky.database;

public class DatabaseMain {
    public static void main(String[] args) {
        DatabaseBridge bridge = new ConcreteDatabaseBridge(new MySqlImplementor());
        bridge.implementor.connect();
        bridge.implementor.getParishData("Sabinov");
        bridge.implementor.close();
    }
}
