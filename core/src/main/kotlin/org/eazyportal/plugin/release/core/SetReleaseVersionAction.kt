package org.eazyportal.plugin.release.core

import org.eazyportal.plugin.release.core.project.ProjectActionsFactory
import org.eazyportal.plugin.release.core.scm.ConventionalCommitType
import org.eazyportal.plugin.release.core.scm.ScmActions
import org.eazyportal.plugin.release.core.scm.exception.ScmActionException
import org.eazyportal.plugin.release.core.scm.model.ScmConfig
import org.eazyportal.plugin.release.core.version.ReleaseVersionProvider
import org.eazyportal.plugin.release.core.version.VersionIncrementProvider
import org.eazyportal.plugin.release.core.version.model.VersionIncrement
import org.slf4j.LoggerFactory
import java.io.File

open class SetReleaseVersionAction(
    private val projectActionsFactory: ProjectActionsFactory,
    private val releaseVersionProvider: ReleaseVersionProvider,
    private val versionIncrementProvider: VersionIncrementProvider
) : ReleaseAction {

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(SetReleaseVersionAction::class.java)
    }

    lateinit var conventionalCommitTypes: List<ConventionalCommitType>
    lateinit var scmActions: ScmActions
    lateinit var scmConfig: ScmConfig

    override fun execute(workingDir: File) {
        scmActions.fetch(workingDir, scmConfig.remote)

        if (scmConfig.releaseBranch != scmConfig.featureBranch) {
            scmActions.checkout(workingDir, scmConfig.featureBranch)
        }

        val projectActions = projectActionsFactory.create(workingDir);

        val currentVersion = projectActions.getVersion()
        val versionIncrement = getVersionIncrement(workingDir)
        val releaseVersion = releaseVersionProvider.provide(currentVersion, versionIncrement)

        if (scmConfig.releaseBranch != scmConfig.featureBranch) {
            scmActions.checkout(workingDir, scmConfig.releaseBranch)

            scmActions.mergeNoCommit(workingDir, scmConfig.featureBranch)
        }

        projectActions.setVersion(releaseVersion)

        scmActions.add(workingDir, *projectActions.scmFilesToCommit())
        scmActions.commit(workingDir, "Release version: $releaseVersion")
        scmActions.tag(workingDir, "-a", releaseVersion.toString(), "-m", "v${releaseVersion}")
    }

    private fun getVersionIncrement(workingDir: File): VersionIncrement {
        val lastTag = try {
            scmActions.getLastTag(workingDir)
        }
        catch (exception: ScmActionException) {
            LOGGER.warn("Ignoring missing Git tag from release version calculation.")

            null
        }

        val commits = scmActions.getCommits(workingDir, lastTag)

        val versionIncrement = versionIncrementProvider.provide(commits, conventionalCommitTypes)

        if ((versionIncrement == null) || (versionIncrement == VersionIncrement.NONE)) {
            throw IllegalArgumentException("There are no acceptable commits.")
        }

        return versionIncrement
    }

}
