package nl.tue.probabilty;

import java.util.Random;

public class Setup {

    public MP[] generateMPs(int run) {
        MP[] mps = new MP[LowerChambers.NUM_MP];
        for (int i = 0; i < LowerChambers.NUM_MP; i++) {
            mps[i] = new MP(0,0, 0.0);
        }
        Random rand = new Random();


        return mps;
    }

    private static class NormalDistribution {

        private Random rand;
        private final double mean;
        private final double variance;

        public NormalDistribution(double mean, double variance) {
            this.rand = new Random();
            this.mean = mean;
            this.variance = variance;
        }

        private NormalDistribution(long seed, double mean, double variance) {
            this.rand = new Random(seed);
            this.mean = mean;
            this.variance = variance;
        }

        private double nextValue() {
            //next normal value with mean and variance
            return rand.nextGaussian() * variance + mean;
        }

    }

    private static class UniformIntDistribution {

        private Random rand;
        private final int min;
        private final int spread;

        public UniformIntDistribution(int min, int max) {
            this.rand = new Random();
            this.min = min;
            this.spread = max - min;
        }

        public UniformIntDistribution(long seed, int min, int max) {
            this.rand = new Random(seed);
            this.min = min;
            this.spread = max - min;
        }

        public int nextValue() {
            //generate a value where min <= x <= max
            return rand.nextInt(spread) + min;
        }

    }

}
