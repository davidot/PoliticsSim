package nl.tue.probabilty;

public class MP {

    public static final int OPINION_MAX = 1000;
    public static final int NEUTRAL_MAX = 100;

    public static final int SPEECH_MAX = 100;

    private int opinion;
    private final int speechSkill;

    public MP(int opinion, int speechSkill) {
        this.opinion = opinion;
        checkOpinionBounds();

        if (speechSkill < 0) {
            this.speechSkill = 0;
        } else if (speechSkill > SPEECH_MAX) {
            this.speechSkill = SPEECH_MAX;
        } else {
            this.speechSkill = speechSkill;
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

    public int getSpeechTotal() {
        //todo add better formula
        return getAbsoluteOpinion() * speechSkill;
    }

    public void listen(int speech) {
        //todo implements cost function
        int influence = speech;
        int absoluteOpinion = getAbsoluteOpinion();
        if (absoluteOpinion > (3 * OPINION_MAX / 4)) {
            influence /= 3;
        } else if (absoluteOpinion > OPINION_MAX / 2) {
            influence /= 2;
        }
        opinion += influence;
        checkOpinionBounds();
    }

    private int getAbsoluteOpinion() {
        return Math.abs(opinion);
    }

    public int speak() {
        //todo add better formula
        return getAbsoluteOpinion() / 100 + speechSkill * 10;
    }
}
