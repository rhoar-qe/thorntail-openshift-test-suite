package io.thorntail.openshift.ts.common.db.allocator;

import java.util.Properties;

public final class DbAllocation {
    private final Properties props;

    DbAllocation(Properties props) {
        this.props = props;
    }

    String getUuid() {
        return props.getProperty("uuid");
    }

    public String getJdbcUrl() {
        return props.getProperty("db.jdbc_url");
    }

    public String getUsername() {
        return props.getProperty("db.username");
    }

    public String getPassword() {
        return props.getProperty("db.password");
    }
}
