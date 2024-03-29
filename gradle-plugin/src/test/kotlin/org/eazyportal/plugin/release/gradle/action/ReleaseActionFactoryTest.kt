package org.eazyportal.plugin.release.gradle.action

import org.assertj.core.api.Assertions.assertThat
import org.eazyportal.plugin.release.core.TestFixtures.ACTION_CONTEXT
import org.eazyportal.plugin.release.core.TestFixtures.CONVENTIONAL_COMMIT_TYPES
import org.eazyportal.plugin.release.core.action.FinalizeReleaseVersionAction
import org.eazyportal.plugin.release.core.action.FinalizeSnapshotVersionAction
import org.eazyportal.plugin.release.core.action.PrepareRepositoryForReleaseAction
import org.eazyportal.plugin.release.core.action.ReleaseAction
import org.eazyportal.plugin.release.core.action.SetReleaseVersionAction
import org.eazyportal.plugin.release.core.action.SetSnapshotVersionAction
import org.eazyportal.plugin.release.core.action.UpdateScmAction
import org.eazyportal.plugin.release.core.project.ProjectDescriptorFactory
import org.eazyportal.plugin.release.core.project.model.ProjectDescriptor
import org.eazyportal.plugin.release.core.scm.model.ScmConfig
import org.eazyportal.plugin.release.gradle.model.EazyReleasePluginExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import kotlin.reflect.KClass

internal class ReleaseActionFactoryTest {

    @Mock
    private lateinit var actionContextFactory: ActionContextFactory

    @Mock
    private lateinit var projectDescriptorFactory: ProjectDescriptorFactory<File>

    @InjectMocks
    private lateinit var underTest: ReleaseActionFactory

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @MethodSource("releaseActions")
    @ParameterizedTest
    fun <T : ReleaseAction> test_create(
        expectedReleaseActionClass: KClass<T>,
        resultProvider: (ReleaseActionFactory, Project) -> T
    ) {
        val extension = mock<EazyReleasePluginExtension> {
            whenever(it.conventionalCommitTypes).thenReturn(CONVENTIONAL_COMMIT_TYPES)
            whenever(it.projectActionsFactory).thenReturn(mock())
            whenever(it.scmActions).thenReturn(mock())
            whenever(it.scmConfig).thenReturn(ScmConfig.GIT_FLOW)
        }

        val extensionContainer = mock<ExtensionContainer> {
            whenever(it.getByType(EazyReleasePluginExtension::class.java)).thenReturn(extension)
        }

        val project = mock<Project> {
            whenever(it.providers).thenReturn(mock())
            whenever(it.extensions).thenReturn(extensionContainer)
            whenever(it.projectDir).thenReturn(mock())
        }

        whenever(actionContextFactory.create(any())).thenReturn(ACTION_CONTEXT)

        whenever(projectDescriptorFactory.create(any(), any(), any())).thenReturn(PROJECT_DESCRIPTOR)

        assertThat(resultProvider.invoke(underTest, project))
            .isInstanceOf(expectedReleaseActionClass.java)
    }

    companion object {
        private val PROJECT_DESCRIPTOR = ProjectDescriptor<File>(
            rootProject = org.eazyportal.plugin.release.core.project.model.Project(
                dir = mock(),
                projectActions = mock()
            ),
            allProjects = emptyList(),
            subProjects = emptyList()
        )

        @JvmStatic
        private fun releaseActions(): List<Arguments> {
            return listOf(
                Arguments.of(
                    FinalizeReleaseVersionAction::class,
                    { underTest: ReleaseActionFactory, project: Project ->
                        underTest.create<FinalizeReleaseVersionAction<File>>(project)
                    }
                ),
                Arguments.of(
                    FinalizeSnapshotVersionAction::class,
                    { underTest: ReleaseActionFactory, project: Project ->
                        underTest.create<FinalizeSnapshotVersionAction<File>>(project)
                    }
                ),
                Arguments.of(
                    PrepareRepositoryForReleaseAction::class,
                    { underTest: ReleaseActionFactory, project: Project ->
                        underTest.create<PrepareRepositoryForReleaseAction<File>>(project)
                    }
                ),
                Arguments.of(
                    SetReleaseVersionAction::class,
                    { underTest: ReleaseActionFactory, project: Project ->
                        underTest.create<SetReleaseVersionAction<File>>(project)
                    }
                ),
                Arguments.of(
                    SetSnapshotVersionAction::class,
                    { underTest: ReleaseActionFactory, project: Project ->
                        underTest.create<SetSnapshotVersionAction<File>>(project)
                    }
                ),
                Arguments.of(
                    UpdateScmAction::class,
                    { underTest: ReleaseActionFactory, project: Project ->
                        underTest.create<UpdateScmAction<File>>(project)
                    }
                )
            )
        }
    }

}
