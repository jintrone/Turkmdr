package edu.mit.cci.amtprojects.kickball.analysis;

import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.solver.Question;
import edu.mit.cci.amtprojects.solver.Solution;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conn.DriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jintrone
 * Date: 12/12/12
 * Time: 7:33 AM
 */
public class AnalyzePaths {



    private Map<Solution,int[]> metrics = new HashMap<Solution,int[]>();


    public void printPaths(long questionid) throws SQLException {
         metrics.clear();
        DataSource dataSource = new DriverDataSource("com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/amtsolutions-analysis",
                "amtsolutions",
                "amtsolutions");

        Configuration config = Configuration.getSharedConfiguration();
        DataDomain domain = config.getDomain("KickballData");
        DataNode node = domain.getNode("SolutionData");
        node.setDataSource(dataSource);

        Question q = DataObjectUtils.objectForPK(DbProvider.getContext(), Question.class, questionid);
        List<Solution> sols = q.getToSolutions();
        for (Solution s : sols) {
            if (s.getToParents().isEmpty()) {
                metrics.put(s,new int[] {0,0});
                continue;
            }
            print(Collections.singletonList(s), 0);
        }
    }


    public int print(List<Solution> s, int count) {
        List<Solution> parents = new ArrayList<Solution>();
        for (Solution p : s) {
            metrics.put(p,new int[]{0,0});
            if (p.getToParents() != null) {
                metrics.get(p)[0] = p.getToParents().size();
                parents.addAll(p.getToParents());
            }
        }
        int depth = 0;
        if (!parents.isEmpty()) {
            depth = print(parents, count + 1)+1;
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

    public static void main(String[] args) throws SQLException {
        Long[] qs = new Long[]{1500l, 1501l, 1502l, 1503l, 1504l, 1505l};
        AnalyzePaths paths = new AnalyzePaths();
        for (Long q : qs) {
            System.out.println("############## QUESTION " + q);
            paths.printPaths(q);
//            System.out.println("\"qid\",\"id\",\"breadth\",\"depth\"");
//            for (Map.Entry<Solution,int[]> ent:paths.metrics.entrySet()) {
//                System.out.println(ent.getKey().getToQuestion().getId()+","+ent.getKey().getId()+","+ent.getValue()[0]+","+ent.getValue()[1]);
//            }
        }
    }


}
