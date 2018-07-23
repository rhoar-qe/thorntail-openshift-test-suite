package io.thorntail.openshift.ts.sql.db;

import org.arquillian.cube.kubernetes.impl.utils.CommandExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractInternalSqlDatabaseTest extends AbstractSqlDatabaseTest {
    private final String image;
    private final File projectDefaultsYml;
    private final Map<String, String> environmentVariables;

    private final CommandExecutor cmd;

    protected AbstractInternalSqlDatabaseTest(String image, File projectDefaultsYml, Map<String, String> environmentVariables) {
        this.image = image;
        this.projectDefaultsYml = projectDefaultsYml;
        this.environmentVariables = environmentVariables;

        this.cmd = new CommandExecutor();
    }

    @Override
    public void createDb() throws Exception {
        cmd.execCommand("oc", "project", oc.getNamespace());

        List<String> createDbCommand = new ArrayList<>(Arrays.asList("oc", "new-app", image));
        for (Map.Entry<String, String> environmentVariable : environmentVariables.entrySet()) {
            createDbCommand.add("-e");
            createDbCommand.add(environmentVariable.getKey() + "=" + environmentVariable.getValue());
        }
        createDbCommand.add("--name=" + DB_APP_NAME);

        cmd.execCommand(createDbCommand.toArray(new String[0]));

        openshift.awaitDeploymentReadiness(DB_APP_NAME, 1);
        openshift.deployAndRollout(projectDefaultsYml, APP_NAME, url);
    }

    @Override
    public void dropDb() {
        cmd.execCommand("oc", "delete", "all", "-l", "app=" + DB_APP_NAME);
    }

    protected static Map<String, String> mapOf(String key1, String value1,
                                               String key2, String value2,
                                               String key3, String value3) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        return result;
    }
}
