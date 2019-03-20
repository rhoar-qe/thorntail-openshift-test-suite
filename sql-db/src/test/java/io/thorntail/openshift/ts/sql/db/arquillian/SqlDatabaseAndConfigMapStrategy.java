package io.thorntail.openshift.ts.sql.db.arquillian;

public interface SqlDatabaseAndConfigMapStrategy {
    void deploy() throws Exception;

    void undeploy() throws Exception;

    SqlDatabaseAndConfigMapStrategy NOOP = new SqlDatabaseAndConfigMapStrategy() {
        @Override
        public void deploy() {
        }

        @Override
        public void undeploy() {
        }
    };
}
