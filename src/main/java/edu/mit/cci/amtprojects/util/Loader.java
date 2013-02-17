package edu.mit.cci.amtprojects.util;


import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.io.PajekNetWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 4/21/11
 * Time: 10:53 AM
 */
public class Loader {


    public static DirectedSparseMultigraph<DefaultJungNode, DefaultJungEdge> loadGraph(File file) throws IOException {
        Reader fileReader = new UnicodeReader(new FileInputStream(file), "UTF-8");
        DirectedSparseMultigraph<DefaultJungNode, DefaultJungEdge> graph = new DirectedSparseMultigraph<DefaultJungNode, DefaultJungEdge>();
        PajekNetReader<DirectedSparseMultigraph<DefaultJungNode, DefaultJungEdge>, DefaultJungNode, DefaultJungEdge> graphreader = new PajekNetReader<DirectedSparseMultigraph<DefaultJungNode, DefaultJungEdge>, DefaultJungNode, DefaultJungEdge>(DefaultJungNode.getFactory(), DefaultJungEdge.getFactory());
        graphreader.setEdgeWeightTransformer(new MapSettableTransformer<DefaultJungEdge, Number>(new HashMap<DefaultJungEdge, Number>()));
        graphreader.setVertexLabeller(new MapSettableTransformer<DefaultJungNode, String>(new HashMap<DefaultJungNode, String>()));
        graphreader.load(fileReader, graph);
        for (DefaultJungNode node : graph.getVertices()) {
            node.setLabel(graphreader.getVertexLabeller().transform(node));
        }
        for (DefaultJungEdge edge : graph.getEdges()) {
            edge.setWeight(graphreader.getEdgeWeightTransformer().transform(edge).floatValue());
        }
        return graph;

    }

     public static void writeGraph(Graph<DefaultJungNode,DefaultJungEdge> graph, OutputStream stream, boolean labels, boolean weights) throws IOException {
        PajekNetWriter<DefaultJungNode, DefaultJungEdge> writer = new PajekNetWriter<DefaultJungNode, DefaultJungEdge>();
        OutputStreamWriter os = new OutputStreamWriter(stream);
         Map<DefaultJungNode,String> labelmap = new HashMap<DefaultJungNode, String>();
         Map<DefaultJungEdge,Number> weightmap = new HashMap<DefaultJungEdge,Number>();
         if (labels) {
             for (DefaultJungNode n:graph.getVertices()) {
                 labelmap.put(n,n.getLabel());
             }
         }

         if (weights) {
             for (DefaultJungEdge e:graph.getEdges()) {
                 weightmap.put(e,e.getWeight());
             }
         }

        writer.save(graph,os,new MapSettableTransformer<DefaultJungNode, String>(labelmap),new MapSettableTransformer<DefaultJungEdge, Number>(weightmap));
        //os.flush();
     }
}
