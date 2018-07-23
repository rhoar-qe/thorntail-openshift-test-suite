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

package io.thorntail.openshift.ts.db.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class DbAllocator {
    private static final String DEFAULT_URL = "http://dballocator.mw.lab.eng.bos.redhat.com:8080/Allocator/AllocatorServlet";
    private static DbAllocator INSTANCE;
    private final String url;

    public static DbAllocator initiate(String url) {
        if (url == null || url.length() == 0) {
            url = DEFAULT_URL;
        }

        INSTANCE = new DbAllocator(url);

        return INSTANCE;
    }

    public static DbAllocator getInstance() {
        if (INSTANCE == null) {
            initiate(null);
        }

        return INSTANCE;
    }

    private DbAllocator(String url) {
        this.url = url;
    }

    public Properties allocate(String label) throws Exception {
        String requestee = "xPaaS-test";
        StackTraceElement[] stack = new Throwable().getStackTrace();
        if (stack.length > 1) {
            StackTraceElement caller = stack[1]; // [0] = DbAllocator.allocate
            requestee += "-" + caller.getClassName() + "." + caller.getMethodName();
        }

        Map<String, String> params = new HashMap<>();

        params.put("expression", label);
        params.put("requestee", requestee);
        params.put("expiry", "60");

        try (InputStream is = performOperation("allocate", params)) {
            Properties props = new Properties();
            props.load(is);

            if (props.toString().contains("ResourceNotAvailableException")) {
              throw new RuntimeException("ResourceNotAvailableException: No resources found for allocation with label expression: " + label);
            }

            System.out.println("Returned props" + props.toString());
            return props;
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read DbAllocator response", ex);
        }
    }

    public void erase(String uuid) throws Exception {
        performOperation("erase", Collections.singletonMap("uuid", uuid));
    }

    public void free(String uuid) throws Exception {
        performOperation("dealloc", Collections.singletonMap("uuid", uuid));
    }

    private InputStream performOperation(String operation, Map<String, String> parameters) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(formatUrl(operation, parameters));

        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            return entity.getContent();
        }
       
        return null;
    }

    private String formatUrl(String operation, Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        sb.append("operation=").append(operation);// .append("&requestee=XPaas-tests");

        if (parameters != null) {
            for (Entry<String, String> parameter : parameters.entrySet()) {
                sb.append("&").append(parameter.getKey()).append("=").append(parameter.getValue());
            }
        }

        return sb.toString();
    }
}
