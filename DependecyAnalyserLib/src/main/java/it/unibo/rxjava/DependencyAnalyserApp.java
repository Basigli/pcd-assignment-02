package it.unibo.rxjava;


import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import it.unibo.rxjava.DependencyCollector;
import it.unibo.vertx.reports.ClassDepsReport;

public class DependencyAnalyserApp {

    private JFrame frame;
    private JTextField folderField;
    private JButton browseButton;
    private JButton startButton;
    private JLabel classesCountLabel;
    private JLabel depsCountLabel;
    private mxGraph graph;
    private Object parent;
    private mxGraphComponent graphComponent;
    private Map<String, Object> nodeMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DependencyAnalyserApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Dependency Analyser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        JPanel controlPanel = new JPanel(new BorderLayout(5,5));
        folderField = new JTextField();
        browseButton = new JButton("Browse...");
        browseButton.addActionListener(this::onBrowse);
        controlPanel.add(folderField, BorderLayout.CENTER);
        controlPanel.add(browseButton, BorderLayout.EAST);

        startButton = new JButton("Start Analysis");
        startButton.addActionListener(this::onStart);

        JPanel statsPanel = new JPanel(new GridLayout(1,2,10,10));
        classesCountLabel = new JLabel("Classes: 0");
        classesCountLabel.setBorder(new TitledBorder("Analyzed"));
        depsCountLabel = new JLabel("Dependencies: 0");
        depsCountLabel.setBorder(new TitledBorder("Found"));
        statsPanel.add(classesCountLabel);
        statsPanel.add(depsCountLabel);

        JPanel topPanel = new JPanel(new BorderLayout(5,5));
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(startButton, BorderLayout.CENTER);
        topPanel.add(statsPanel, BorderLayout.SOUTH);

        // Initialize JGraphX
        graph = new mxGraph();
        parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            // nothing initially
        } finally {
            graph.getModel().endUpdate();
        }
        graphComponent = new mxGraphComponent(graph);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(graphComponent, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void onBrowse(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            folderField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onStart(ActionEvent e) {
        String root = folderField.getText();
        if (root.isEmpty()) return;
        startButton.setEnabled(false);
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
            nodeMap.clear();
        } finally {
            graph.getModel().endUpdate();
        }
        classesCountLabel.setText("Classes: 0");
        depsCountLabel.setText("Dependencies: 0");

        Observable.<File>create(emitter -> {
                    scanDirectory(new File(root), emitter);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .flatMap(this::parseAndCollect) // Use the updated parseAndCollect method
                .observeOn(Schedulers.single())
                .subscribe(result -> {
                    SwingUtilities.invokeLater(() -> {
                        addNode(result.getClassName());
                        result.getClassDependencies().forEach(dep -> {
                            addNode(dep);
                            addEdge(result.getClassName(), dep);
                        });
                        classesCountLabel.setText("Classes: " + nodeMap.size());
                        depsCountLabel.setText("Dependencies: " + countEdges());
                    });
                }, err -> {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Error: " + err.getMessage()));
                }, () -> SwingUtilities.invokeLater(() -> startButton.setEnabled(true)));
    }

    private void addNode(String node) {
        if (!nodeMap.containsKey(node)) {
            graph.getModel().beginUpdate();
            try {
                Object v = graph.insertVertex(parent, null, node, 0, 0, node.length()*10 + 10, 30);
                nodeMap.put(node, v);
            } finally {
                graph.getModel().endUpdate();
            }
            applyLayout(); // Apply layout after adding the node
        }
    }
    private void applyLayout() {
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.execute(parent);
    }

    private void addEdge(String from, String to) {
        Object v1 = nodeMap.get(from);
        Object v2 = nodeMap.get(to);
        if (v1 != null && v2 != null) {
            graph.getModel().beginUpdate();
            try {
                graph.insertEdge(parent, null, "", v1, v2);
            } finally {
                graph.getModel().endUpdate();
            }
        }
    }

    private int countEdges() {
        return graph.getChildEdges(parent).length;
    }

    private void scanDirectory(File dir, ObservableEmitter<File> emitter) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(file, emitter);
            } else if (file.getName().endsWith(".java")) {
                emitter.onNext(file);
                try {
                    // Introduce a delay of 500 milliseconds
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during delay", e);
                }
            }
        }
    }
    /*
    private ClassDepsReport parseAndCollect(File file) throws Exception {
        System.out.println("Parsing file: " + file.getAbsolutePath());
        System.out.println("Collecting dependencies...");
        CompilationUnit cu = StaticJavaParser.parse(file);
        HashSet<String> dependencies = new HashSet<>();
        var visitor = new DependencyCollector();
        visitor.visit(cu, dependencies);
        System.out.println("Found dependencies: " + dependencies);
        String className = cu.getPrimaryTypeName().orElse(file.getName());
        ClassDepsReport report = new ClassDepsReport();
        report.setClassName(className);
        report.setClassDependencies(dependencies);
        return report;
    }
    */
    private Observable<ClassDepsReport> parseAndCollect(File file) {
        return Observable.create(emitter -> {
            try {
                System.out.println("Parsing file: " + file.getAbsolutePath());
                CompilationUnit cu = StaticJavaParser.parse(file);

                // Create a DependencyCollector and an Observable to collect dependencies
                Observable<String> dependenciesObservable = Observable.create(depEmitter -> {
                    DependencyCollector collector = new DependencyCollector();
                    collector.visit(cu, depEmitter);
                    depEmitter.onComplete();
                });

                // Collect dependencies into a HashSet
                HashSet<String> dependencies = new HashSet<>();
                dependenciesObservable
                        .delay(300, TimeUnit.MILLISECONDS)
                        .subscribe(dependencies::add, emitter::onError, () -> {
                            // Create ClassDepsReport
                            String className = cu.getPrimaryTypeName().orElse(file.getName());
                            ClassDepsReport report = new ClassDepsReport();
                            report.setClassName(className);
                            report.setClassDependencies(dependencies);

                            // Emit the report
                            emitter.onNext(report);
                        });
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
