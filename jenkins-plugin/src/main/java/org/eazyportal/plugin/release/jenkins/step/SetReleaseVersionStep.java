package org.eazyportal.plugin.release.jenkins.step;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import org.eazyportal.plugin.release.core.action.model.ActionContext;
import org.eazyportal.plugin.release.core.model.ProjectDescriptor;
import org.eazyportal.plugin.release.jenkins.ProjectDescriptorFactory;
import org.eazyportal.plugin.release.jenkins.action.ActionContextFactory;
import org.eazyportal.plugin.release.jenkins.action.SetReleaseVersionActionFactory;
import org.jenkinsci.Symbol;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class SetReleaseVersionStep extends Builder implements SimpleBuildStep, Serializable {

    @DataBoundConstructor
    public SetReleaseVersionStep() {
        // required by Jenkins
    }

    @Override
    public void perform(@NotNull Run<?, ?> run, @NotNull FilePath workspace, @NotNull EnvVars env, @NotNull Launcher launcher, @NotNull TaskListener listener)
            throws InterruptedException, IOException {

        File workingDir = new File(workspace.toURI());

        ProjectDescriptor projectDescriptor = run.getAction(ProjectDescriptorFactory.class)
            .create(workingDir);

        ActionContext actionContext = run.getAction(ActionContextFactory.class)
            .create(env);

        run.getAction(SetReleaseVersionActionFactory.class)
            .create()
            .execute(projectDescriptor, actionContext);
    }

    @Extension
    @Symbol("setReleaseVersion")
    public static final class SetReleaseVersionStepDescriptor extends BuildStepDescriptor<Builder> {

        @NotNull
        @Override
        public String getDisplayName() {
            return "Set release version";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }

}
