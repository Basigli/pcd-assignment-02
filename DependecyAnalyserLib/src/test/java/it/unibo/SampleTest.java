package it.unibo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import com.mxgraph.view.mxGraph;
import com.mxgraph.layout.mxCircleLayout;

public class SampleTest {

    private Map<String, List<String>> cache = new HashMap<>();

    public static void main(String[] args) {
        SampleTest tester = new SampleTest();
        tester.runAnalysis("src");
    }

    public void runAnalysis(String rootDir) {
        Observable<File> files = Observable.<File>create(emitter -> {
            scan(new File(rootDir), emitter);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());

        files.map(this::parseFile)
                .filter(cu -> cu.getTypes().size() > 0)
                .map(this::extractNames)
                .observeOn(Schedulers.single())
                .subscribe(list -> {
                    System.out.println("Found types: " + list);
                }, err -> {
                    System.err.println("Error: " + err.getMessage());
                });
    }

    private void scan(File dir, io.reactivex.rxjava3.core.ObservableEmitter<File> em) {
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File f : children) {
            if (f.isDirectory()) {
                scan(f, em);
            } else if (f.getName().endsWith(".java")) {
                em.onNext(f);
            }
        }
    }

    private CompilationUnit parseFile(File f) throws IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            return StaticJavaParser.parse(r);
        }
    }

    private List<String> extractNames(CompilationUnit cu) {
        List<String> names = new ArrayList<>();
        cu.getTypes().forEach(t -> names.add(t.getNameAsString()));
        // aggiungo in cache per testare HashMap e List
        cache.putIfAbsent("key", names);
        return cache.get("key").stream()
                .filter(n -> n.startsWith("A"))
                .collect(Collectors.toList());
    }

    public void demoGraph() {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            Object v1 = graph.insertVertex(parent, null, "A", 20, 20, 80, 30);
            Object v2 = graph.insertVertex(parent, null, "B", 200, 150, 80, 30);
            graph.insertEdge(parent, null, "edge", v1, v2);
        } finally {
            graph.getModel().endUpdate();
        }
        // Layout circolare
        new mxCircleLayout(graph).execute(parent);
    }
}