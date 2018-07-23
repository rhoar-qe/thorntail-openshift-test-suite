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
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Properties;

public class DbAllocatorDefaultsReplaceStringReader extends StringReader {

    private StringReader finalStringReader;

    public DbAllocatorDefaultsReplaceStringReader(String s, Properties dbAllocatorProps) {
        super(s);
        Enumeration<Object> keys = dbAllocatorProps.keys();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            s = s.replace(key, dbAllocatorProps.getProperty(key));
        }
        finalStringReader = new StringReader(s);
    }

    @Override
    public void close() {
        finalStringReader.close();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        finalStringReader.mark(readAheadLimit);
    }

    @Override
    public boolean markSupported() {
        return finalStringReader.markSupported();
    }

    @Override
    public int read() throws IOException {
        return finalStringReader.read();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return finalStringReader.read(cbuf, off, len);
    }

    @Override
    public boolean ready() throws IOException {
        return finalStringReader.ready();
    }

    @Override
    public void reset() throws IOException {
        finalStringReader.reset();
    }

    @Override
    public long skip(long ns) throws IOException {
        return finalStringReader.skip(ns);
    }
}
