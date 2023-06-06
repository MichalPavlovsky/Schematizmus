package sk.pavlovsky.database;

import sk.pavlovsky.database.interfaces.DatabaseImplementor;

abstract class DatabaseBridge {
    protected DatabaseImplementor implementor;

    public DatabaseBridge(DatabaseImplementor implementor) {
        this.implementor = implementor;
    }
    abstract void connect();
    abstract void close();
}
class ConcreteDatabaseBridge extends DatabaseBridge {
    public ConcreteDatabaseBridge(DatabaseImplementor implementor) {
        super(implementor);
    }

    @Override
    public void connect() {
        implementor.connect();
    }

    @Override
    public void close() {
        implementor.close();
    }
}
