package game;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * It contains the representation of the game Board which uses an HashMap of Field indexes.
 * Uses also two-dimensional HashMap of rows and columns.
 * Board Fields are instances of the class Field.
 */
public class Board {
    private final int radius;
    private final HashMap<Integer, HashMap<Integer, Field>> map = new HashMap<>();
    private final HashMap<Integer, Field> fields = new HashMap<>();

    /**
     * Constructor which takes the radius as a parameter.
     * In our game we use constructor with radius==4 because that is size of the board for Abalone game.
     * It is possible to make the Board smaller or bigger if user wants to adjust game preferences later on.
     * @param radius Based on radius it creates the board of that size.
     */
    public Board(int radius) {
        this.radius = radius;
    }

    /**
     * Gets the HashMap of the fields which are creating board.
     * @return Return HashMap of the fields which are creating board.
     */
    public HashMap<Integer, Field> getFields() {
        return this.fields;
    }



    /**
     * Gets the Field based on coordinate system from a two-dimensional map.
     * @param x Coordinate x.
     * @param y Coordinate y.
     * @return Returns the Field on the board.
     */
    public Field getField(Integer x, Integer y) {
        if (this.map.get(y) != null) {
            return this.map.get(y).get(x);
        } else {
            return null;
        }
    }

    /**
     * Gets the Field based on index system from a map.
     * @param index Index of the Field.
     * @return Returns the Field on the board.
     */
    public Field getField(int index) {
        return this.fields.get(index);
    }

    /**
     * Sets the Field based on index system.
     * @param index Sets the field on index position on the Board.
     * @param field Object of class Field which replaces another Field previously located on index position.
     */

    public void setField(int index, Field field) {
        this.fields.replace(index, field);
    }

    /**
     * Sets the Field based on coordinate system.
     * @param x Coordinate x.
     * @param y Coordinate y.
     * @param field Field which will take place of another Field previously located on the x,y position.
     */
    public void setField(Integer x, Integer y, Field field) {
        HashMap<Integer, Field> xz = this.map.get(y);
        if (xz != null) {
            xz.replace(x, field);
            this.map.replace(y, xz);
        }
    }

    /**
     * Using previously defined methods for setting Field, updates the Field.
     * @param index Takes an index and uses it to update current Field. (Coordinates not needed here)
     * @param field Takes a Field and replaces a Field previously located on index position.
     */
    public void updateField(int index, Field field) {
        this.setField(index, field);
        this.setField(field.getX(), field.getY(), field);
    }

    /**
     * Creates all the Field on the Board using stored radius.
     * Size of the board is depended on the radius instance variable.
     */
    public void createFields() {
        for (int i = 0; i <= this.radius * 2; i++) {
            HashMap<Integer, Field> js = new HashMap<>();
            for (int j = this.radius - i; j <= this.radius * 2 && i + j <= this.radius * 3; j++) {
                Field field = new Field(Math.abs(j), i);
                js.put(Math.abs(j), field);
            }
            this.map.put(i, js);
        }
        this.addIndexes();
    }

    /**
     * Combines indexes with coordinate system.
     */
    public void addIndexes() {
        int index = 0;
        for (int i = 0; i <= this.radius * 2; i++) {
            for (int j = 0; j <= this.radius * 2; j++) {
                if (this.map.get(i) != null) {
                    Field field = this.map.get(i).get(j);
                    if (field != null) {
                        field.setIndex(index);
                        this.fields.put(index, field);
                        index++;
                    }
                }
            }
        }
    }

    /**
     * Makes a Move on a board. If it is, invalid it throws exception.
     * For every Marble located on a Field it makes previous position empty.
     * For every Marble it checks if Marble is not pushed away from the board.
     * If it is not it moves to new Field.
     * @param move Instance of class Move. Responsible for a Move on a Board.
     * @throws Exception Invalid move exception if move is not valid.
     */
    public void makeMove(Move move) throws Exception {
        if (!move.isValid()) {
            throw new Exception("Invalid move!");
        }
        ArrayList<Field> result = move.getResult();
        for (Field field: move.getFrom()) {
            field.setMarble(null);
        }
        for (Field field: result) {
            if (field == null) {
                move.getPlayer().getTeam().addPoint();
            } else {
                this.updateField(field.getIndex(), field);
            }
        }
    }

    /**
     * Gets the Upper Right neighbour of the Field.
     * @param field Field which is in centre.
     * @return Field which is on upper right position from the argument field.
     */
    public Field getUpperRight(Field field) {
        int x = field.getX() + 1;
        int y = field.getY() - 1;
        return this.getField(x, y);
    }

    /**
     * Gets the Right neighbour of the Field.
     * @param field Field which is in centre.
     * @return Field which is on right position from the argument field.
     */
    public Field getRight(Field field) {
        int x = field.getX() + 1;
        int y = field.getY();
        return this.getField(x, y);
    }

    /**
     * Gets the Lower Right neighbor of the Field.
     * @param field Field which is in center.
     * @return Field which is on lower right position from the argument field.
     */
    public Field getLowerRight(Field field) {
        int x = field.getX();
        int y = field.getY() + 1;
        return this.getField(x, y);
    }

    /**
     * Gets the Lower Left neighbour of the Field.
     * @param field Field which is in centre.
     * @return Field which is on lower left position from the argument field.
     */
    public Field getLowerLeft(Field field) {
        int x = field.getX() - 1;
        int y = field.getY() + 1;
        return this.getField(x, y);
    }

    /**
     * Gets the Left neighbour of the Field.
     * @param field Field which is in centre.
     * @return Field which is on left position from the argument field.
     */
    public Field getLeft(Field field) {
        int x = field.getX() - 1;
        int y = field.getY();
        return this.getField(x, y);
    }

    /**
     * Gets the Upper Left neighbour of the Field.
     * @param field Field which is in centre.
     * @return Field which is on upper left position from the argument field.
     */
    public Field getUpperLeft(Field field) {
        int x = field.getX();
        int y = field.getY() - 1;
        return this.getField(x, y);
    }
    
    /**
     * Overrides toString method inherited from Object class.
     * @return Returns string representation of the Board class. (With index numbers)
     */
    public String toString() {
        String board = "";
        for (int i = 0; i <= this.radius * 2; i++) {
            String preLine = "";
            String line = "";
            String postLine = "";
            for (int s = i - this.radius; s > 0; s--) {
                preLine = preLine.concat("      ");
                postLine = postLine.concat("      ");
                line = line.concat("      ");
            }
            for (int j = 0; j <= this.radius * 2; j++) {
                Field field = this.map.get(i).get(j);
                if (field != null) {
                    if (field.getMarble() != null) {
                        preLine = preLine.concat("/   "
                                + "(").concat(field.getMarble().getColor().toString()).concat(")  \\  ");
                    } else {
                        preLine = preLine.concat("/        \\  ");
                    }
                    postLine = postLine.concat("\\        /  ");
                    line = line.concat(field.toString()).concat("  ");
                } else {
                    preLine = preLine.concat("      ");
                    postLine = postLine.concat("      ");
                    line = line.concat("      ");
                }
            }
            board = board.concat(preLine).concat("\n");
            board = board.concat(line).concat("\n");
            board = board.concat(postLine).concat("\n");
        }
        return board;
    }
}
