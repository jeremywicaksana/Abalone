package game;

/**
 * Represents a Field on a hexagonal board.
 * It has information about the Marble located on it.
 * It is implemented in 3 coordinate system which also uses indexes as protocol requires.
 */
public class Field {
    private final Integer x;
    private final Integer y;
    private final Integer z;
    private Integer index;
    private Marble marble = null;

    /**
     * Creates a Field with x and y coordinates. It also calculates its z coordinate.
     * @param x x coordinate
     * @param y y coordinate
     */
    public Field(int x, int y) {
        this.x = x;
        this.y = y;
        this.z = x + y - Game.BOARD_SIZE;
    }

    /**
     * Second constructor of Field. Takes already created field as a parameter.
     * @param field Takes another field as an argument.
     */
    public Field(Field field) {
        this.x = field.getX();
        this.y = field.getY();
        this.z = field.getZ();
        this.index = field.getIndex();
        this.marble = field.getMarble();
    }

    /**
     * Sets the index of the Field.
     * @param index of the assigned Field.
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Gets the index of the Field.
     * @return Index of the field.
     */
    public Integer getIndex() {
        return this.index;
    }

    /**
     * Gets the Marble located on the Field.
     * @return Marble stored on the Field.
     */
    public Marble getMarble() {
        return this.marble;
    }

    /**
     * Sets the Marble on a Field.
     * @param marble Sets a marble on a Field.
     */
    public void setMarble(Marble marble) {
        this.marble = marble;
    }

    /**
     * Gets the x coordinate of the Field.
     * @return x coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the y coordinate of the Field.
     * @return y coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Gets the z coordinate of the Field.
     * @return z coordinate.
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Overrides toString method inherited from Object class.
     * @return Returns string representation of the Field class. (With index numbers)
     */
    public String toString() {
        return "|   " + String.format("%02d", index) + "   |";
    }
}


