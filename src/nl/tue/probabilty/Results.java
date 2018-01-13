package nl.tue.probabilty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class Results {

    public static final int DEFAULT_RUNS = 10000;

    public static final int DATA_PER_ROUND = VoteOptions.values().length;
    public static final int TOTAL_MEASURE_POINTS = LowerChambers.SPEAKERS_PER_SIDE + 1;

    private final int runs;
    private final Setup setup;
    private int[][][] data;
    private VoteOptions[][] finalVotes;

    public Results() {
        this(Setup.getDefault());
    }

    public Results(Setup setup) {
        this(DEFAULT_RUNS, setup);
    }

    public Results(int runs, Setup setup) {
        if (runs <= 0) {
            throw new IllegalArgumentException("Must do at least one run");
        }
        this.runs = runs;
        this.setup = setup;
        createStorage();
    }

    private void createStorage() {
        data = new int[runs][TOTAL_MEASURE_POINTS][DATA_PER_ROUND];
        finalVotes = new VoteOptions[runs][TOTAL_MEASURE_POINTS];
    }

    private void executeRun(int run) {
        LowerChambers lc = new LowerChambers(setup, run);
        //get initial vote
        RoundResult result = lc.getCurrentResult();
        data[run][0] = result.getVotes();
        finalVotes[run][0] = result.getTotalVote();
        for (int i = 1; i < TOTAL_MEASURE_POINTS; i++) {
            lc.runRound();
            //collect vote after each round of discussion
            result = lc.getCurrentResult();
            data[run][i] = result.getVotes();
            finalVotes[run][i] = result.getTotalVote();
        }

    }

    public void runAll() {
        for (int i = 0; i < runs; i++) {
            executeRun(i);
            if (i % 1000 == 0) {
                System.out.println("Did " + i + " runs");
            }
        }
        System.out.println("\n\n\n");
        System.out.println(resultsToString());
    }

    public void runToFile(String name) {
        runToFile(name, false);
    }

    public void runToFile(String name, String baseDir) {
        runToFile(name, false, baseDir);
    }

    public void runToFile(String name, boolean both) {
        runToFile(name, both, ".");
    }

    public void runToFile(String name, boolean both, String baseDir) {
        File dir = new File(baseDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("Could not create directory");
            }
        }
        if (!dir.isDirectory()) {
            System.out.println(dir.getAbsolutePath());
            dir = new File(".");
        }

        int file = 0;
        String fileName = "run-" + file + "-" + name;
        while(new File(dir, fileName + (both ? "-pro-starts" : "") + ".txt").exists() && ++file <
                100) {
            fileName = "run-" +file + "-" + name;
        }

        LowerChambers.setStartSide(VoteOptions.PRO);
        for (int i = 0; i < runs; i++) {
            executeRun(i);
            if (i % 1000 == 0) {
                System.out.println("Did " + i + " runs");
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(new File(dir, fileName +
                (both ? "-pro-starts" : "") + ".txt").toPath())) {
            writer.write(resultsToString());
        } catch(IOException e) {
            e.printStackTrace();
        }

        if (!both) {
            // we only test one side
            return;
        }

        //empty data
        createStorage();
        LowerChambers.setStartSide(VoteOptions.AGAINST);
        for (int i = 0; i < runs; i++) {
            executeRun(i);
            if (i % 1000 == 0) {
                System.out.println("Did " + i + " runs AGAINST");
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(new File(dir, fileName +
                "-against-starts" + ".txt").toPath())) {
            writer.write(resultsToString());
        } catch(IOException e) {
            e.printStackTrace();
        }


        System.out.println("Saved to " + fileName + ".txt");
    }

    private String resultsToString() {
        double[][] medians = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];
        double[][] q1s = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];
        double[][] q3s = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];

        double[][] means = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];
        double[][] mins = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];
        double[][] maxs = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];

        int[][] finalVotesCount = new int[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];
        double[][] finalPercentage = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];

        for (int i = 0; i < TOTAL_MEASURE_POINTS; i++) {

            for (int k = 0; k < runs; k++) {
                //increment the winning vote option from run k in round i
                finalVotesCount[finalVotes[k][i].ordinal()][i]++;
            }
            for (int j = 0; j < DATA_PER_ROUND; j++) {
                List<Integer> values = new ArrayList<>(runs);
                for (int k = 0; k < runs; k++) {
                    values.add(data[k][i][j]);
                }

                //sort the values
                values.sort(Integer::compareTo);

                double median;
                double q1;
                double q3;

                int vSize = values.size();
                int hSize = vSize / 2;
                int qSize = vSize / 4;
                if (vSize % 2 == 0) {
                    //even size pick average of two middle elements
                    median = (values.get(hSize) + values.get(hSize + 1)) / 2.0;
                    q1 = (values.get(qSize) + values.get(qSize - 1)) / 2.0;
                    q3 = (values.get(vSize - qSize) + values.get(vSize - qSize - 1)) / 2.0;
                } else {
                    //uneven size just pick the middle one
                    median = values.get(hSize);
                    q1 = (values.get(qSize) + values.get(qSize - 1)) / 2.0;
                    q3 = (values.get(vSize - qSize) + values.get(vSize - qSize - 1)) / 2.0;
                }
                //put the median in
//                System.out.println("Median for round " + i + " datapoint " + j + " == " + median);
                medians[j][i] = median;
                q1s[j][i] = q1;
                q3s[j][i] = q3;

                DoubleSummaryStatistics dss = values.stream().mapToDouble(a -> a).summaryStatistics();
                //calculate mean

                means[j][i] = dss.getAverage();
                maxs[j][i] = dss.getMax();
                mins[j][i] = dss.getMin();
            }
        }

        for (int i = 0; i < finalVotesCount.length; i++) {
            for (int j = 0; j < finalVotesCount[i].length; j++) {
                finalPercentage[i][j] = finalVotesCount[i][j] / (double) runs;
            }
        }

        //output as csv for now

        StringBuilder mainBuilder = new StringBuilder("Means\n\n");
        mainBuilder.append(valuesToCSV(means));

        mainBuilder.append("\nMedians\n");
        mainBuilder.append(valuesToCSV(medians));

        mainBuilder.append("\nMins\n");
        mainBuilder.append(valuesToCSV(mins));

        mainBuilder.append("\nMaxs\n");
        mainBuilder.append(valuesToCSV(maxs));

        mainBuilder.append("\nQ1\n");
        mainBuilder.append(valuesToCSV(q1s));

        mainBuilder.append("\nQ3\n");
        mainBuilder.append(valuesToCSV(q3s));

        mainBuilder.append("\nWINS\n");
        mainBuilder.append(runPartToCSV(finalVotesCount));

        mainBuilder.append("\n% WINS\n");
        mainBuilder.append(valuesToCSV(finalPercentage));

        return  mainBuilder.toString();
    }

    private String valuesToCSV(double[][] values) {
        StringBuilder builder = new StringBuilder();
        for (int vote = 0; vote < DATA_PER_ROUND; vote++) {
            builder.append(VoteOptions.values()[vote].getName()).append(":");

            for (int i = 0; i < TOTAL_MEASURE_POINTS; i++) {
                builder.append(";").append(String.format("%10.2f", values[vote][i]));
            }
            //print median to out
            builder.append("\n");
        }
        return builder.toString();
    }

    private String runPartToCSV(int[][] values) {
        StringBuilder builder = new StringBuilder();
        for (int vote = 0; vote < DATA_PER_ROUND; vote++) {
            builder.append(VoteOptions.values()[vote].getName()).append(":");

            for (int i = 0; i < TOTAL_MEASURE_POINTS; i++) {
                builder.append(";").append(String.format("%6d", values[vote][i]));
            }
            //print median to out
            builder.append("\n");
        }
        return builder.toString();
    }

    public static class RoundResult {
        private final int[] votes;
        private final VoteOptions totalVote;

        public RoundResult(int[] votes) {
            this.votes = votes;
            if (votes[VoteOptions.PRO.ordinal()] > votes[VoteOptions.AGAINST.ordinal()]) {
                totalVote = VoteOptions.PRO;
            } else if (votes[VoteOptions.PRO.ordinal()] < votes[VoteOptions.AGAINST.ordinal()]) {
                totalVote = VoteOptions.AGAINST;
            } else {
                totalVote = VoteOptions.NEUTRAL;
            }
        }

        public int[] getVotes() {
            return votes;
        }

        public VoteOptions getTotalVote() {
            return totalVote;
        }
    }

}
