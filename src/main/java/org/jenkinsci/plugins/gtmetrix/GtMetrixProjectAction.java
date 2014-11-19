package org.jenkinsci.plugins.gtmetrix;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;

/**
 * Created by chrislondon on 11/11/14.
 */
public class GtMetrixProjectAction implements Action {
    public AbstractProject project;

    public GtMetrixProjectAction(AbstractProject project)
    {
        super();
        this.project = project;
    }

    public AbstractProject getProject() {
        return project;
    }

    /**
     * Get the violations action for this project.
     * This is defined as the most recent violations actions
     * of the builds of this project.
     * @return the most recent violations build action.
     */
    public GtMetrixBuildAction getLastResult() {

        for (AbstractBuild<?, ?> b = getProject().getLastBuild();
             b != null;
             b = b.getPreviousBuild()) {

            GtMetrixBuildAction ret = b.getAction(GtMetrixBuildAction.class);

            if (ret != null && ret.getReport() != null) {
                return ret;
            }
        }

        return null;
    }


    /**
     * Returns the path to the JDepend page
     * @see hudson.model.Action#getUrlName()
     */
    public String getUrlName() {
        return null;
    }

    /**
     * Return the JDepend display name
     * @see hudson.model.Action#getDisplayName()
     */
    public String getDisplayName() {
        return null;
    }

    /**
     * Return the JDepend icon path
     * @see hudson.model.Action#getIconFileName()
     */
    public String getIconFileName() {
        return null;
    }
}
