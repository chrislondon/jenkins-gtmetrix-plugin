package org.jenkinsci.plugins.gtmetrix;

import hudson.model.AbstractBuild;
import hudson.model.Action;

/**
 * Created by chrislondon on 11/11/14.
 */
public class GtMetrixBuildAction implements Action {
    public final AbstractBuild<?, ?> build;

    public GtMetrixBuildAction(AbstractBuild<?, ?> build)
    {
        super();
        this.build = build;
    }

    public String findReport() {
        return "HI!";
    }

    public String getGtMetrixHtml() {
        return "<h1>WOOT!</h1>";
    }

    /**
     * Returns the path to the JDepend page
     * @see hudson.model.Action#getUrlName()
     */
    public String getUrlName() {
        return "gtmetrix";
    }

    /**
     * Return the JDepend display name
     * @see hudson.model.Action#getDisplayName()
     */
    public String getDisplayName() {
        return "GT Metrix";
    }

    /**
     * Return the JDepend icon path
     * @see hudson.model.Action#getIconFileName()
     */
    public String getIconFileName() {
        return "graph.gif";
    }
}
