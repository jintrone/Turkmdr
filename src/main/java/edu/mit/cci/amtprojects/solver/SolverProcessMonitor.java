package edu.mit.cci.amtprojects.solver;

import edu.cci.amtprojects.HitManager;
import edu.mit.cci.amtprojects.BatchManager;
import edu.mit.cci.amtprojects.DbProvider;
import edu.mit.cci.amtprojects.kickball.cayenne.Batch;
import edu.mit.cci.amtprojects.kickball.cayenne.Experiment;
import edu.mit.cci.amtprojects.kickball.cayenne.TurkerLog;
import edu.mit.cci.amtprojects.util.CayenneUtils;
import edu.mit.cci.amtprojects.util.MturkUtils;
import jsc.datastructures.MatchedData;
import jsc.descriptive.MeanVar;
import jsc.relatedsamples.FriedmanTest;
import org.apache.cayenne.DataObjectUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * User: jintrone
 * Date: 10/15/12
 * Time: 2:55 PM
 */
public class SolverProcessMonitor {

    long experimentId;
    Timer t;
    boolean running = false;


    private final static Map<Long, SolverProcessMonitor> monitorMap = new HashMap<Long, SolverProcessMonitor>();


    private static Logger logger = Logger.getLogger(SolverProcessMonitor.class);

    private SolverProcessMonitor(Experiment e) {
        experimentId = e.getExperimentId();
       // if (!running) restart();
    }

    public boolean isRunning() {
        return running;
    }


    public void restart() {
        if (t != null) {
            t.cancel();

        }

        t = new Timer() {
            public void cancel() {
                super.cancel();
                running = false;

                cleanup();


            }
        };

        t.schedule(new TimerTask() {

            public boolean cancel() {
                boolean result = super.cancel();
                t.cancel();
                return result;

            }

            @Override
            public void run() {
                running = true;
                try {
                    checkStatus();
                } catch (Exception e) {
                   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                   t.cancel();
                }



            }
        }, 0, 60000);
    }

    public void halt() {
        if (running) {
            t.cancel();
        }

    }

    public void cleanup() {
        BatchManager manager = new SolverPluginFactory().getBatchManager();
        for (Batch b : experiment().getToBatch()) {
            manager.haltBatch(b);
        }
        t = null;
    }

    public static SolverProcessMonitor get(Experiment e) {
        if (!(monitorMap.containsKey(e.getExperimentId()))) {
            monitorMap.put(e.getExperimentId(), new SolverProcessMonitor(e));
        }
        return monitorMap.get(e.getExperimentId());
    }

    public void checkStatus() throws UnsupportedEncodingException, JSONException {
        logger.info("Checking status");
        for (Batch b : experiment().getToBatch()) {
            SolverTaskModel model = new SolverTaskModel(b);

            if (model.getCurrentStatus().getPhase() == Phase.COMPLETE) continue;

            int currentRound = model.getCurrentStatus().getCurrentRound();
            HitManager manager = HitManager.get(b);

            List<TurkerLog> logs = manager.getFilteredLogs(false, false, "RESULTS");
            List<TurkerLog> roundLogs = findCurrentLogs(currentRound, model.getCurrentStatus().getPhase(), logs);

            if (!roundLogs.isEmpty()) {
                if (model.getCurrentStatus().getPhase() == Phase.INIT) {

                    updateRanks(b, model, roundLogs);
                    model.getCurrentStatus().setPhase(Phase.GENERATE);

                } else if (model.getCurrentStatus().getPhase() == Phase.RANK) {

                    updateRanks(b, model, roundLogs);
                    pruneSolutions(model);
                    if (currentRound + 1 == model.getNumberOfRounds()) {
                        model.getCurrentStatus().setPhase(Phase.COMPLETE);
                    } else {
                        model.getCurrentStatus().setPhase(Phase.GENERATE);
                        model.getCurrentStatus().setCurrentRound(currentRound + 1);
                    }

                } else if (model.getCurrentStatus().getPhase() == Phase.GENERATE) {
                    updateAnswers(model, roundLogs);
                    model.getCurrentStatus().setPhase(Phase.RANK);
                }

                model.updateCurrentStatus();
                DbProvider.getContext().commitChanges();
                if (model.getCurrentStatus().getPhase() != Phase.COMPLETE) {
                    SolverHitCreator.getInstance().launch(null, null, b);

                }

            }

        }


    }

    private void pruneSolutions(SolverTaskModel model) throws JSONException {
        List<Solution> sol = new ArrayList<Solution>(model.getCurrentStatus().getCurrentAnswers());
        if (sol.size() > model.getSizeOfFront()) {
            Collections.sort(sol, new Comparator<Solution>() {

                public int compare(Solution solution, Solution solution1) {
                    Float r1 = solution.getLastRank().getRankValue();
                    Float r2 = solution1.getLastRank().getRankValue();
                    return -1 * r1.compareTo(r2);

                }
            });

            sol = sol.subList(0, model.getSizeOfFront());
            model.getCurrentStatus().setCurrentAnswers(sol);
            model.updateCurrentStatus();
        }

    }


    private void updateAnswers(SolverTaskModel model, List<TurkerLog> roundLogs) {
        for (TurkerLog l : roundLogs) {
            String text = MturkUtils.extractAnswer(l, "solutiontext");

            Solution s = DbProvider.getContext().newObject(Solution.class);
            s.setText(text);
            s.setAssignmentId(l.getAssignmentId());
            s.setCreation(l.getDate());
            s.setRound(Integer.parseInt(MturkUtils.extractAnswer(l, "round")));
            s.setWorkerId(l.getWorkerId());
            s.setToQuestion(model.getQuestion());


            String parentText = MturkUtils.extractAnswer(l, "parents");
            if (parentText != null && !parentText.isEmpty()) {
                String[] parents = MturkUtils.extractAnswer(l, "parents").split("\\|");
                for (String p : parents) {
                    if (p == null || p.isEmpty()) continue;
                    Solution ps = DataObjectUtils.objectForPK(DbProvider.getContext(), Solution.class, Long.parseLong(p));
                    if (ps == null) {
                        logger.error("Cannot identify parent " + p + " for new solution");

                    } else {
                        s.addToToParents(ps);
                    }
                }

            }
            DbProvider.getContext().commitChanges();
            model.getCurrentStatus().addToCurrentAnswers(s);
        }

        DbProvider.getContext().commitChanges();
    }

    private void updateRanks(Batch b, SolverTaskModel model, List<TurkerLog> logs) throws JSONException {


        //map of each turkers assessment

        //debug
        if (logs.size() == 1) {
            TurkerLog log = new TurkerLog();
            log.setData(logs.get(0).getData());
            logs.add(log);
        }

        String hitId = logs.get(0).getHit();
        int round = Integer.parseInt(MturkUtils.extractAnswer(logs.get(0), "round"));

        Map<TurkerLog, double[]> response = new HashMap<TurkerLog, double[]>();
        SolverTaskStatus status = model.getCurrentStatus();
        List<Solution> answers = new ArrayList<Solution>(status.getCurrentAnswers());

        for (TurkerLog log : logs) {

            List<Double> ranks = new ArrayList<Double>();

            for (Solution s : answers) {
                double rank = (1 + answers.size() - Integer.parseInt(MturkUtils.extractAnswer(log, "Solution." + s.getId()))) / (double) (answers.size());


                ranks.add(rank);
            }
            response.put(log, ArrayUtils.toPrimitive(ranks.toArray(new Double[ranks.size()]), 0d));
        }

        double[][] ranks = response.values().toArray(new double[response.size()][]);
        logger.info("Ranks are:");
        for (double[] rank1 : ranks) {
            logger.info(Arrays.toString(rank1));
        }
        double[] finalRanks = new double[answers.size()];
        float w = (float) new FriedmanTest(new MatchedData(ranks)).getW();
        logger.info("W = " + w);
        //update ranks
        for (int s = 0; s < answers.size(); s++) {
            double[] row = new double[logs.size()];
            for (int r = 0; r < logs.size(); r++) {
                row[r] = ranks[r][s];
            }
            MeanVar mv = new MeanVar(row);
            SolutionRank solutionRank = DbProvider.getContext().newObject(SolutionRank.class);
            solutionRank.setDate(new Date());
            solutionRank.setRound(round);
            solutionRank.setHitId(hitId);
            try {
                finalRanks[s] = mv.getMean();
                solutionRank.setRank(w, (float) mv.getSd(), (float) finalRanks[s]);
            } catch (JSONException ex) {
                logger.error("Could not serialize ranks");
            }
            answers.get(s).addToToRanks(solutionRank);
        }

        DbProvider.getContext().commitChanges();

        //assign bonus to rankers based on kendall's W
        for (int r = 0; r < logs.size(); r++) {
            float agreement = (float) new FriedmanTest(new MatchedData(new double[][]{finalRanks, ranks[r]})).getW();
            float bonus = agreement * model.getMaxRankingBonus();
            String feedback = String.format("Your ranking was similar to the mean ranking with a score of %.2f as assessed by Kendall's W. You are thus granted a bonus " +
                    "of %.2f * %.2f = $%.2f", agreement, agreement, model.getMaxRankingBonus(), bonus);
            if (logs.get(r).getAssignmentId() == null || logs.get(r).getAssignmentId().isEmpty()) {
                continue;
            }
            HitManager.get(b).bonusAssignments(new String[]{logs.get(r).getAssignmentId()}, feedback, bonus);
        }

        //assign bonus to generators
        Collections.sort(answers, new Comparator<Solution>() {
            public int compare(Solution solution, Solution solution1) {
                return -1 * ((Float) solution.getLastRank().getRankValue()).compareTo(solution1.getLastRank().getRankValue());
            }
        });

        for (int i = 0; i < Math.min(model.getSizeOfFront(),answers.size()); i++) {
            Solution s = answers.get(i);
            if (s.getToParents().isEmpty() && s.getRound() == status.getCurrentRound()) {
                float rank = s.getLastRank().getRankValue();
                float bonus = rank * model.getMaxGeneratingBonus();
                String feedback = String.format("Your solution achieved a rank of %.2f (on a 0 - 1 scale) and so" +
                        "you are granted a bonus of %.2f * $%.2f = %.2f", rank, rank, model.getMaxGeneratingBonus(), bonus);
                HitManager.get(b).bonusAssignments(new String[]{s.getAssignmentId()}, feedback, bonus);

            } else if (!s.getToParents().isEmpty() && s.getRound() == status.getCurrentRound()) {
                float old = 0.0f;
                for (Solution sp : s.getToParents()) {
                    old += sp.getLastRank().getRankValue();

                }
                old /= (float) s.getToParents().size();
                float improvement = s.getLastRank().getRankValue() - old;
                float bonus = improvement * model.getMaxCombiningBonus();
                bonus = Float.parseFloat(String.format("%.2f", bonus));
                if (bonus > 0) {
                    String feedback = String.format("Your solution achieved an improvement of %.2f (on a 0 - 1 scale) over its progenitors and so" +
                            "you are granted a bonus of %.2f * $%.2f = %.2f", improvement, improvement, model.getMaxCombiningBonus(), bonus);
                    HitManager.get(b).bonusAssignments(new String[]{s.getAssignmentId()}, feedback, bonus);
                }
            }
        }

    }

    private List<TurkerLog> findCurrentLogs(int currentRound, Phase currentPhase, List<TurkerLog> logs) {
        for (Iterator<TurkerLog> itl = logs.iterator(); itl.hasNext(); ) {
            TurkerLog log = itl.next();
            if (Integer.parseInt(MturkUtils.extractAnswer(log, "round")) != currentRound || !MturkUtils.extractAnswer(log, "phase").equals(currentPhase.name())) {
                itl.remove();
            }

        }
        return logs;
    }


    public Experiment experiment() {
        return CayenneUtils.findExperiment(DbProvider.getContext(), experimentId);

    }


    public static enum Phase {
        INIT, GENERATE, RANK, COMPLETE
    }
}
