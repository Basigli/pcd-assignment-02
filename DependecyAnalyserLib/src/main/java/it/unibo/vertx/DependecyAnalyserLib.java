package it.unibo.vertx;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import it.unibo.vertx.reports.ClassDepsReport;
import it.unibo.vertx.reports.PackageDepsReport;
import it.unibo.vertx.reports.ProjectDepsReport;
import it.unibo.vertx.verticles.ClassDependencyAnalyser;
import it.unibo.vertx.verticles.PackageDepencyAnalyser;
import it.unibo.vertx.verticles.ProjectDependencyAnalyser;

public class DependecyAnalyserLib {



    public static Future<ClassDepsReport> getClassDependencies(String classSrcFile, Vertx vertx) {
        Promise<ClassDepsReport> promise = Promise.promise();
        vertx.deployVerticle(new ClassDependencyAnalyser(classSrcFile, promise));
        return promise.future();
    }

    public static Future<PackageDepsReport> getPackageDependencies(String packageSrcFolder, Vertx vertx) {
        Promise<PackageDepsReport> promise = Promise.promise();
        vertx.deployVerticle(new PackageDepencyAnalyser(packageSrcFolder, promise));
        return promise.future();
    }

    public static Future<ProjectDepsReport> getProjectDependencies(String projectSrcFolder, Vertx vertx) {
        Promise<ProjectDepsReport> promise = Promise.promise();
        vertx.deployVerticle(new ProjectDependencyAnalyser(projectSrcFolder, promise));
        return promise.future();
    }
}
