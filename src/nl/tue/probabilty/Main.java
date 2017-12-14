package nl.tue.probabilty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static final int NUM_MP = 150;
    public static final int SPEAKERS_PER_SIDE = 10;
    public static final VoteOptions START_SIDE = VoteOptions.PRO;
    public static final VoteOptions SECOND_SIDE = START_SIDE.otherSide();

    private final MP[] mps = new MP[NUM_MP];

    private final List<MP> spoken = new ArrayList<>(SPEAKERS_PER_SIDE * 2);


    public Main() {
        Random rand = new Random();
        //initialize the mps
        for(int i = 0; i < NUM_MP; i++) {
            mps[i] = new MP(rand.nextInt(MP.OPINION_MAX/2) - MP.OPINION_MAX/4 - 1, rand.nextInt(5));
        }

        System.out.println("Initial setup state:");
        printVoteResult();
    }

    private void start() {
        //play out the rounds
        for(int i = 0; i < SPEAKERS_PER_SIDE; i++) {
            MP mp = getBestSpeaker(START_SIDE);
            speakToOthers(mp);

            mp = getBestSpeaker(SECOND_SIDE);
            speakToOthers(mp);
            // System.out.println("After speaking " + i);
            // printVoteResult();
        }

        System.out.println("Final result:");
        printVoteResult();
    }

    private void speakToOthers(MP mp) {
        if (mp != null) {
            spoken.add(mp);
            int influence = mp.speak() * mp.vote().opinionModifier();
            // System.out.println("I:" + influence);
            for(int j = 0; j < NUM_MP; j++) {
                if (mps[j] != mp) {
                    mps[j].listen(influence);
                }
            }
        } else {
            System.out.println("No one left to speak");
        }
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


    public static void main(String[] args) {
        new Main().start();
    }
}
