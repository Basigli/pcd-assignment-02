package it.unibo;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import io.vertx.core.*;
import io.vertx.core.file.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class AsyncApp extends AbstractVerticle {

    private static final String FILE_PATH = "/Users/gabe/Documents/GitHub/pcd-assignment-02/DependecyAnalyserLib/src/main/java/it/unibo/App.java";

    @Override
    public void start(Promise<Void> startPromise) {
        FileSystem fs = vertx.fileSystem();

        fs.readFile(FILE_PATH, result -> {
            if (result.failed()) {
                startPromise.fail(result.cause());
                return;
            }

            String sourceCode = result.result().toString();

            // Parsing e visita AST in un thread worker
            vertx.executeBlocking(promise -> {
                try {
                    CompilationUnit cu = StaticJavaParser.parse(sourceCode);
                    var declarations = new ArrayList<String>();
                    VoidVisitor<List<String>> visitor = new DependencyCollector();
                    visitor.visit(cu, declarations);
                    promise.complete(declarations);
                } catch (Exception e) {
                    promise.fail(e);
                }
            }, res -> {
                if (res.succeeded()) {
                    List<String> declarations = (List<String>) res.result();
                    declarations.forEach(n -> System.out.println("Class or interface: " + n));
                    startPromise.complete();
                } else {
                    startPromise.fail(res.cause());
                }
            });
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AsyncApp(), res -> {
            if (res.succeeded()) {
                System.out.println("Analisi completata.");
            } else {
                System.err.println("Errore: " + res.cause().getMessage());
            }
        });
    }
}
