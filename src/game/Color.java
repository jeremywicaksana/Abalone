package game;

/**
 * Enumeration representing color of the Marbles. BLACK and WHITE are used for the 2-player game.
 * RED is additionally used in 3 player game.
 * BLUE is additionally used in 4 player game.
 */
public enum Color {
    WHITE("1"),
    BLACK("2"),
    BLUE("3"),
    RED("4");

    private final String character;

    Color(String character) {
        this.character = character;
    }

    /**
     * Overrides toString method inherited from Object class.
     * @return Returns string representation of the Color enum.
     */
    public String toString() {
        return this.character;
    }
}
