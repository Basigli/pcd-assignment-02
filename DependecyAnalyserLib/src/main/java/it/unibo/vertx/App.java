package it.unibo.vertx;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import io.vertx.core.Vertx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static final String FILE_PATH = "DependecyAnalyserLib/src/main/java/it/unibo/vertx/reports/ClassDepsReport.java";
    private static final String PACKAGE_PATH = "DependecyAnalyserLib/src/main/java/it/unibo/vertx/reports";
    private static final String PROJECT_PATH = "DependecyAnalyserLib/src/main/java/it/unibo/vertx";
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        /*
        DependecyAnalyserLib.getClassDependencies(FILE_PATH, vertx).onComplete(result -> {
            if (result.succeeded()) {
                System.out.println(result.result().toString());
            } else {
                System.err.println("Failed to get class dependencies: " + result.cause());
            }
        });

        DependecyAnalyserLib.getPackageDependencies(PACKAGE_PATH, vertx).onComplete(result -> {
            if (result.succeeded()) {
                System.out.println(result.result().toString());
            } else {
                System.err.println("Failed to get package dependencies: " + result.cause());
            }
        });
        */
        DependecyAnalyserLib.getProjectDependencies(PROJECT_PATH, vertx).onComplete(result -> {
            if (result.succeeded()) {
                System.out.println(result.result().toString());
            } else {
                System.err.println("Failed to get project dependencies: " + result.cause());
            }
        });




    }
}
