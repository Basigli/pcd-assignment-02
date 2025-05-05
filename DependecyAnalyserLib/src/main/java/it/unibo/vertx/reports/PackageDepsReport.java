package it.unibo.vertx.reports;


import java.util.HashSet;
import java.util.Set;

public class PackageDepsReport {

    private String packageName;
    private HashSet<ClassDepsReport> packageDependencies = new HashSet<>();

    public HashSet<ClassDepsReport> getPackageDependencies() {
        return packageDependencies;
    }
    public void setPackageDependencies(HashSet<ClassDepsReport> packageDependencies) {
        this.packageDependencies = packageDependencies;
    }
    public void addPackageDependency(ClassDepsReport classDepsReport) {
        this.packageDependencies.add(classDepsReport);
    }
    public void removePackageDependency(ClassDepsReport classDepsReport) {
        this.packageDependencies.remove(classDepsReport);
    }

    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Package: ").append(packageName).append("\n");
        for (ClassDepsReport classDepsReport : packageDependencies) {
            res.append(classDepsReport.toString());
        }
        return res.toString();
    }

}
