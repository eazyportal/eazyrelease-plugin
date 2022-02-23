package org.eazyportal.plugin.release.gradle

import org.eazyportal.plugin.release.core.SetReleaseVersionAction
import org.eazyportal.plugin.release.core.SetSnapshotVersionAction
import org.eazyportal.plugin.release.core.UpdateScmAction
import org.eazyportal.plugin.release.core.project.ProjectActionsFactory
import org.eazyportal.plugin.release.core.version.ReleaseVersionProvider
import org.eazyportal.plugin.release.core.version.SnapshotVersionProvider
import org.eazyportal.plugin.release.core.version.VersionIncrementProvider
import org.eazyportal.plugin.release.gradle.model.EazyReleasePluginExtension
import org.eazyportal.plugin.release.gradle.project.GradleProjectActionsFactory
import org.eazyportal.plugin.release.gradle.tasks.EazyReleaseBaseTask
import org.eazyportal.plugin.release.gradle.tasks.SetReleaseVersionTask
import org.eazyportal.plugin.release.gradle.tasks.SetSnapshotVersionTask
import org.eazyportal.plugin.release.gradle.tasks.UpdateScmTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension

class EazyReleasePlugin : Plugin<Project> {

    companion object {
        const val RELEASE_TASK_NAME = "release"
        const val SET_RELEASE_VERSION_TASK_NAME = "setReleaseVersion"
        const val SET_SNAPSHOT_VERSION_TASK_NAME = "setSnapshotVersion"
        const val UPDATE_SCM_TASK_NAME = "updateScm"

        const val PROJECT_ACTIONS_FACTORY_EXTRA_PROPERTY = "projectActionsFactory"
    }

    override fun apply(project: Project) {
        project.plugins.apply("maven-publish")

        val projectActionsFactory: ProjectActionsFactory
        project.extensions.getByType(ExtraPropertiesExtension::class.java).apply {
            if (!has(PROJECT_ACTIONS_FACTORY_EXTRA_PROPERTY)) {
                set(PROJECT_ACTIONS_FACTORY_EXTRA_PROPERTY, GradleProjectActionsFactory())
            }

            projectActionsFactory = get(PROJECT_ACTIONS_FACTORY_EXTRA_PROPERTY) as ProjectActionsFactory
        }

        val extension = project.extensions.create("release", EazyReleasePluginExtension::class.java)

        val setReleaseVersionAction = SetReleaseVersionAction(projectActionsFactory, ReleaseVersionProvider(), VersionIncrementProvider())
        val setSnapshotVersionAction = SetSnapshotVersionAction(projectActionsFactory, SnapshotVersionProvider())
        val updateScmAction = UpdateScmAction()

        project.tasks.apply {
            register(SET_RELEASE_VERSION_TASK_NAME, SetReleaseVersionTask::class.java, setReleaseVersionAction).configure {
                it.conventionalCommitTypes.set(extension.conventionalCommitTypes)
                it.scmActions.set(extension.scmActions)
                it.scmConfig.set(extension.scmConfig)
            }

            val buildTask = getByName("build").also {
                it.mustRunAfter(SET_RELEASE_VERSION_TASK_NAME)
            }

            val publishTask = getByName("publish").also {
                it.mustRunAfter(buildTask)
            }

            register(SET_SNAPSHOT_VERSION_TASK_NAME, SetSnapshotVersionTask::class.java, setSnapshotVersionAction).configure {
                it.mustRunAfter(SET_RELEASE_VERSION_TASK_NAME)

                it.scmActions.set(extension.scmActions)
                it.scmConfig.set(extension.scmConfig)
            }

            register(UPDATE_SCM_TASK_NAME, UpdateScmTask::class.java, updateScmAction).configure {
                it.mustRunAfter(SET_SNAPSHOT_VERSION_TASK_NAME)

                it.scmActions.set(extension.scmActions)
                it.scmConfig.set(extension.scmConfig)
            }

            register(RELEASE_TASK_NAME, EazyReleaseBaseTask::class.java) {
                it.dependsOn(SET_RELEASE_VERSION_TASK_NAME, buildTask, publishTask, SET_SNAPSHOT_VERSION_TASK_NAME, UPDATE_SCM_TASK_NAME)
            }
        }
    }

}
