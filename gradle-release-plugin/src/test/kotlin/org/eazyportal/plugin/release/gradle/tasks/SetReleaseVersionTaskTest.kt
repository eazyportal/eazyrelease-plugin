package org.eazyportal.plugin.release.gradle.tasks

import org.eazyportal.plugin.release.core.SetReleaseVersionAction
import org.eazyportal.plugin.release.core.scm.ConventionalCommitType
import org.eazyportal.plugin.release.core.version.model.VersionIncrement.MAJOR
import org.eazyportal.plugin.release.gradle.EazyReleasePlugin.Companion.SET_RELEASE_VERSION_TASK_NAME
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

internal class SetReleaseVersionTaskTest : EazyBaseTaskTest<SetReleaseVersionTask>() {

    companion object {
        @JvmStatic
        fun run() = listOf(
            Arguments.of(listOf<ConventionalCommitType>()),
            Arguments.of(listOf(ConventionalCommitType(listOf("dummy"), MAJOR))),
            Arguments.of(ConventionalCommitType.DEFAULT_TYPES)
        )
    }

    @Mock
    private lateinit var setReleaseVersionAction: SetReleaseVersionAction

    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)

        underTest = project.tasks.create(SET_RELEASE_VERSION_TASK_NAME, SetReleaseVersionTask::class.java, setReleaseVersionAction)
            .also {
                it.scmActions.set(scmActions)
                it.scmConfig.set(scmConfig)
            }
    }

    @MethodSource("run")
    @ParameterizedTest
    fun test_run(conventionalCommitTypes: List<ConventionalCommitType>) {
        // GIVEN
        underTest.conventionalCommitTypes.set(conventionalCommitTypes)

        // WHEN
        // THEN
        underTest.run()

        verify(setReleaseVersionAction).conventionalCommitTypes = conventionalCommitTypes
        verify(setReleaseVersionAction).scmActions = scmActions
        verify(setReleaseVersionAction).scmConfig = scmConfig
        verify(setReleaseVersionAction).execute(project.rootDir)
        verifyNoMoreInteractions(setReleaseVersionAction)
    }

}