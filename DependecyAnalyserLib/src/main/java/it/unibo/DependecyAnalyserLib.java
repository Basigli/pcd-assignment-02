package it.unibo;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DependecyAnalyserLib {

    public static ClassDepsReport getClassDependencies(String classSrcFile) throws IOException {
        var cu = DependecyAnalyserLib.getCompilationUnit(classSrcFile);
        var declarations = new ArrayList<String>();
        VoidVisitor<List<String>> visitor = new DependencyCollector();
        visitor.visit(cu, declarations);
        ClassDepsReport report = new ClassDepsReport();
        report.setList(declarations);
        return report;
    }

    public static PackageDepsReport getPackageDependencies(String packageSrcFolder) throws IOException {
        ArrayList<String> packageDependencies = new ArrayList<>();
        for (String file : Files.walk(Paths.get(packageSrcFolder))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .toList()) {
            try {
                getClassDependencies(file).getList().forEach(dep -> packageDependencies.add(dep));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PackageDepsReport report = new PackageDepsReport();
        report.setList(packageDependencies);
        return report;
    }

    public static ProjectDepsReport getProjectDependencies(String projectSrcFolder) throws IOException {
        ArrayList<String> projectDependencies = new ArrayList<>();
        for (var packageSrcFolder : Files.walk(Paths.get(projectSrcFolder))
                .filter(Files::isDirectory)
                .map(Path::toString)
                .toList()) {
            try {
                getPackageDependencies(packageSrcFolder).getList().forEach(dep -> projectDependencies.add(dep));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ProjectDepsReport report = new ProjectDepsReport();
        report.setList(projectDependencies);
        return report;
    }


    private static CompilationUnit getCompilationUnit(String filePath) throws IOException {
        return StaticJavaParser.parse(Files.newInputStream(Paths.get(filePath)));
    }



}
