package io.thorntail.openshift.ts.sql.db;

import io.thorntail.openshift.ts.common.db.allocator.DbAllocation;
import io.thorntail.openshift.ts.common.db.allocator.DbAllocator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class AbstractExternalSqlDatabaseTest extends AbstractSqlDatabaseTest {
    private static final DbAllocator DB_ALLOCATOR = new DbAllocator(System.getProperty("db.allocator.url"));

    private final String dbAllocatorLabel;
    private final String projectDefaultsYmlPath;

    private static DbAllocation allocatedDb; // static because each test gets its own instance of this class :-(

    protected AbstractExternalSqlDatabaseTest(String dbAllocatorLabel, String projectDefaultsYmlPath) {
        this.dbAllocatorLabel = dbAllocatorLabel;
        this.projectDefaultsYmlPath = projectDefaultsYmlPath;
    }

    @Override
    protected void createDb() throws Exception {
        allocatedDb = DB_ALLOCATOR.allocate(dbAllocatorLabel);

        String config = new String(Files.readAllBytes(Paths.get(projectDefaultsYmlPath)), StandardCharsets.UTF_8)
                .replace("${db.jdbc.url}", allocatedDb.getJdbcUrl())
                .replace("${db.username}", allocatedDb.getUsername())
                .replace("${db.password}", allocatedDb.getPassword());
        InputStream configInputStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
        openshift.deployAndRollout(configInputStream, APP_NAME, url);
    }

    @Override
    protected void dropDb() throws Exception {
        DB_ALLOCATOR.free(allocatedDb);
    }
}
