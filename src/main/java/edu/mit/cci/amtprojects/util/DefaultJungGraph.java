package edu.mit.cci.amtprojects.util;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

/**
 * User: jintrone
 * Date: 4/27/11
 * Time: 7:29 AM
 */
public class DefaultJungGraph extends DirectedSparseMultigraph<DefaultJungNode,DefaultJungEdge> {

    public DefaultJungGraph(DefaultJungGraph copy) {
        for (DefaultJungEdge edge:copy.getEdges()){
            this.addEdge(edge,copy.getEndpoints(edge));
        }
    }

    public DefaultJungGraph() {

    }

    public DefaultJungNode addNode(String name) {
        DefaultJungNode node = new DefaultJungNode(name);
        this.addVertex(node);
        return node;
    }

    public DefaultJungEdge addEdge(DefaultJungNode one, DefaultJungNode two, float weight) {
        DefaultJungEdge e = new DefaultJungEdge(weight);
        this.addEdge(e,one,two);
        return e;
    }

    public DefaultJungNode findVertex(String name) {
        for (DefaultJungNode node:getVertices()) {
            if (name.equals(node.getLabel())) {
                return node;
            }
        }
        return null;
    }

}
