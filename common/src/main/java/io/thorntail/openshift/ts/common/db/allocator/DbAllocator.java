package io.thorntail.openshift.ts.common.db.allocator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public final class DbAllocator {
    private final String url;

    public DbAllocator(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("DbAllocator URL must be provided");
        }
        if (url.contains("AllocatorServlet")) {
            throw new IllegalArgumentException("Only http://db.allocator.host/ expected, not the full AllocatorServlet URL");
        }

        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "Allocator/AllocatorServlet";
        this.url = url;
    }

    public DbAllocation allocate(String label) throws Exception {
        String requestee = "Thorntail-TS";
        StackTraceElement[] stack = new Throwable().getStackTrace();
        if (stack.length > 1) {
            StackTraceElement caller = stack[1]; // [0] = DbAllocator.allocate
            requestee += "-" + caller.getClassName() + "." + caller.getMethodName();
        }

        Map<String, String> params = new HashMap<>();

        params.put("expression", label);
        params.put("requestee", requestee);
        params.put("expiry", "60");

        try {
            String response = performOperation("allocate", params);
            Properties props = new Properties();
            props.load(new StringReader(response));

            if (props.toString().contains("ResourceNotAvailableException")) {
                throw new RuntimeException("ResourceNotAvailableException: No resources found for allocation with label expression: " + label);
            }

            return new DbAllocation(props);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read DbAllocator response", ex);
        }
    }

    public void erase(DbAllocation allocation) throws Exception {
        performOperation("erase", Collections.singletonMap("uuid", allocation.getUuid()));
    }

    public void free(DbAllocation allocation) throws Exception {
        performOperation("dealloc", Collections.singletonMap("uuid", allocation.getUuid()));
    }

    private String performOperation(String operation, Map<String, String> parameters) throws Exception {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(formatUrl(operation, parameters));

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        }

        return null;
    }

    private String formatUrl(String operation, Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(url)
                .append("?")
                .append("operation=")
                .append(operation);

        if (parameters != null) {
            for (Entry<String, String> parameter : parameters.entrySet()) {
                sb.append("&")
                        .append(parameter.getKey())
                        .append("=")
                        .append(URLEncoder.encode(parameter.getValue(), StandardCharsets.UTF_8.name()));
            }
        }

        return sb.toString();
    }
}
