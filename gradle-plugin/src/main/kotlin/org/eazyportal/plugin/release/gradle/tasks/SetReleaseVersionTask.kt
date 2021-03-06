package org.eazyportal.plugin.release.gradle.tasks

import org.eazyportal.plugin.release.core.ProjectDescriptorFactory
import org.eazyportal.plugin.release.core.model.ProjectDescriptor
import org.eazyportal.plugin.release.gradle.action.SetReleaseVersionActionFactory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class SetReleaseVersionTask @Inject constructor(
    private val projectDescriptorFactory: ProjectDescriptorFactory,
    private val setReleaseVersionActionFactory: SetReleaseVersionActionFactory,
) : EazyReleaseBaseTask() {

    @TaskAction
    fun run() {
        logger.quiet("Setting release version...")

        val projectDescriptor: ProjectDescriptor = projectDescriptorFactory.create(
            extension.projectActionsFactory,
            extension.scmActions,
            project.projectDir
        )

        setReleaseVersionActionFactory.create(extension)
            .execute(projectDescriptor)
    }

}
