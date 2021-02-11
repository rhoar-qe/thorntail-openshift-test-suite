package io.thorntail.openshift.ts.sql.db.infra;

import io.thorntail.openshift.test.util.OpenShiftUtil;
import io.thorntail.openshift.ts.sql.db.infra.allocator.DbAllocation;
import io.thorntail.openshift.ts.sql.db.infra.allocator.DbAllocator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class AbstractExternalSqlDatabaseAndConfigMap extends AbstractSqlDatabaseAndConfigMap {
    private static final DbAllocator DB_ALLOCATOR = new DbAllocator(System.getProperty("db.allocator.url"));

    private final String dbAllocatorLabel;
    private final String projectDefaultsYmlPath;

    private static DbAllocation allocatedDb; // static because we don't store the instance anywhere :-/
    private static String configMapContent;

    protected AbstractExternalSqlDatabaseAndConfigMap(OpenShiftUtil openshift, String dbAllocatorLabel, String projectDefaultsYmlPath) {
        super(openshift);
        this.dbAllocatorLabel = dbAllocatorLabel;
        this.projectDefaultsYmlPath = projectDefaultsYmlPath;
    }

    @Override
    public void deploy() throws Exception {
        allocatedDb = DB_ALLOCATOR.allocate(dbAllocatorLabel);

        configMapContent = new String(Files.readAllBytes(Paths.get(projectDefaultsYmlPath)), StandardCharsets.UTF_8)
                .replace("${db.jdbc.url}", allocatedDb.getJdbcUrl())
                .replace("${db.username}", allocatedDb.getUsername())
                .replace("${db.password}", allocatedDb.getPassword());
        InputStream configMapInputStream = new ByteArrayInputStream(configMapContent.getBytes(StandardCharsets.UTF_8));
        openshift.applyYaml(configMapInputStream);
    }

    @Override
    public void undeploy() throws Exception {
        InputStream configMapInputStream = new ByteArrayInputStream(configMapContent.getBytes(StandardCharsets.UTF_8));
        openshift.deleteYaml(configMapInputStream);
        DB_ALLOCATOR.free(allocatedDb);
    }
}
