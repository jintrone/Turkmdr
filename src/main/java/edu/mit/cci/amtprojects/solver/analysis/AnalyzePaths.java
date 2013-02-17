package edu.mit.cci.amtprojects.solver.analysis;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.solver.Question;
import edu.mit.cci.amtprojects.solver.Solution;
import edu.mit.cci.amtprojects.solver.SolutionRank;
import edu.mit.cci.amtprojects.util.DefaultJungEdge;
import edu.mit.cci.amtprojects.util.DefaultJungGraph;
import edu.mit.cci.amtprojects.util.DefaultJungNode;
import edu.mit.cci.amtprojects.util.Utils;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conn.DriverDataSource;
import org.apache.commons.collections15.Transformer;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 12/12/12
 * Time: 7:33 AM
 */
public class AnalyzePaths {

    private Map<Solution, int[]> metrics = new HashMap<Solution, int[]>();


    public AnalyzePaths() throws SQLException {
        DataSource dataSource = new DriverDataSource("com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/amtsolutions-analysis2",
                "amtsolutions",
                "amtsolutions");

        Configuration config = Configuration.getSharedConfiguration();
        DataDomain domain = config.getDomain("KickballData");
        DataNode node = domain.getNode("SolutionData");
        node.setDataSource(dataSource);
    }

    public Question findQuestion(long questionid) {
        Question q = DataObjectUtils.objectForPK(DbProvider.getContext(), Question.class, questionid);
        return q;
    }


    public void printPaths(Question q) throws SQLException {
        metrics.clear();

        List<Solution> sols = q.getToSolutions();
        for (Solution s : sols) {
            if (s.getToParents().isEmpty()) {
                metrics.put(s, new int[]{0, 0});
                continue;
            }
            print(Collections.singletonList(s), 0);
        }
    }


    public int print(List<Solution> s, int count) {
        List<Solution> parents = new ArrayList<Solution>();
        for (Solution p : s) {
            metrics.put(p, new int[]{0, 0});
            if (p.getToParents() != null) {
                metrics.get(p)[0] = p.getToParents().size();
                parents.addAll(p.getToParents());
            }
        }
        int depth = 0;
        if (!parents.isEmpty()) {
            depth = print(parents, count + 1) + 1;
        }
        for (Solution p : s) {
            metrics.get(p)[1] = depth;

        }
        System.out.println("*************** Level " + count + " *****************");
        for (Solution sp : s) {
            System.out.println(sp.getId() + " -- " + sp.getText());
            System.out.println("------------------------------------");
        }
        if (count == 0) {
            System.out.println("\n");
        }
        return depth;

    }

    public static DefaultJungGraph createSolutionGraph(Question q) {
        DefaultJungGraph graph = new DefaultJungGraph();

        Map<Solution, List<DefaultJungNode>> nodemap = new HashMap<Solution, List<DefaultJungNode>>();
        int id = 0;
        for (Solution s : q.getToSolutions()) {
            List<DefaultJungNode> nodes = new ArrayList<DefaultJungNode>();
            nodemap.put(s, nodes);
            DefaultJungNode node = new DefaultJungNode("" + (id++));
            node.setAttribute("Age", s.getRound()+1);
            node.setAttribute("Weight", 0f);
            node.setAttribute("SolutionId",s.getId());
            nodes.add(node);
            graph.addVertex(node);

            for (SolutionRank r : s.getToRanks()) {
                if (r.getRound()!=(Integer)node.getAttribute("Age")-1) {
                    node = new DefaultJungNode("" + (id++));
                    nodes.add(node);
                    node.setAttribute("Age", r.getRound()+1);
                    node.setAttribute("SolutionId",s.getId());

                    graph.addVertex(node);
                }
                node.setAttribute("Weight", r.getRankValue());
            }
            Collections.sort(nodes, new Comparator<DefaultJungNode>() {
                public int compare(DefaultJungNode defaultJungNode, DefaultJungNode defaultJungNode1) {
                    int age0 = (Integer) defaultJungNode.getAttribute("Age");
                    int age1 = (Integer) defaultJungNode.getAttribute("Age");
                    return age0 - age1;
                }
            });
            for (int i = 1; i < nodes.size(); i++) {
                graph.addEdge(nodes.get(i - 1), nodes.get(i), 1.0f);
            }
        }

        for (Map.Entry<Solution, List<DefaultJungNode>> ent : nodemap.entrySet()) {
            DefaultJungNode target = ent.getValue().get(0);
            for (Solution s : ent.getKey().getToParents()) {
                for (DefaultJungNode n : nodemap.get(s)) {
                    if ((Integer) n.getAttribute("Age") + 1 == (Integer) target.getAttribute("Age")) {
                        graph.addEdge(n, target, 1.0f);
                        break;
                    }
                }
            }
        }

        return graph;

    }

    public static void writeGraphML(DefaultJungGraph graph, Map<String, Object> nodeatts, String filename) throws IOException {
        MyGraphMLWriter<DefaultJungNode, DefaultJungEdge> writer = new MyGraphMLWriter<DefaultJungNode, DefaultJungEdge>();
        for (Map.Entry<String, Object> ent : nodeatts.entrySet()) {
            final String key = ent.getKey();
            final String[] clazz = new String[]{"string"};
            if (ent.getValue() instanceof Integer) {
                clazz[0] = "int";
            } else if (ent.getValue() instanceof Float || ent.getValue() instanceof Double) {
                clazz[0] = "decimal";
            }


            writer.addVertexData(ent.getKey(), "", clazz[0], ent.getValue().toString(), new Transformer<DefaultJungNode, String>() {

                public String transform(DefaultJungNode defaultJungNode) {
                    return defaultJungNode.getAttribute(key).toString();
                }
            });
        }
        FileWriter out = new FileWriter(filename);
        writer.save(graph, out);

    }

    public static void main(String[] args) throws SQLException, IOException {
        long qid = 1881;
        AnalyzePaths analysis = new AnalyzePaths();
        Question q = analysis.findQuestion(qid);
        DefaultJungGraph g = AnalyzePaths.createSolutionGraph(q);
        AnalyzePaths.writeGraphML(g, Utils.mapify("Age",0,"SolutionId",0,"Weight",0f),"SolutionsBatch2021.plain.graphml");

//        Long[] qs = new Long[]{1500l, 1501l, 1502l, 1503l, 1504l, 1505l};
//        AnalyzePaths paths = new AnalyzePaths();
//        for (Long q : qs) {
//            System.out.println("############## QUESTION " + q);
//            paths.printPaths(q);
//            System.out.println("\"qid\",\"id\",\"breadth\",\"depth\"");
//            for (Map.Entry<Solution,int[]> ent:paths.metrics.entrySet()) {
//                System.out.println(ent.getKey().getToQuestion().getId()+","+ent.getKey().getId()+","+ent.getValue()[0]+","+ent.getValue()[1]);
//            }
    }



}
