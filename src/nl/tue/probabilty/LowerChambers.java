package nl.tue.probabilty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LowerChambers {

    public static final int NUM_MP = 150;
    public static final int SPEAKERS_PER_SIDE = 10;
    private static VoteOptions startSide = VoteOptions.PRO;
    private static VoteOptions secondSide = startSide.otherSide();

    private final MP[] mps;

    private final List<MP> spoken = new ArrayList<>(SPEAKERS_PER_SIDE * 2);

    public LowerChambers(Setup setup, int run) {
        mps = setup.generateMPs(run);
    }

    public LowerChambers() {
        Random rand = new Random();
        //initialize the mps
        mps = new MP[NUM_MP];
        for(int i = 0; i < NUM_MP; i++) {
            mps[i] = new MP(rand.nextInt(MP.OPINION_MAX/2) - MP.OPINION_MAX/4 - 1, rand.nextDouble(), 1.0);
        }

        System.out.println("Initial setup state:");
        printVoteResult();
    }

    private void start() {
        //play out the rounds
        for(int i = 0; i < SPEAKERS_PER_SIDE; i++) {
            runRound();
        }

        System.out.println("Final result:");
        printVoteResult();
    }

    public void runRound() {
        MP first = getBestSpeaker(startSide);
        MP other = getBestSpeaker(secondSide);

        if (first != null) {
            spoken.add(first);
        }

        if (other != null) {
            spoken.add(other);
        }

        int totalInfluence = calcInfluence(first) + calcInfluence(other);

        speakToOthers(totalInfluence, first, other);
    }

    private void speakToOthers(int influence, MP first, MP other) {
        for(int j = 0; j < NUM_MP; j++) {
            if (mps[j] != first && mps[j] != other) {
                mps[j].listen(influence);
            }
        }
    }

    private int calcInfluence(MP mp) {
        if (mp == null) {
            return 0;
        }
        return mp.speak() * mp.vote().opinionModifier();
    }

    private MP getBestSpeaker(VoteOptions side) {
        int maxSpeech = -1;
        MP mpMax = null;
        for(MP mp: mps) {
            if (mp.vote() == side && !spoken.contains(mp)) {
                int speechTotal = mp.speak();
                if (speechTotal > maxSpeech) {
                    mpMax = mp;
                    maxSpeech = speechTotal;
                }
            }
        }
        return mpMax;
    }

    private void printVoteResult() {
        int pro = 0;
        int against = 0;
        int neutral = 0;
        for(MP mp: mps) {
            switch (mp.vote()) {
                case PRO:
                    pro++;
                    break;
                case AGAINST:
                    against++;
                    break;
                case NEUTRAL:
                    neutral++;
                    break;
            }
        }
        System.out.printf("Pro:     (%3d/%3d) %5.2f%%%n", pro, NUM_MP, ((double) (pro)) / NUM_MP);
        System.out.printf("Against: (%3d/%3d) %5.2f%%%n", against, NUM_MP, ((double) (against)) / NUM_MP);
        System.out.printf("Neutral: (%3d/%3d) %5.2f%%%n", neutral, NUM_MP, ((double) (neutral)) / NUM_MP);

        if (pro > against) {
            System.out.println("Result: PRO wins");
        } else if (against > pro) {
            System.out.println("Result: AGAINST wins");
        } else {
            System.out.println("Result: TIE");
        }

    }

    public Results.RoundResult getCurrentResult() {
        int[] results = new int[Results.DATA_PER_ROUND];
        for (int i = 0; i < NUM_MP; i++) {
            results[mps[i].vote().ordinal()]++;
        }
        return new Results.RoundResult(results);
    }

    public static void setStartSide(VoteOptions start) {
        startSide = start;
        secondSide = start.otherSide();
    }

    public static void main(String[] args) {
        new Results(Setup.getDefaultConsistency()).runToFile("consistency", true, "proving");

        for (int i = 0; i < 10; i++) {
            new Results(Setup.getDefault()).runToFile("normal-disted-opinion-" + i, true,
                    "proving");
        }

        for(int i = 1; i < 25; i++) {
            new Results(new Setup.RootNTestSetup(i,0, 1.0)).runToFile("root-n-" +
                    "-accepting-" + i, "rootN");
            new Results(new Setup.RootNTestSetup(i,0)).runToFile("root-n-" +
                    "-normStubb-" + i, "rootN");
        }


        for (int i = 5; i <= 75; i+= 5) {
            new Results(new Setup.StubbornMinority(i, 400.0, 150.0, -600.0, 100.0,
                    1.0, 0.1)).runToFile("stub-minor-" + i, "stubMin");
        }

        for (int i = 51; i < 60; i++) {
            if (i % 5 == 0) {
                //already done in the loop above
                continue;
            }
            new Results(new Setup.StubbornMinority(i, 400.0, 150.0, -600.0, 100.0,
                    1.0, 0.1)).runToFile("stub-minor-" + i, "stubMin");
        }

        for (int i = 0; i <= 75; i++) {
            new Results(new Setup.StubbornMinority(i, 400.0, 200.0, -400.0, 200.0,
                    1.0, 1.0)).runToFile("against-" + i, "tweedeKamer4");
        }

    }
}
