package edu.mit.cci.amtprojects.util;


import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 5/23/11
 * Time: 11:29 AM
 */
public class JungUtils {

//    public static DefaultUndirectedJungGraph makeUndirectedGraph(DefaultJungGraph g) {
//        DefaultUndirectedJungGraph graph =
//    }

    public static enum MergePolicy {
        ADD {

            public float merge(DefaultJungEdge one, DefaultJungEdge two) {
                return one.getWeight()+two.getWeight();

            }
        },
        MAX {

            public float merge(DefaultJungEdge one, DefaultJungEdge two) {
                return Math.max(one.getWeight(),two.getWeight());
            }
        };

        public abstract float merge(DefaultJungEdge one, DefaultJungEdge two);
    }

    public static AbstractGraph<DefaultJungNode,DefaultJungEdge> copy(AbstractGraph<DefaultJungNode,DefaultJungEdge> graph, boolean directed) {
       AbstractGraph<DefaultJungNode,DefaultJungEdge> result = directed?new DefaultJungGraph():new DefaultUndirectedJungGraph();
        for (DefaultJungNode node:graph.getVertices()) {
            result.addVertex(node);
        }
        for (DefaultJungEdge edge:graph.getEdges()){
            Pair<DefaultJungNode> pair = graph.getEndpoints(edge);
            result.addEdge(edge,pair.getFirst(),pair.getSecond());
        }
        return result;
    }




    public static void merge(Graph<DefaultJungNode, DefaultJungEdge> to, Graph<DefaultJungNode, DefaultJungEdge> from,MergePolicy policy) {
        if (from == null || from.getVertexCount()==0) return;
        Map<String, DefaultJungNode> labelmap = new HashMap<String, DefaultJungNode>();
        for (DefaultJungNode node : to.getVertices()) {
            labelmap.put(node.getLabel(), node);
        }

        for (DefaultJungNode node : from.getVertices()) {
            if (!labelmap.containsKey(node.getLabel())) {
                to.addVertex(node);
                labelmap.put(node.getLabel(), node);
            }
        }

        for (DefaultJungEdge edge : from.getEdges()) {
            //double check if this is using equality or what
            Pair<DefaultJungNode> p = from.getEndpoints(edge);
            DefaultJungNode src = labelmap.get(p.getFirst().getLabel());
            DefaultJungNode dest = labelmap.get(p.getSecond().getLabel());
            DefaultJungEdge e = to.findEdge(src, dest);
            if (e != null) {
                e.setWeight(policy.merge(e,edge));
            } else {
                DefaultJungEdge nedge = new DefaultJungEdge();
                nedge.setWeight(edge.getWeight());
                to.addEdge(nedge, src, dest);
            }
        }
    }

    public static void merge(Graph<DefaultJungNode, DefaultJungEdge> to, Graph<DefaultJungNode, DefaultJungEdge> from) {
        merge(to,from, MergePolicy.ADD);
    }




    public static void writeGenericFile(Writer out, AbstractGraph<DefaultJungNode,DefaultJungEdge> graph) throws IOException {
        for (DefaultJungEdge e:graph.getEdges()) {
            Pair<DefaultJungNode> pair  = graph.getEndpoints(e);
            out.write(pair.getFirst().getLabel() + " " + pair.getSecond().getLabel() + " " + e.getWeight()+"\n");
        }
        out.flush();
    }


}
