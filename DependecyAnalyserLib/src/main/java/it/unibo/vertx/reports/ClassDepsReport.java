package it.unibo.vertx.reports;


import java.util.HashSet;
import java.util.Set;

public class ClassDepsReport {
    private String className;
    private HashSet<String> classDependencies = new HashSet<>();
    public HashSet<String> getClassDependencies() {
        return classDependencies;
    }
    public void setClassDependencies(HashSet<String> classDependencies) {
        this.classDependencies = classDependencies;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public void addClassDependency(String classDependency) {
        this.classDependencies.add(classDependency);
    }
    public void removeClassDependency(String classDependency) {
        this.classDependencies.remove(classDependency);
    }

    public String toString() {
        StringBuilder res = new StringBuilder("+ " + className + "\n");
        for (String classDependency : classDependencies) {
            res.append("|-- ").append(classDependency).append("\n");
        }
        return res.toString();
    }
}
