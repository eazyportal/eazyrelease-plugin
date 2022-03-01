package org.eazyportal.plugin.release.gradle

import org.eazyportal.plugin.release.core.SetSnapshotVersionAction
import org.eazyportal.plugin.release.core.version.SnapshotVersionProvider
import org.eazyportal.plugin.release.gradle.model.EazyReleasePluginExtension

class SetSnapshotVersionActionFactory {

    fun create(extension: EazyReleasePluginExtension): SetSnapshotVersionAction =
        SetSnapshotVersionAction(extension.projectActionsFactory, SnapshotVersionProvider()).apply {
            scmActions = extension.scmActions
            scmConfig = extension.scmConfig
        }

}