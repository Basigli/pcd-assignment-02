package it.unibo;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class App {
    private static final String FILE_PATH = "/Users/gabe/Documents/GitHub/pcd-assignment-02/DependecyAnalyserLib/src/main/java/it/unibo/App.java";
    public static void main(String[] args) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(Files.newInputStream(Paths.get(FILE_PATH)));

        var declarations = new ArrayList<String>();
        VoidVisitor<List<String>> visitor = new DependencyCollector();
        visitor.visit(cu, declarations);
        declarations.forEach(n -> System.out.println("Class or interface: " + n));
    }
}
