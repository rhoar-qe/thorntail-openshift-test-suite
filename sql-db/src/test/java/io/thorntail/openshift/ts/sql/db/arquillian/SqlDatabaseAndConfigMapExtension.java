package io.thorntail.openshift.ts.sql.db.arquillian;

import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.arquillian.test.spi.event.suite.ClassLifecycleEvent;

import java.util.logging.Logger;

public class SqlDatabaseAndConfigMapExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(SqlDatabaseAndConfigMapDeployer.class);
    }

    private static class SqlDatabaseAndConfigMapDeployer {
        private static final Logger log = Logger.getLogger(SqlDatabaseAndConfigMapDeployer.class.getName());

        @Inject
        private Instance<Injector> injector;

        // precedence of these observers must be higher than the precedence of @OpenShiftResource observers
        // in Arquillian Cube, because the database must be deployed sooner / undeployed after the app
        // (which is deployed/undeployed using @OpenShiftResource)

        public void deploy(@Observes(precedence = 1000) BeforeClass event) throws Exception {
            SqlDatabaseAndConfigMapStrategy strategy = strategy(event);
            log.info("Deploying " + strategy.getClass().getName());
            strategy.deploy();
        }

        public void undeploy(@Observes(precedence = 1000) AfterClass event) throws Exception {
            SqlDatabaseAndConfigMapStrategy strategy = strategy(event);
            log.info("Undeploying " + strategy.getClass().getName());
            strategy.undeploy();
        }

        private SqlDatabaseAndConfigMapStrategy strategy(ClassLifecycleEvent event) throws ReflectiveOperationException {
            if (event.getTestClass().isAnnotationPresent(SqlDatabaseAndConfigMap.class)) {
                Class<? extends SqlDatabaseAndConfigMapStrategy> maintainer = event.getTestClass().getAnnotation(SqlDatabaseAndConfigMap.class).value();
                SqlDatabaseAndConfigMapStrategy instance = maintainer.newInstance();
                return injector.get().inject(instance);
            }

            return SqlDatabaseAndConfigMapStrategy.NOOP;
        }
    }
}
