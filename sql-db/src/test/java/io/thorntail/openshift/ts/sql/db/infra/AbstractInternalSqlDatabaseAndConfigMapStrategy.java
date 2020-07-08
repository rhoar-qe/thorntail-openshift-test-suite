package io.thorntail.openshift.ts.sql.db.infra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.cube.kubernetes.impl.utils.CommandExecutor;

public abstract class AbstractInternalSqlDatabaseAndConfigMapStrategy extends AbstractSqlDatabaseAndConfigMapStrategy {
    private final String image;
    private final File projectDefaultsYml;
    private final Map<String, String> environmentVariables;
    private final int port;

    private final CommandExecutor cmd;

    protected AbstractInternalSqlDatabaseAndConfigMapStrategy(String defaultImage, String imageFromCL, File projectDefaultsYml,
                                                              Map<String, String> environmentVariables, int port) {

        this.image = getImage(defaultImage, imageFromCL);
        System.out.println("Image to be used: " + image);
        this.projectDefaultsYml = projectDefaultsYml;
        this.environmentVariables = environmentVariables;
        this.port = port;

        this.cmd = new CommandExecutor();
    }

    @Override
    public void deploy() throws IOException {
        cmd.execCommand("oc", "project", oc().getNamespace());

        cmd.execCommand("oc", "create", "dc", DB_APP_NAME, "--image=" + image);

        cmd.execCommand("oc", "label", "dc", DB_APP_NAME, "app=" + DB_APP_NAME);

        List<String> setEnvCommand = new ArrayList<>(Arrays.asList("oc", "set", "env", "dc", DB_APP_NAME));
        for (Map.Entry<String, String> environmentVariable : environmentVariables.entrySet()) {
            setEnvCommand.add(environmentVariable.getKey() + "=" + environmentVariable.getValue());
        }
        cmd.execCommand(setEnvCommand.toArray(new String[0]));

        cmd.execCommand("oc", "expose", "dc", DB_APP_NAME, "--port=" + port);

        openshift().awaitDeploymentReadiness(DB_APP_NAME, 1);
        openshift().applyYaml(projectDefaultsYml);
    }

    @Override
    public void undeploy() throws IOException {
        openshift().deleteYaml(projectDefaultsYml);
        cmd.execCommand("oc", "delete", "all", "-l", "app=" + DB_APP_NAME);
    }

    protected String getImage(String defaultImage, String fromCL) {
        final String imageFromCL = System.getProperty(fromCL);
        if (imageFromCL == null || imageFromCL.isEmpty()) {
            return defaultImage;
        } else {
            return imageFromCL;
        }
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
