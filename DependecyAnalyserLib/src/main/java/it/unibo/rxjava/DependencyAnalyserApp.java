package it.unibo.rxjava;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class DependencyAnalyserApp {
    private JFrame frame;
    private JTextField folderField;
    private JButton browseButton;
    private JButton startButton;
    private JLabel classesCountLabel;
    private JLabel depsCountLabel;
    private GraphView graphView;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DependencyAnalyserApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Dependency Analyser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        folderField = new JTextField();
        browseButton = new JButton("Browse...");
        browseButton.addActionListener(this::onBrowse);
        controlPanel.add(folderField, BorderLayout.CENTER);
        controlPanel.add(browseButton, BorderLayout.EAST);

        startButton = new JButton("Start Analysis");
        startButton.addActionListener(this::onStart);

        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        classesCountLabel = new JLabel("Classes: 0");
        depsCountLabel = new JLabel("Dependencies: 0");
        statsPanel.add(classesCountLabel);
        statsPanel.add(depsCountLabel);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(startButton, BorderLayout.CENTER);
        topPanel.add(statsPanel, BorderLayout.SOUTH);

        graphView = new GraphView();

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(graphView.getComponent(), BorderLayout.CENTER);
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

        graphView.reset();
        classesCountLabel.setText("Classes: 0");
        depsCountLabel.setText("Dependencies: 0");

        JavaFileScanner scanner = new JavaFileScanner();
        JavaDependencyParser parser = new JavaDependencyParser();

        scanner.scan(new File(root))
                .subscribeOn(Schedulers.io())
                .flatMap(parser::parse)
                .observeOn(Schedulers.single())
                .concatMap(report -> Observable.just(report).delay(200, TimeUnit.MILLISECONDS))
                .subscribe(report -> {
                    SwingUtilities.invokeLater(() -> {
                        graphView.addNode(report.getClassName());
                        report.getClassDependencies().forEach(dep -> {
                            graphView.addNode(dep);
                            graphView.addEdge(report.getClassName(), dep);
                        });
                        classesCountLabel.setText("Classes: " + graphView.getNodeCount());
                        depsCountLabel.setText("Dependencies: " + graphView.getEdgeCount());
                    });
                }, err -> {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Error: " + err.getMessage()));
                }, () -> SwingUtilities.invokeLater(() -> startButton.setEnabled(true)));
    }
}