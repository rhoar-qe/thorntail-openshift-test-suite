package io.thorntail.openshift.ts.sql.db;

import io.thorntail.openshift.ts.sql.db.util.DbAllocator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class AbstractExternalSqlDatabaseTest extends AbstractSqlDatabaseTest {
    private static final String DB_ALLOCATOR_SERVLET_URL = System.getProperty("db.allocator.url");

    private final String dbAllocatorLabel;
    private final String projectDefaultsYmlPath;
    private final DbAllocator dbAllocator;

    private String allocatedDbUuid;

    protected AbstractExternalSqlDatabaseTest(String dbAllocatorLabel, String projectDefaultsYmlPath) {
        this.dbAllocatorLabel = dbAllocatorLabel;
        this.projectDefaultsYmlPath = projectDefaultsYmlPath;

        this.dbAllocator = new DbAllocator(DB_ALLOCATOR_SERVLET_URL);
    }

    @Override
    protected void createDb() throws Exception {
        Properties props = dbAllocator.allocate(dbAllocatorLabel);
        allocatedDbUuid = props.getProperty("uuid");

        String config = new String(Files.readAllBytes(Paths.get(projectDefaultsYmlPath)), StandardCharsets.UTF_8)
                .replace("${db.jdbc.url}", props.getProperty("db.jdbc_url"))
                .replace("${db.username}", props.getProperty("db.username"))
                .replace("${db.password}", props.getProperty("db.password"));
        InputStream configInputStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
        openshift.deployAndRollout(configInputStream, APP_NAME, url);
    }

    @Override
    protected void dropDb() throws Exception {
        dbAllocator.free(allocatedDbUuid);
    }
}
