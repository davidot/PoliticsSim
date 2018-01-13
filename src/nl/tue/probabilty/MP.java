package nl.tue.probabilty;

public class MP {

    public static final int OPINION_MAX = 1000;
    public static final double OPINION_DIV = OPINION_MAX / 10.0;
    public static final int NEUTRAL_MAX = 100;

    private int opinion;
    private final double speechSkill;
    private final double stubbornness;

    public MP(int opinion, double speechSkill, double stubbornness) {
        this.opinion = opinion;
        checkOpinionBounds();

        if (speechSkill < 0.0) {
            this.speechSkill = 0.0;
        } else if (speechSkill > 1.0) {
            this.speechSkill = 1.0;
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
        int influence = (int) (stubbornness * 2  * speech * (OPINION_MAX - absoluteOpinion) /
                        (2*absoluteOpinion + OPINION_MAX));
        opinion += influence;
        checkOpinionBounds();
    }

    private int getAbsoluteOpinion() {
        return Math.abs(opinion);
    }

    public int speak() {
        if (getAbsoluteOpinion() > OPINION_MAX / 2) {
            return (int) (getAbsoluteOpinion() / 5 * speechSkill);
        } else {
            return getAbsoluteOpinion() / 5 * (int)(calcSpeechMod() * speechSkill);
        }
    }

    private double calcSpeechMod() {
        double v = (getAbsoluteOpinion()) / 750.0;
        return 27.0/4.0 * (v * v * ( 1- v));
    }
}
