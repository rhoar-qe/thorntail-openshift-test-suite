package io.thorntail.openshift.ts.sql.db;

import io.thorntail.openshift.ts.sql.db.util.DbAllocator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public abstract class AbstractExternalSqlDatabaseTest extends AbstractSqlDatabaseTest {
    private static final DbAllocator DB_ALLOCATOR = new DbAllocator(System.getProperty("db.allocator.url"));

    private final String dbAllocatorLabel;
    private final String projectDefaultsYmlPath;

    private static String allocatedDbUuid; // static because each test gets its own instance of the class :-(

    protected AbstractExternalSqlDatabaseTest(String dbAllocatorLabel, String projectDefaultsYmlPath) {
        this.dbAllocatorLabel = dbAllocatorLabel;
        this.projectDefaultsYmlPath = projectDefaultsYmlPath;
    }

    @Override
    protected void createDb() throws Exception {
        Properties props = DB_ALLOCATOR.allocate(dbAllocatorLabel);
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
        DB_ALLOCATOR.free(allocatedDbUuid);
    }
}
