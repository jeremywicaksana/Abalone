package game;

/**
 * Enumeration which stores all the starting indexes of the Marbles depending how many players want to play.
 */
public enum Position {
    TWO_1(new Integer[]{45, 46, 47, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60}),
    TWO_2(new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 14, 15}),

    THREE_1(new Integer[]{0, 1, 5, 6, 11, 12, 18, 19, 26, 27, 35}),
    THREE_2(new Integer[]{3, 4, 9, 10, 16, 17, 24, 25, 33, 34, 42}),
    THREE_3(new Integer[]{50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60}),

    FOUR_1(new Integer[]{26, 27, 28, 35, 36, 37, 43, 44, 50}),
    FOUR_2(new Integer[]{10, 17, 25, 34, 16, 24, 33, 23, 32}),
    FOUR_3(new Integer[]{57, 58, 59, 52, 53, 54, 46, 47}),
    FOUR_4(new Integer[]{0, 1, 2, 3, 6, 7, 8, 13, 14});

    public final Integer[] indexes;

    Position(Integer[] indexes) {
        this.indexes = indexes;
    }
}

