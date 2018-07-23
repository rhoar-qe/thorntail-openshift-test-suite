/*
 *
 *  Copyright 2018 Red Hat, Inc, and individual contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.thorntail.openshift.ts.db;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;
import org.postgresql.util.ReaderInputStream;

import io.thorntail.openshift.ts.db.util.DbAllocator;
import io.thorntail.openshift.ts.db.util.DbAllocatorDefaultsReplaceStringReader;
import io.thorntail.openshift.ts.utils.OpenShiftUtils;

@RunWith(Arquillian.class)
public class DbExternalMySQLIT extends AbstractDBOpenshiftTest {

    @Override
    protected void initDB() throws Exception {
        Properties externalDbProperties = DbAllocator.getInstance().allocate("mysql57");

        byte[] dbDefaultsBytes = Files.readAllBytes(new File("target/test-classes/project-defaults-external-mysql.yml").toPath());
        String externalDefaults = new String(dbDefaultsBytes, StandardCharsets.UTF_8);

        StringReader reader = new DbAllocatorDefaultsReplaceStringReader(externalDefaults, externalDbProperties);

        OpenShiftUtils.deployConfigMapAndRollout(openShiftAssistant, new ReaderInputStream(reader), APP_NAME, url);

    }

}
