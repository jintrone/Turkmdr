package edu.mit.cci.amtprojects.util;

import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 6/1/11
 * Time: 5:22 PM
 */
public class DefaultUndirectedJungGraph extends UndirectedSparseMultigraph<DefaultJungNode, DefaultJungEdge> {

    Map<String, DefaultJungNode> accelerator = new HashMap<String, DefaultJungNode>();

    public DefaultJungNode addNode(String name) {
        DefaultJungNode node = new DefaultJungNode(name);
        this.addVertex(node);
        return node;
    }

    public DefaultJungEdge addEdge(DefaultJungNode one, DefaultJungNode two, float weight) {
        DefaultJungEdge e = new DefaultJungEdge(weight);
        this.addEdge(e, one, two);
        return e;
    }

    public DefaultJungNode findVertex(String name) {
        if (accelerator.containsKey(name)) {
            return accelerator.get(name);
        } else if (accelerator.size() == getVertexCount()) {
            return null;
        } else {
            for (DefaultJungNode node : getVertices()) {
                if (name.equals(node.getLabel())) {
                    accelerator.put(name,node);
                    return node;
                }
            }
        }
        return null;
    }
}
