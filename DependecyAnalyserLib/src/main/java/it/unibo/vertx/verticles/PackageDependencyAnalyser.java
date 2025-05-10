package it.unibo.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import it.unibo.vertx.DependecyAnalyserLib;
import it.unibo.vertx.reports.PackageDepsReport;

public class PackageDependencyAnalyser extends AbstractVerticle {
    private final Promise<PackageDepsReport> promise;
    private final String packageSrcFolder;

    public PackageDependencyAnalyser(String packageSrcFolder, Promise<PackageDepsReport> promise) {
        this.promise = promise;
        this.packageSrcFolder = packageSrcFolder;
    }


    @Override
    public void start() {
        System.out.println("PackageDepencyAnalyser started");
        vertx.fileSystem().props(packageSrcFolder).onSuccess(props -> {
           if (props.isDirectory()) {
               vertx.fileSystem().readDir(packageSrcFolder)
                       .onSuccess(files -> {
                           PackageDepsReport report = new PackageDepsReport();
                           report.setPackageName(packageSrcFolder);
                           var futures = files.stream()
                                   .filter(file -> file.contains(".java"))
                                   .map(file -> DependecyAnalyserLib.getClassDependencies(file, vertx)
                                           .onSuccess(report::addPackageDependency)
                                           .onFailure(err -> System.err.println("Failed to get class dependencies: " + err.getMessage())))
                                   .toList();
                           Future.all(futures)
                                   .onSuccess(res -> promise.complete(report))
                                   .onFailure(err -> {
                                       System.err.println("Failed to analyze package dependencies: " + err.getMessage());
                                       promise.fail(err);
                                   });
                       })
                       .onFailure(err -> {
                           System.err.println("File reading failed: " + err.getMessage());
                           promise.fail(err);
                       });
           } else {
               System.err.println("Path is not a directory: " + packageSrcFolder);
               promise.fail("Path is not a directory");
           }
        }).onFailure(err -> {
            System.err.println("Failed to get file properties: " + err.getMessage());
            promise.fail(err);
        });

    }
}
