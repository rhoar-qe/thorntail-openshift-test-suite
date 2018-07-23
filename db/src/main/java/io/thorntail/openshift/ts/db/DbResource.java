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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/sqldb")
@ApplicationScoped
public class DbResource {
    private static final String WF_TEST_TABLE = "wfswarm_test";

    @Resource
    private DataSource myDS;

    @GET
    public Response ping() throws Exception {
        return Response.ok().entity("PONG").build();
    }

    @GET
    @Path("/query")
    public Response query() throws Exception {

        Connection con = null;
        try {

            initDatabase();
            con = myDS.getConnection();

            try (PreparedStatement statement = con
                    .prepareStatement("insert into " + WF_TEST_TABLE + " values (?, ?)")) {
                statement.setInt(1, 1);
                statement.setString(2, "first message");
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("select count(*) from " + WF_TEST_TABLE)) {

                ResultSet results = statement.executeQuery();
                results.next();
                int count = results.getInt(1);

                results.close();
                if (count == 1) {
                    return Response.ok().entity("OK").build();
                }
            }

            return Response.status(404).build();
        } finally {
            if (con != null) {
                con.close();
            }
            endDatatabase();
        }
    }

    @GET
    @Path("/update")
    public Response update() throws Exception {

        Connection con = null;
        try {
            initDatabase();

            con = myDS.getConnection();

            try (PreparedStatement statement = con
                    .prepareStatement("insert into " + WF_TEST_TABLE + " values (?, ?)")) {
                statement.setInt(1, 1);
                statement.setString(2, "first message");
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con
                    .prepareStatement("insert into " + WF_TEST_TABLE + " values (?, ?)")) {
                statement.setInt(1, 2);
                statement.setString(2, "second message");
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con
                    .prepareStatement("update " + WF_TEST_TABLE + " set message = ? where id = ?")) {
                statement.setString(1, "hellow world");
                statement.setInt(2, 1);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con
                    .prepareStatement("select message from " + WF_TEST_TABLE + " where id = ?")) {
                statement.setInt(1, 1);
                ResultSet results = statement.executeQuery();
                results.next();
                String message = results.getString(1);

                results.close();
                if (message.contains("hellow world")) {
                    return Response.ok().entity("OK").build();
                }
            }

            return Response.status(404).build();
        } finally {
            if (con != null) {
                con.close();
            }
            endDatatabase();
        }
    }

    @GET
    @Path("/delete")
    public Response delete() throws Exception {

        Connection con = null;
        try {

            initDatabase();
            con = myDS.getConnection();

            try (PreparedStatement statement = con
                    .prepareStatement("insert into " + WF_TEST_TABLE + " values (?, ?)")) {
                statement.setInt(1, 1);
                statement.setString(2, "first message");
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con
                    .prepareStatement("insert into " + WF_TEST_TABLE + " values (?, ?)")) {
                statement.setInt(1, 2);
                statement.setString(2, "second message");
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("delete from " + WF_TEST_TABLE + " where id = ?")) {
                statement.setInt(1, 1);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("select count(*) from " + WF_TEST_TABLE)) {

                ResultSet results = statement.executeQuery();
                results.next();
                int count = results.getInt(1);

                results.close();
                if (count == 1) {
                    return Response.ok().entity("OK").build();
                }
            }

            return Response.status(404).build();
        } finally {
            if (con != null) {
                con.close();
            }
            endDatatabase();
        }
    }

    @GET
    @Path("/selectall")
    public Response selectall() throws Exception {
        Connection con = null;
        try {
            initDatabase();

            con = myDS.getConnection();

            try (PreparedStatement statement = con
                    .prepareStatement("insert into " + WF_TEST_TABLE + " values (?, ?)")) {
                statement.setInt(1, 1);
                statement.setString(2, "first message");
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con
                    .prepareStatement("insert into " + WF_TEST_TABLE + " values (?, ?)")) {
                statement.setInt(1, 2);
                statement.setString(2, "second message");
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("select id, message from " + WF_TEST_TABLE)) {

                ResultSet results = statement.executeQuery();
                int count = 0;
                while (results.next()) {
                    count++;
                }
                results.close();

                if (count == 2) {
                    return Response.ok().entity("OK").build();
                }
            }

            return Response.status(404).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        } finally {
            if (con != null) {
                con.close();
            }
            endDatatabase();
        }
    }

    private void initDatabase() throws Exception {
        try (Connection con = myDS.getConnection();
                PreparedStatement statement = con
                        .prepareStatement("create table " + WF_TEST_TABLE + " (id int, message varchar(30))")) {
            statement.executeUpdate();
        }
    }

    private void endDatatabase() throws Exception {
        try (Connection con = myDS.getConnection();
                PreparedStatement statement = con.prepareStatement("drop table " + WF_TEST_TABLE)) {
            statement.executeUpdate();
        }
    }
}