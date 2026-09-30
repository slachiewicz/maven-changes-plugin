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
package org.apache.maven.plugins.changes.github;

import java.io.IOException;

import org.apache.maven.api.Project;
import org.apache.maven.api.model.IssueManagement;
import org.apache.maven.api.model.Model;
import org.apache.maven.api.plugin.Log;
import org.apache.maven.api.settings.Server;
import org.apache.maven.api.settings.Settings;
import org.apache.maven.plugins.changes.issues.Issue;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GitHubDownloaderTest {

    @Test
    public void testCreateIssue() throws IOException {
        IssueManagement issueManagement = newGitHubIssueManagement();
        GitHubDownloader gitHubDownloader = newGitHubDownloader(issueManagement);

        GHIssue githubIssue = mock(GHIssue.class);
        when(githubIssue.getNumber()).thenReturn(1);
        when(githubIssue.getTitle()).thenReturn("Title");
        when(githubIssue.getBody()).thenReturn("Body");
        when(githubIssue.getUser()).thenReturn(new GHUser());
        when(githubIssue.getState()).thenReturn(GHIssueState.OPEN);

        Issue issue = gitHubDownloader.createIssue(githubIssue);

        assertEquals(Integer.toString(githubIssue.getNumber()), issue.getId());
        assertEquals(Integer.toString(githubIssue.getNumber()), issue.getKey());
        assertEquals(githubIssue.getTitle(), issue.getSummary());
        assertEquals(githubIssue.getState().name(), issue.getStatus());
        assertEquals(issueManagement.getUrl() + githubIssue.getNumber(), issue.getLink());
    }

    @Test
    public void testConfigureAuthenticationWithServer() throws Exception {
        GitHubDownloader gitHubDownloader = newGitHubDownloader(newGitHubIssueManagement());
        Settings settings = Settings.newBuilder()
                .servers(java.util.Collections.singletonList(newServer("github-server")))
                .build();
        Log log = mock(Log.class);

        gitHubDownloader.configureAuthentication("github-server", settings, log);

        verify(log, never()).warn(anyString());
    }

    @Test
    public void testConfigureAuthenticationWithNoServer() throws Exception {
        GitHubDownloader gitHubDownloader = newGitHubDownloader(newGitHubIssueManagement());
        Settings settings = Settings.newBuilder()
                .servers(java.util.Collections.singletonList(newServer("not-the-right-one")))
                .build();
        Log log = mock(Log.class);

        gitHubDownloader.configureAuthentication("github-server", settings, log);

        verify(log).warn("Can't find server id [github-server] configured in settings.xml");
    }

    private Server newServer(String id) {
        return Server.newBuilder()
                .id(id)
                .username("some-user")
                .password("Sup3rSecret")
                .build();
    }

    private GitHubDownloader newGitHubDownloader(IssueManagement issueManagement) throws IOException {
        Project project = mock(Project.class);
        when(project.getModel())
                .thenReturn(Model.newBuilder().issueManagement(issueManagement).build());
        return new GitHubDownloader(project, true, false);
    }

    private IssueManagement newGitHubIssueManagement() {
        return IssueManagement.newBuilder()
                .system("GitHub")
                .url("https://github.com/dadoonet/spring-elasticsearch/issues/")
                .build();
    }
}
