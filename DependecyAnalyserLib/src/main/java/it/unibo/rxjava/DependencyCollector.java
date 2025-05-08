package it.unibo.rxjava;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class DependencyCollector extends VoidVisitorAdapter<ObservableEmitter<String>> {
    @Override
    public void visit(ClassOrInterfaceType declaration, ObservableEmitter<String> emitter) {
        super.visit(declaration, emitter);
        log("Visiting: " + declaration.getNameAsString());
        emitter.onNext(declaration.getNameAsString());
    }
    static private void log(String msg) {
        System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
    }

}