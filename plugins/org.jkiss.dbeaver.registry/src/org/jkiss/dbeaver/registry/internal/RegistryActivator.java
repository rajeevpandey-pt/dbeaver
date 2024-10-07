/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2024 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.registry.internal;

import org.jkiss.api.DriverReference;
import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.connection.DBPDriver;
import org.jkiss.dbeaver.model.runtime.LoggingProgressMonitor;
import org.jkiss.dbeaver.registry.DataSourceProviderRegistry;
import org.jkiss.spi.JdbcDriverInstanceProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.sql.Driver;

public class RegistryActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(JdbcDriverInstanceProvider.class, new DriverInstanceProvider(), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // nothing to do
    }

    private static class DriverInstanceProvider implements JdbcDriverInstanceProvider {
        private static final Log log = Log.getLog(DriverInstanceProvider.class);

        @Nullable
        @Override
        public Driver get(@NotNull DriverReference ref) {
            DBPDriver driver = DataSourceProviderRegistry.getInstance().findDriver(ref);
            if (driver == null) {
                log.debug("Driver '" + ref + "' not found in the registry");
                return null;
            }
            try {
                return driver.getDriverInstance(new LoggingProgressMonitor());
            } catch (DBException e) {
                log.error("Error getting driver instance", e);
                return null;
            }
        }

        @Override
        public boolean provides(@NotNull DriverReference ref) {
            return DataSourceProviderRegistry.getInstance().findDriver(ref) != null;
        }
    }
}
