package io.thorntail.openshift.ts.sql.db.infra;

import io.thorntail.openshift.test.Command;
import io.thorntail.openshift.test.util.OpenShiftUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

public abstract class AbstractInternalSqlDatabaseAndConfigMap extends AbstractSqlDatabaseAndConfigMap {
    private final String image;
    private final File projectDefaultsYml;
    private final Map<String, String> environmentVariables;
    private final int port;

    protected AbstractInternalSqlDatabaseAndConfigMap(OpenShiftUtil openshift,
                                                      String defaultImage, String imageFromCL, File projectDefaultsYml,
                                                      Map<String, String> environmentVariables, int port) {

        super(openshift);

        this.image = getImage(defaultImage, imageFromCL);
        this.projectDefaultsYml = projectDefaultsYml;
        this.environmentVariables = environmentVariables;
        this.port = port;
    }

    @Override
    public void deploy() throws IOException, InterruptedException {
        System.out.println(ansi().a("deploying database ").fgYellow().a(image).reset());

        new Command("oc", "create", "dc", DB_APP_NAME, "--image=" + image).runAndWait();

        new Command("oc", "label", "dc", DB_APP_NAME, "app=" + DB_APP_NAME).runAndWait();

        List<String> setEnvCommand = new ArrayList<>(Arrays.asList("oc", "set", "env", "dc", DB_APP_NAME));
        for (Map.Entry<String, String> environmentVariable : environmentVariables.entrySet()) {
            setEnvCommand.add(environmentVariable.getKey() + "=" + environmentVariable.getValue());
        }
        new Command(setEnvCommand.toArray(new String[0])).runAndWait();

        new Command("oc", "expose", "dc", DB_APP_NAME, "--port=" + port).runAndWait();

        openshift.awaitDeploymentReadiness(DB_APP_NAME, 1);
        openshift.applyYaml(projectDefaultsYml);
    }

    @Override
    public void undeploy() throws IOException, InterruptedException {
        System.out.println(ansi().a("undeploying database ").fgYellow().a(image).reset());

        openshift.deleteYaml(projectDefaultsYml);
        new Command("oc", "delete", "all", "-l", "app=" + DB_APP_NAME).runAndWait();
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
