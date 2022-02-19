package org.eazyportal.plugin.release.jenkins.action;

import org.eazyportal.plugin.release.core.SetSnapshotVersionAction;
import org.eazyportal.plugin.release.core.project.ProjectActions;
import org.eazyportal.plugin.release.core.scm.ScmActions;
import org.eazyportal.plugin.release.core.scm.model.ScmConfig;
import org.eazyportal.plugin.release.core.version.SnapshotVersionProvider;
import org.eazyportal.plugin.release.jenkins.ReleaseStepConfig;
import org.eazyportal.plugin.release.jenkins.project.ProjectActionsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SetSnapshotVersionActionFactoryTest {

    @Mock
    private transient ProjectActionsProvider projectActionsProvider;
    @Mock
    private transient ReleaseStepConfig releaseStepConfig;
    @Mock
    private transient SnapshotVersionProvider snapshotVersionProvider;

    @InjectMocks
    private SetSnapshotVersionActionFactory underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_create() {
        // GIVEN
        File workingDir = new File("");

        ProjectActions projectActions = mock(ProjectActions.class);
        ScmActions scmActions = mock(ScmActions.class);

        // WHEN
        when(projectActionsProvider.provide(workingDir)).thenReturn(projectActions);
        when(releaseStepConfig.getScmActions()).thenReturn(scmActions);
        when(releaseStepConfig.getScmConfig()).thenReturn(ScmConfig.getGIT_FLOW());

        // THEN
        SetSnapshotVersionAction actual = underTest.create(workingDir);

        assertThat(actual.getScmActions()).isEqualTo(scmActions);
        assertThat(actual.getScmConfig()).isEqualTo(ScmConfig.getGIT_FLOW());

        verifyNoInteractions(projectActions, scmActions, snapshotVersionProvider);
        verify(projectActionsProvider).provide(workingDir);
        verify(releaseStepConfig).getScmActions();
        verify(releaseStepConfig).getScmConfig();
        verifyNoMoreInteractions(projectActionsProvider, releaseStepConfig);
    }

}