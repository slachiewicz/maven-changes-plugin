/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.changes;

import org.apache.maven.api.Project;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.plugin.Log;
import org.apache.maven.api.plugin.Mojo;
import org.apache.maven.api.plugin.annotations.Parameter;

/**
 * Base class for the non-report goals of this plugin.
 */
public abstract class AbstractChangesMojo implements Mojo {
    /**
     * The current project.
     */
    @Inject
    protected Project project;

    @Inject
    private Log log;

    /**
     * This will cause the execution to be run only at the top of a given module tree. That is, run in the project
     * contained in the same folder where the mvn execution was launched.
     *
     * @since 2.9
     */
    @Parameter(property = "changes.runOnlyAtExecutionRoot", defaultValue = "false")
    protected boolean runOnlyAtExecutionRoot;

    protected Log getLog() {
        return log;
    }

    /**
     * Returns <code>true</code> if the current project is located at the Execution Root Directory (where mvn was
     * launched).
     *
     * @return <code>true</code> if the current project is at the Execution Root
     */
    protected boolean isThisTheExecutionRoot() {
        boolean result = project.isTopProject();
        if (result) {
            getLog().debug("This is the execution root.");
        } else {
            getLog().debug("This is NOT the execution root.");
        }
        return result;
    }
}
