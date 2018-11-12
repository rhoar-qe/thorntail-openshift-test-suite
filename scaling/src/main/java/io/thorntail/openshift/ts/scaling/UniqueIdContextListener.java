package io.thorntail.openshift.ts.scaling;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.UUID;

@WebListener
public class UniqueIdContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("uniqueId", UUID.randomUUID().toString());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
