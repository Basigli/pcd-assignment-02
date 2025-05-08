package it.unibo.rxjava;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.reactivex.rxjava3.core.Observable;
import it.unibo.rxjava.DependencyCollector;
import it.unibo.vertx.reports.ClassDepsReport;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class JavaDependencyParser {
    public Observable<ClassDepsReport> parse(File file) {
        return Observable.create(emitter -> {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                ClassDepsReport report = new ClassDepsReport();
                String className = cu.getPrimaryTypeName().orElse(file.getName());
                report.setClassName(className);

                Observable<String> deps = Observable.create(depEmitter -> {
                    new DependencyCollector().visit(cu, depEmitter);
                    depEmitter.onComplete();
                });

                deps
                        .concatMap(dep -> Observable.just(dep).delay(200, TimeUnit.MILLISECONDS))
                        .doOnNext(dep -> {
                            report.addClassDependency(dep);
                            ClassDepsReport classReport = new ClassDepsReport();
                            classReport.setClassName(className);
                            classReport.addClassDependency(dep);
                            emitter.onNext(classReport);
                        })
                        .doOnComplete(() -> {
                            emitter.onComplete();
                        })
                        .subscribe();

            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}

