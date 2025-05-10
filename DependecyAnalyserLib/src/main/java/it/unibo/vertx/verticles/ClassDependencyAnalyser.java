package it.unibo.vertx.verticles;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import it.unibo.vertx.DependencyCollector;
import it.unibo.vertx.reports.ClassDepsReport;

import java.util.HashSet;
import java.util.Set;

public class ClassDependencyAnalyser extends AbstractVerticle {

    private final String classSrcFile;
    private final Promise<ClassDepsReport> promise;

    public ClassDependencyAnalyser(String classSrcFile, Promise<ClassDepsReport> promise) {
        this.classSrcFile = classSrcFile;
        this.promise = promise;
    }

    @Override
    public void start() {
        System.out.println("ClassDependencyAnalyser started");

        vertx.fileSystem().readFile(classSrcFile)
                .onSuccess(buffer -> {
                    vertx.<ClassDepsReport>executeBlocking(() -> {
                                CompilationUnit cu = StaticJavaParser.parse(buffer.toString());
                                HashSet<String> dependencies = new HashSet<>();
                                var visitor = new DependencyCollector();
                                visitor.visit(cu, dependencies);

                                ClassDepsReport report = new ClassDepsReport();
                                report.setClassName(classSrcFile);
                                report.setClassDependencies(dependencies);

                                return report;
                            }, false)
                            .onSuccess(promise::complete)
                            .onFailure(err -> {
                                System.err.println("Parsing failed: " + err.getMessage());
                                promise.fail(err);
                            });
                })
                .onFailure(err -> {
                    System.err.println("File reading failed: " + err.getMessage());
                    promise.fail(err);
                });
    }
}
