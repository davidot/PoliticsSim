package nl.tue.probabilty;

public enum VoteOptions {
    PRO,
    AGAINST,
    NEUTRAL;

    public VoteOptions otherSide() {
        switch (this) {
            case PRO:
                return AGAINST;
            case AGAINST:
                return PRO;
        }
        return NEUTRAL;
    }

    public int opinionModifier() {
        switch (this) {
            case PRO:
                return 1;
            case AGAINST:
                return -1;
        }
        //neutral should not have any influence since they should not be speaking
        return 0;
    }

    public String getName() {
        StringBuilder name = new StringBuilder(name());
        while (name.length() < 7) {
            name.append(" ");
        }
        return name.toString();
    }
}
