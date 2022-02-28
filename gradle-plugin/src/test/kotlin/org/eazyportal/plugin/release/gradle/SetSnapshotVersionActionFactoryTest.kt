package org.eazyportal.plugin.release.gradle

import org.assertj.core.api.Assertions.assertThat
import org.eazyportal.plugin.release.core.project.ProjectActionsFactory
import org.eazyportal.plugin.release.core.scm.ScmActions
import org.eazyportal.plugin.release.core.scm.model.ScmConfig
import org.eazyportal.plugin.release.gradle.model.EazyReleasePluginExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class SetSnapshotVersionActionFactoryTest {

    private lateinit var underTest: SetSnapshotVersionActionFactory

    @BeforeEach
    fun setUp() {
        underTest = SetSnapshotVersionActionFactory()
    }

    @Test
    fun test_create() {
        val projectActionsFactory = mock<ProjectActionsFactory>()
        val extension = mock<EazyReleasePluginExtension>()

        val scmActions = mock<ScmActions>()
        val scmConfig = mock<ScmConfig>()

        // WHEN
        whenever(extension.scmActions).thenReturn(scmActions)
        whenever(extension.scmConfig).thenReturn(scmConfig)

        // THEN
        val actual = underTest.create(projectActionsFactory, extension)

        assertThat(actual.scmActions).isEqualTo(scmActions)
        assertThat(actual.scmConfig).isEqualTo(scmConfig)
        assertThat(actual).hasNoNullFieldsOrProperties()
    }

}
