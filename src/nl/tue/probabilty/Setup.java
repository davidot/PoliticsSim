package nl.tue.probabilty;

import java.util.Random;

public class Setup {

    public MP[] generateMPs(int run) {
        MP[] mps = new MP[LowerChambers.NUM_MP];
        for (int i = 0; i < LowerChambers.NUM_MP; i++) {
            mps[i] = new MP(0,0);
        }
        Random rand = new Random();


        return mps;
    }

    private static double nextNormal(Random rand, double mean, double variance) {
        //next normal value with mean and variance
        return rand.nextGaussian() * variance + mean;
    }

    private static int nextUniformInt(Random rand, int min, int max) {
        //generate a value where min <= x <= max
        return rand.nextInt(max - min) + min;
    }

}
