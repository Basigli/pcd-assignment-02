package it.unibo.vertx;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.HashSet;

public class DependencyCollector extends VoidVisitorAdapter<HashSet<String>> {
    @Override
    public void visit(ClassOrInterfaceType declaration, HashSet<String> collector) {
        super.visit(declaration, collector);
        collector.add(declaration.getNameAsString());
    }
}
