package it.unibo.rxjava;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class GraphView {
    private final mxGraph graph;
    private final Object parent;
    private final mxGraphComponent graphComponent;
    private final Map<String, Object> nodeMap = new HashMap<>();

    public GraphView() {
        this.graph = new mxGraph();
        this.parent = graph.getDefaultParent();
        this.graphComponent = new mxGraphComponent(graph);
    }

    public JComponent getComponent() {
        return graphComponent;
    }

    public void reset() {
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildVertices(parent));
            nodeMap.clear();
        } finally {
            graph.getModel().endUpdate();
        }
    }

    public void addNode(String name) {
        if (!nodeMap.containsKey(name)) {
            graph.getModel().beginUpdate();
            try {
                Object v = graph.insertVertex(parent, null, name, 0, 0, name.length() * 10 + 10, 30);
                nodeMap.put(name, v);
            } finally {
                graph.getModel().endUpdate();
            }
            applyLayout();
        }
    }

    public void addEdge(String from, String to) {
        Object v1 = nodeMap.get(from);
        Object v2 = nodeMap.get(to);
        if (v1 != null && v2 != null && v1 != v2 && !edgeExists(v1, v2)) {
            graph.getModel().beginUpdate();
            try {
                graph.insertEdge(parent, null, "", v1, v2);
            } finally {
                graph.getModel().endUpdate();
            }
        }
    }

    private boolean edgeExists(Object from, Object to) {
        for (Object edge : graph.getChildEdges(parent)) {
            if (graph.getModel().getTerminal(edge, true) == from && graph.getModel().getTerminal(edge, false) == to) {
                return true;
            }
        }
        return false;
    }
    /*
    private void applyLayout() {
        new mxHierarchicalLayout(graph).execute(parent);
    }
     */
    private void applyLayout() {
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setOrientation(SwingConstants.NORTH); // Disposizione verticale
        layout.setIntraCellSpacing(30); // Spaziatura tra nodi nello stesso livello
        layout.setInterRankCellSpacing(100); // Spaziatura tra livelli
        layout.execute(parent);
    }


    public int getNodeCount() {
        return nodeMap.size();
    }

    public int getEdgeCount() {
        return graph.getChildEdges(parent).length;
    }
}