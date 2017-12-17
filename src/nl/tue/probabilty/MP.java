package nl.tue.probabilty;

public class MP {

    public static final int OPINION_MAX = 1000;
    public static final int NEUTRAL_MAX = 100;

    public static final int SPEECH_MAX = 100;

    //todo choose value
    private static final int SPEECH_CONSTANT = 9;

    private int opinion;
    private final int speechSkill;
    private final double stubbornness;

    public MP(int opinion, int speechSkill, double stubbornness) {
        this.opinion = opinion;
        checkOpinionBounds();

        if (speechSkill < 0) {
            this.speechSkill = 0;
        } else if (speechSkill > SPEECH_MAX) {
            this.speechSkill = SPEECH_MAX;
        } else {
            this.speechSkill = speechSkill;
        }

        if (stubbornness < 0.0) {
            this.stubbornness = 0.0;
        } else if (stubbornness > 1.0) {
            this.stubbornness = 1.0;
        } else {
            this.stubbornness = stubbornness;
        }

    }

    private void checkOpinionBounds() {
        if (opinion > OPINION_MAX) {
            this.opinion = OPINION_MAX;
        } else if (opinion < -OPINION_MAX) {
            this.opinion = -OPINION_MAX;
        }
    }

    public VoteOptions vote() {
        if (getAbsoluteOpinion() <= NEUTRAL_MAX) {
            return VoteOptions.NEUTRAL;
        }
        if (opinion > 0) {
            return VoteOptions.PRO;
        }
        return VoteOptions.AGAINST;
    }

    public void listen(int speech) {
        int absoluteOpinion = getAbsoluteOpinion();
        //todo implements cost function
        int influence = (int) (2 * stubbornness * speech * (OPINION_MAX - absoluteOpinion) /
                        (2*absoluteOpinion + OPINION_MAX));
        opinion += influence;
        checkOpinionBounds();
    }

    private int getAbsoluteOpinion() {
        return Math.abs(opinion);
    }

    public int speak() {
        if (getAbsoluteOpinion() > OPINION_MAX / 2) {
            return speechSkill * SPEECH_CONSTANT;
        } else {
            return (int)(calcSpeedMod() * speechSkill * SPEECH_CONSTANT);
        }
    }

    private double calcSpeedMod() {
        //todo implement curve
        return 0.5;
    }
}
