package nl.tue.probabilty;

import java.util.Random;

public abstract class Setup {

    public static Setup getDefault() {
        return new Setup() {
            @Override
            public MP[] generateMPs(int run) {
                MP[] mps = new MP[LowerChambers.NUM_MP];
                Random rand = new Random();
                NormalDistribution neutralOpinionDist = new NormalDistribution(rand.nextLong(), 0,
                        1000);
                for (int i = 0; i < LowerChambers.NUM_MP; i++) {
                    mps[i] = new MP(neutralOpinionDist.nextIntValue(), 1, 1.0);
                }
                return mps;
            }
        };
    }

    public static Setup getDefaultConsistency() {
        return new Setup() {
            @Override
            public MP[] generateMPs(int run) {
                MP[] mps = new MP[LowerChambers.NUM_MP];
                for (int i = 0; i < LowerChambers.NUM_MP; i+= 3) {
                    mps[i] = new MP(500, 1, 1.0);
                    mps[i + 1] = new MP(0, 1, 1.0);
                    mps[i + 2] = new MP(-500, 1, 1.0);
                }
                return mps;
            }
        };
    }

    public abstract MP[] generateMPs(int run);


    public static Setup getStubbornMinority(int minSize, double minMean, double minVar, double
            minStub, double majMean, double majVar, double majStub) {
        return new StubbornMinority(minSize, majMean, majVar, minMean, minVar, majStub, minStub);
    }

    private static class StubbornMinority extends Setup {

        private final int minoritySize;

        private final double meanMax;
        private final double varMaj;
        private final double meanMin;
        private final double varMin;

        private final double stubMaj;
        private final double stubMin;

        public StubbornMinority(int minoritySize, double meanMaj, double varMaj, double meanMin,
                                double varMin, double stubMaj, double stubMin) {
            if (minoritySize > 150 || minoritySize < 0) {
                throw new IllegalArgumentException("Split must be between 0 and 150");
            }
            this.minoritySize = minoritySize;
            this.meanMax = meanMaj;
            this.varMaj = varMaj;
            this.meanMin = meanMin;
            this.varMin = varMin;
            this.stubMaj = stubMaj;
            this.stubMin = stubMin;
        }

        @Override
        public MP[] generateMPs(int run) {
            MP[] mps = new MP[LowerChambers.NUM_MP];
            Random rand = new Random();
            NormalDistribution normalMaj = new NormalDistribution(rand.nextLong(), meanMax, varMaj);
            NormalDistribution normalMin = new NormalDistribution(rand.nextLong(), meanMin, varMin);

            for (int i = 0; i < minoritySize; i++) {
                mps[i] = new MP(normalMin.nextIntValue(), 1, stubMin);
            }

            for (int i = minoritySize; i < LowerChambers.NUM_MP; i++) {
                mps[i] = new MP(normalMaj.nextIntValue(), 1, stubMaj);
            }


            return mps;
        }
    }

    public static Setup getNormalOpSetup(double mean, double var, int speech, double stub) {
        return new NormalOpNoSpeechOrStub(mean, var, speech, stub);
    }

    private static class NormalOpNoSpeechOrStub extends Setup {

        private final int speech;
        private final double stub;
        private final double var;
        private final double mean;

        public NormalOpNoSpeechOrStub(double mean, double var, int speech, double stub) {
            this.speech = speech;
            this.stub = stub;
            this.var = var;
            this.mean = mean;
        }

        public MP[] generateMPs(int run) {
            MP[] mps = new MP[LowerChambers.NUM_MP];
            Random rand = new Random();
            NormalDistribution normalOpinionDist = new NormalDistribution(rand.nextLong(), mean,
                    var);

            for (int i = 0; i < LowerChambers.NUM_MP; i++) {
                mps[i] = new MP(normalOpinionDist.nextIntValue(), speech, stub);
            }
            return mps;
        }
    }


    public static Setup getRootNTest(int pro, int against, double stubborn) {
        return new RootNTestSetup(pro, against, stubborn);
    }

    private static class RootNTestSetup extends Setup {
        private final int pro;
        private final int against;
        private final double stubborn;

        public RootNTestSetup(int pro, int against, double stubborn) {
            this.pro = pro;
            this.against = against;
            this.stubborn = stubborn;
        }

        @Override
        public MP[] generateMPs(int run) {
            MP[] mps = new MP[LowerChambers.NUM_MP];
            Random rand = new Random();
            NormalDistribution proOpinionDist = new NormalDistribution(rand.nextLong(), 500, 50);
            NormalDistribution neutralOpinionDist = new NormalDistribution(rand.nextLong(), 0,
                    1000);
            NormalDistribution againstOpinionDist = new NormalDistribution(rand.nextLong(), 500,
                    50);

            for (int i = 0; i < pro; i++) {
                mps[i] = new MP(proOpinionDist.nextIntValue(), 1, stubborn);
            }

            for (int i = pro; i < pro + against; i++) {
                mps[i] = new MP(againstOpinionDist.nextIntValue(), 1, stubborn);
            }

            for (int i = pro + against; i < LowerChambers.NUM_MP; i++) {
                mps[i] = new MP(neutralOpinionDist.nextIntValue(), 1, stubborn);
            }

            return mps;
        }
    }

    public static class NormalDistribution {

        private Random rand;
        private final double mean;

        private final double variance;

        public NormalDistribution(double mean, double variance) {
            this.rand = new Random();
            this.mean = mean;
            this.variance = variance;
        }

        public NormalDistribution(long seed, double mean, double variance) {
            this.rand = new Random(seed);
            this.mean = mean;
            this.variance = variance;
        }

        public double nextValue() {
            //next normal value with mean and variance
            return rand.nextGaussian() * variance + mean;
        }

        public int nextIntValue() {
            return (int) nextValue();
        }

    }
    public static class UniformIntDistribution {

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
