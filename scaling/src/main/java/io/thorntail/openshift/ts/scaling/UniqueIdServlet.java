package io.thorntail.openshift.ts.scaling;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class UniqueIdServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uniqueId = (String) req.getServletContext().getAttribute("uniqueId");
        resp.getWriter().println(uniqueId);
    }
}
