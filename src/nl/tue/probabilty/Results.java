package nl.tue.probabilty;

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
        }

    }

    public void runAll() {
        for (int i = 0; i < runs; i++) {
            executeRun(i);
        }
        analyzeResults();
    }

    private void analyzeResults() {
        //todo analyze the collected data
    }

}
