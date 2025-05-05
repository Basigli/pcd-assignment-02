package it.unibo.vertx.reports;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectDepsReport {
    private HashSet<PackageDepsReport> projectDependencies = new HashSet<>();
    private String projectName;

    public HashSet<PackageDepsReport> getProjectDependencies() {
        return projectDependencies;
    }
    public void setProjectDependencies(HashSet<PackageDepsReport> projectDependencies) {
        this.projectDependencies = projectDependencies;
    }
    public void addProjectDependency(PackageDepsReport packageDepsReport) {
        this.projectDependencies.add(packageDepsReport);
    }
    public void removeProjectDependency(PackageDepsReport packageDepsReport) {
        this.projectDependencies.remove(packageDepsReport);
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Project: ").append(projectName).append("\n");
        for (PackageDepsReport packageDepsReport : projectDependencies) {
            res.append(packageDepsReport.toString());
        }
        return res.toString();
    }

}
