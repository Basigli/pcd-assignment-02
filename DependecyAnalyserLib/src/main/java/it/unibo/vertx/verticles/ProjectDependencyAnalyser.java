package it.unibo.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import it.unibo.vertx.reports.ProjectDepsReport;
import it.unibo.vertx.DependecyAnalyserLib;
public class ProjectDependencyAnalyser extends AbstractVerticle {
    private final Promise<ProjectDepsReport> promise;
    private final String projectSrcFolder;

    public ProjectDependencyAnalyser(String projectSrcFolder, Promise<ProjectDepsReport> promise) {
        this.promise = promise;
        this.projectSrcFolder = projectSrcFolder;
    }
    @Override
    public void start() {
        System.out.println("ProjectDependencyAnalyser started");
        vertx.fileSystem().readDir(projectSrcFolder)
                .onSuccess(files -> {
                    ProjectDepsReport report = new ProjectDepsReport();
                    report.setProjectName(projectSrcFolder);

                    var futures = files.stream()
                            .map(file -> DependecyAnalyserLib.getPackageDependencies(file, vertx)
                                    .onSuccess(report::addProjectDependency)
                                    .onFailure(err -> System.err.println("Failed to get package dependencies: " + err.getMessage())))
                            .toList();
                    Future.all(futures)
                            .onSuccess(res -> promise.complete(report))
                            .onFailure(err -> {
                                System.err.println("Failed to analyze project dependencies: " + err.getMessage());
                                promise.fail(err);
                            });
                })
                .onFailure(err -> {
                    System.err.println("File reading failed: " + err.getMessage());
                    promise.fail(err);
                });
    }
}
