package io.thorntail.openshift.ts.sql.db;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("/sqldb")
@ApplicationScoped
public class DbResource {
    @Resource
    private DataSource myDS;

    private void initDatabase() throws Exception {
        try (Connection conn = myDS.getConnection();
             PreparedStatement stmt = conn.prepareStatement("create table thorntail_test (id int, message varchar(30))")) {
            stmt.executeUpdate();
        }
    }

    private void endDatatabase() throws Exception {
        try (Connection conn = myDS.getConnection();
             PreparedStatement stmt = conn.prepareStatement("drop table thorntail_test")) {
            stmt.executeUpdate();
        }
    }

    @GET
    public Response ping() {
        return Response.ok().build();
    }

    @GET
    @Path("/select-one")
    public Response selectOne() throws Exception {
        initDatabase();

        try (Connection conn = myDS.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("insert into thorntail_test values (?, ?)")) {
                stmt.setInt(1, 1);
                stmt.setString(2, "first message");
                stmt.executeUpdate();
            }

            try (PreparedStatement statement = conn.prepareStatement("select count(*) from thorntail_test");
                 ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    int count = rs.getInt(1);

                    if (count == 1) {
                        return Response.ok().entity("OK").build();
                    }
                }
            }

            return Response.status(500).build();
        } finally {
            endDatatabase();
        }
    }

    @GET
    @Path("/select-many")
    public Response selectMany() throws Exception {
        initDatabase();

        try (Connection con = myDS.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement("insert into thorntail_test values (?, ?)")) {
                stmt.setInt(1, 1);
                stmt.setString(2, "first message");
                stmt.executeUpdate();

                stmt.setInt(1, 2);
                stmt.setString(2, "second message");
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = con.prepareStatement("select id, message from thorntail_test");
                 ResultSet rs = stmt.executeQuery()) {

                int count = 0;
                while (rs.next()) {
                    count++;
                }

                if (count == 2) {
                    return Response.ok().entity("OK").build();
                }
            }

            return Response.status(500).build();
        } finally {
            endDatatabase();
        }
    }

    @GET
    @Path("/update")
    public Response update() throws Exception {
        initDatabase();

        try (Connection conn = myDS.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("insert into thorntail_test values (?, ?)")) {
                stmt.setInt(1, 1);
                stmt.setString(2, "first message");
                stmt.executeUpdate();

                stmt.setInt(1, 2);
                stmt.setString(2, "second message");
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("update thorntail_test set message = ? where id = ?")) {
                stmt.setString(1, "hello world");
                stmt.setInt(2, 1);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("select message from thorntail_test where id = ?")) {
                stmt.setInt(1, 1);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String message = rs.getString(1);
                        if (message.contains("hello world")) {
                            return Response.ok().entity("OK").build();
                        }
                    }
                }
            }

            return Response.status(500).build();
        } finally {
            endDatatabase();
        }
    }

    @GET
    @Path("/delete")
    public Response delete() throws Exception {
        initDatabase();

        try (Connection conn = myDS.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("insert into thorntail_test values (?, ?)")) {
                stmt.setInt(1, 1);
                stmt.setString(2, "first message");
                stmt.executeUpdate();

                stmt.setInt(1, 2);
                stmt.setString(2, "second message");
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("delete from thorntail_test where id = ?")) {
                stmt.setInt(1, 1);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("select count(*) from thorntail_test");
                 ResultSet results = stmt.executeQuery()) {

                if (results.next()) {
                    int count = results.getInt(1);

                    if (count == 1) {
                        return Response.ok().entity("OK").build();
                    }
                }
            }

            return Response.status(500).build();
        } finally {
            endDatatabase();
        }
    }
}
