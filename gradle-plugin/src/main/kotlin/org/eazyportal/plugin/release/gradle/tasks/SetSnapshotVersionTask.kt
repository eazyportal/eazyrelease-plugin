package org.eazyportal.plugin.release.gradle.tasks

import org.eazyportal.plugin.release.gradle.SetSnapshotVersionActionFactory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class SetSnapshotVersionTask @Inject constructor(
    private val setSnapshotVersionActionFactory: SetSnapshotVersionActionFactory
) : EazyReleaseBaseTask() {

    @TaskAction
    fun run() {
        logger.quiet("Setting SNAPSHOT version...")

        setSnapshotVersionActionFactory.create(projectActionsFactory, extension)
            .run { execute(project.rootDir) }
    }

}
