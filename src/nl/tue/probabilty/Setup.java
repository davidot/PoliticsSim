package nl.tue.probabilty;

import java.util.Random;

public abstract class Setup {

    public abstract MP[] generateMPs(int run);

    public static Setup getDefault() {
        return new Setup() {
            @Override
            public MP[] generateMPs(int run) {
                MP[] mps = new MP[LowerChambers.NUM_MP];

                for (int i = 0; i < LowerChambers.NUM_MP; i++) {
                    int opinion = fullNormalDist.nextIntValue();
                    while (Math.abs(opinion) > MP.OPINION_MAX) {
                        opinion = fullNormalDist.nextIntValue();
                    }
                    mps[i] = new MP(opinion, 1.0, 1.0);
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
                    mps[i] = new MP(500, 1.0, 1.0);
                    mps[i + 1] = new MP(0, 1.0, 1.0);
                    mps[i + 2] = new MP(-500, 1.0, 1.0);
                }
                return mps;
            }
        };
    }


    public static class StubbornMinority extends Setup {

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
                mps[i] = new MP(normalMin.nextIntValue(), 1.0, stubMin);
            }

            for (int i = minoritySize; i < LowerChambers.NUM_MP; i++) {
                mps[i] = new MP(normalMaj.nextIntValue(), 1.0, stubMaj);
            }


            return mps;
        }
    }

    public static class NormalOpNoSpeechOrStub extends Setup {

        private final double speech;
        private final double stub;
        private final double var;
        private final double mean;

        public NormalOpNoSpeechOrStub(double mean, double var, double speech, double stub) {
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

    public static class RootNTestSetup extends Setup {
        private final int pro;
        private final int against;
        private final double stubborn;
        private NormalDistribution stubNorm;

        public RootNTestSetup(int pro, int against) {
            this.pro = pro;
            this.against = against;
            this.stubborn = 1.0;

        }

        public RootNTestSetup(int pro, int against, double stubborn) {
            this.pro = pro;
            this.against = against;
            this.stubborn = stubborn;
            this.stubNorm = null;
        }

        @Override
        public MP[] generateMPs(int run) {
            MP[] mps = new MP[LowerChambers.NUM_MP];
            Random rand = new Random();

            this.stubNorm = new NormalDistribution(rand.nextLong(), 0.6, 0.15);

//            NormalDistribution proOpinionDist = new NormalDistribution(rand.nextLong(), 750, 50);
//            NormalDistribution againstOpinionDist = new NormalDistribution(rand.nextLong(), 750,
//                    50);
            NormalDistribution speech = new NormalDistribution(rand.nextLong(), 0.5, 0.25);



            for (int i = 0; i < pro; i++) {
                //add pro
                mps[i] = new MP(800, speech.nextValue(), getStubborn());
            }

            for (int i = pro; i < pro + against; i++) {
                //add against
                mps[i] = new MP(-800, speech.nextValue(), getStubborn());
            }

            for (int i = pro + against; i < LowerChambers.NUM_MP; i++) {
                mps[i] = new MP(fullNormalDist.nextIntValue(), speech.nextValue(), getStubborn());
            }

            return mps;
        }

        private double getStubborn() {
            if (stubNorm != null) {
                return stubNorm.nextValue();
            } else {
                return stubborn;
            }
        }
    }

    public static final TweedeKamerSetup.Party[] roken = new TweedeKamerSetup.Party[] {
            new TweedeKamerSetup.Party(41, -400, 200, 1.0), //VVD
            new TweedeKamerSetup.Party(38, 400, 200, 1.0), //PVDA
            new TweedeKamerSetup.Party(15, -400, 200, 1.0), //PVV
            new TweedeKamerSetup.Party(15, -400, 200, 1.0), //SP
            new TweedeKamerSetup.Party(13, 400, 200, 1.0), //CDA
            new TweedeKamerSetup.Party(12, 400, 200, 1.0), //D66
            new TweedeKamerSetup.Party(5, 400, 200, 1.0), //CU
            new TweedeKamerSetup.Party(4, 400, 200, 1.0), //GL
            new TweedeKamerSetup.Party(3, 400, 200, 1.0), //SGP
            new TweedeKamerSetup.Party(2, 400, 200, 1.0), //PVDD
            new TweedeKamerSetup.Party(2, 400, 200, 1.0), //50PLUS
    };

    public static final TweedeKamerSetup.Party[] goedkoopVlees = new TweedeKamerSetup.Party[] {
            new TweedeKamerSetup.Party(41, -400, 200, 1.0), //VVD
            new TweedeKamerSetup.Party(38, 400, 200, 1.0), //PVDA
            new TweedeKamerSetup.Party(15, -400, 200, 1.0), //PVV
            new TweedeKamerSetup.Party(15, 400, 200, 1.0), //SP
            new TweedeKamerSetup.Party(13, -400, 200, 1.0), //CDA
            new TweedeKamerSetup.Party(12, -400, 200, 1.0), //D66
            new TweedeKamerSetup.Party(5, -400, 200, 1.0), //CU
            new TweedeKamerSetup.Party(4, -400, 200, 1.0), //GL
            new TweedeKamerSetup.Party(3, 400, 200, 1.0), //SGP
            new TweedeKamerSetup.Party(2, -400, 200, 1.0), //PVDD
            new TweedeKamerSetup.Party(2, -400, 200, 1.0), //50PLUS
    };

    public static class TweedeKamerSetup extends Setup {

        private static class Party {
            private final int size;
            private final NormalDistribution dist;
            private final double stubb;

            private Party(int size, int opinion, int variance, double stubb) {
                this.size = size;
                this.stubb = stubb;
                dist = new NormalDistribution(opinion, variance);
            }

            private int generate(MP[] mps, int offset) {
                for(int i = offset; i < offset + size; i++) {
                    mps[i] = new MP(dist.nextIntValue(), 1.0, stubb);
                }
                return size;
            }
        }

        private Party[] parties;

        public TweedeKamerSetup(Party[] parties) {
            this.parties = parties;
        }

        @Override
        public MP[] generateMPs(int run) {
            MP[] mps = new MP[LowerChambers.NUM_MP];
            int off = 0;
            for(Party party: parties) {
                off += party.generate(mps, off);
            }

            if (off != 150) {
                throw new RuntimeException("Did not make 150 MPs");
            }

            return mps;
        }
    }

    private static final NormalDistribution fullNormalDist = new FullNormalDistribution();

    private static class FullNormalDistribution extends NormalDistribution {

        public FullNormalDistribution() {
            super(0, 500);
        }

        @Override
        public double nextValue() {
            double val;

            do {
                val = super.nextValue();
            } while (val > 1000.0);

            return super.nextValue();
        }
    }

    public static class NormalDistribution {

        private Random rand;
        private final double mean;

        private final double stdDeviation;

        public NormalDistribution(double mean, double stdDeviation) {
            this.rand = new Random();
            this.mean = mean;
            this.stdDeviation = stdDeviation;
        }

        public NormalDistribution(long seed, double mean, double stdDeviation) {
            this.rand = new Random(seed);
            this.mean = mean;
            this.stdDeviation = stdDeviation;
        }

        public double nextValue() {
            //next normal value with mean and stdDeviation
            return rand.nextGaussian() * stdDeviation + mean;
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
