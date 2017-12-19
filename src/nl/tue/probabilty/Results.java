package nl.tue.probabilty;

import java.util.*;

public class Results {

    public static final int DEFAULT_RUNS = 10000;

    public static final int DATA_PER_ROUND = VoteOptions.values().length;
    public static final int TOTAL_MEASURE_POINTS = LowerChambers.SPEAKERS_PER_SIDE + 1;

    private final int runs;
    private final Setup setup;
    private int[][][] data;

    public Results() {
        this(new Setup());
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
    }

    private void executeRun(int run) {
        LowerChambers lc = new LowerChambers(setup, run);
        //get initial vote
        data[run][0] = lc.resultsFromVote();
        for (int i = 1; i < TOTAL_MEASURE_POINTS; i++) {
            lc.runRound();
            //collect vote after each round of discussion
            data[run][i] = lc.resultsFromVote();
//            System.out.println("Result for run " + run + ", round= " + i + " : "
//                    + Arrays.toString(data[run][i]));
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
        analyzeResults();
    }

    private void analyzeResults() {
        double[][] medians = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];

        double[][] means = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];
        double[][] mins = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];
        double[][] maxs = new double[DATA_PER_ROUND][TOTAL_MEASURE_POINTS];
        for (int i = 0; i < TOTAL_MEASURE_POINTS; i++) {
            for (int j = 0; j < DATA_PER_ROUND; j++) {
                List<Integer> values = new ArrayList<>(runs);
                for (int k = 0; k < runs; k++) {
                    values.add(data[k][i][j]);
                }

                //sort the values
                values.sort(Integer::compareTo);

                double median;

                int vSize = values.size();
                if (vSize % 2 == 0) {
                    //even size pick average of two middle elements
                    median = (values.get(vSize / 2) + values.get(vSize / 2 + 1)) / 2.0;
                } else {
                    //uneven size just pick the middle one
                    median = values.get(vSize / 2);

                }
                //put the median in
                System.out.println("Median for round " + i + " datapoint " + j + " == " + median);
                medians[j][i] = median;

                DoubleSummaryStatistics dss = values.stream().mapToDouble(a -> a).summaryStatistics();
                //calculate mean

                means[j][i] = dss.getAverage();
                maxs[j][i] = dss.getMax();
                mins[j][i] = dss.getMin();

            }
        }

        //output as csv for now

        System.out.println("Means\n");
        valuesToCSV(means);

        System.out.println("\nMedians\n");
        valuesToCSV(medians);

        System.out.println("\nMins\n");
        valuesToCSV(mins);

        System.out.println("\nMaxs\n");
        valuesToCSV(maxs);
    }

    private void valuesToCSV(double[][] values) {
        for (int vote = 0; vote < DATA_PER_ROUND; vote++) {
            StringBuilder builder = new StringBuilder(VoteOptions.values()[vote].name() + ":");

            for (int i = 0; i < TOTAL_MEASURE_POINTS; i++) {
                builder.append(";").append(Double.toString(values[vote][i]).replace(".", ","));
            }
            //print median to out
            System.out.println(builder.toString());
        }
    }

}
