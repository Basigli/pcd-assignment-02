package it.unibo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.vertx.reports.PackageDepsReport;
import org.junit.jupiter.api.Test;
import it.unibo.vertx.reports.ClassDepsReport;

import java.util.HashSet;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }


    @Test
    public void toStringTest() {
        ClassDepsReport classDepsReport = new ClassDepsReport();
        classDepsReport.setClassName("DependencyCollector");
        classDepsReport.addClassDependency("java.util.Set");
        classDepsReport.addClassDependency("org.junit.jupiter.api.Test");


        ClassDepsReport secondClassDepsReport = new ClassDepsReport();
        secondClassDepsReport.setClassName("DependencyAnalyser");
        secondClassDepsReport.addClassDependency("java.util.Set");
        secondClassDepsReport.addClassDependency("org.junit.jupiter.api.Test");

        //System.out.println(classDepsReport.toString());
        PackageDepsReport packageDepsReport = new PackageDepsReport();
        packageDepsReport.addPackageDependency(classDepsReport);
        packageDepsReport.addPackageDependency(secondClassDepsReport);
        System.out.println(packageDepsReport.toString());





    }


}
