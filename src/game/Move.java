package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import player.Player;
import shared.ProtocolMessages;

/**
 * Responsible for creating a Move.
 * It knows the Board where the move should be performed.
 * It performs the move from starting location to the end location by particular Player.
 */
public class Move {
    private final Board board;
    private final ArrayList<Field> from;
    private final String to;
    private final Player player;

    /**
     * Constructor of Move class.
     * It is instantiated by providing board, player, starting point(from) and ending position(to)
     */
    public Move(ArrayList<Field> from, String to, Board board, Player player) {
        this.from = from;
        this.to = to;
        this.board = board;
        this.player = player;
    }

    /**
     * Gets the targets of the particular Move.
     * @return Returns list of targets.
     * @ensures result != null;
     * @pure
     */
    public ArrayList<Field> getTargets() {
        ArrayList<Field> targets = new ArrayList<>();
        for (Field field : from) {
            Field target;
            switch (to) {
                case "L":
                    target = new Field(board.getLeft(field));
                    targets.add(target);
                    break;
                case "R":
                    target = new Field(board.getRight(field));
                    targets.add(target);
                    break;
                case "UL":
                    target = new Field(board.getUpperLeft(field));
                    targets.add(target);
                    break;
                case "UR":
                    target = new Field(board.getUpperRight(field));
                    targets.add(target);
                    break;
                case "LL":
                    target = new Field(board.getLowerLeft(field));
                    targets.add(target);
                    break;
                case "LR":
                    target = new Field(board.getLowerRight(field));
                    targets.add(target);
                    break;
            }
        }
        return targets;
    }

    /**
     * Gets the result where Marble moved.
     * @return The list of Fields where Marble moved.
     * @pure
     * @ensures result != null;
     */
    public ArrayList<Field> getResult() {
        ArrayList<Field> result = new ArrayList<>();
        for (Field field : from) {
            Field target;
            switch (to) {
                case "L":
                    if (board.getLeft(field) != null) {
                        target = new Field(board.getLeft(field));
                        target.setMarble(field.getMarble());
                        result.add(target);
                    } else {
                        result.add(null);
                    }
                    break;
                case "R":
                    if (board.getRight(field) != null) {
                        target = new Field(board.getRight(field));
                        target.setMarble(field.getMarble());
                        result.add(target);
                    } else {
                        result.add(null);
                    }
                    break;
                case "UL":
                    if (board.getUpperLeft(field) != null) {
                        target = new Field(board.getUpperLeft(field));
                        target.setMarble(field.getMarble());
                        result.add(target);
                    } else {
                        result.add(null);
                    }
                    break;
                case "UR":
                    if (board.getUpperRight(field) != null) {
                        target = new Field(board.getUpperRight(field));
                        target.setMarble(field.getMarble());
                        result.add(target);
                    } else {
                        result.add(null);
                    }
                    break;
                case "LL":
                    if (board.getLowerLeft(field) != null) {
                        target = new Field(board.getLowerLeft(field));
                        target.setMarble(field.getMarble());
                        result.add(target);
                    } else {
                        result.add(null);
                    }
                    break;
                case "LR":
                    if (board.getLowerRight(field) != null) {
                        target = new Field(board.getLowerRight(field));
                        target.setMarble(field.getMarble());
                        result.add(target);
                    } else {
                        result.add(null);
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * Checks if push move is valid.
     * @param marbles List of up to 5 marbles to move, in order
     * @return Returns true if push is valid.
     * @pure
     * @requires marbles != null
     */
    public boolean checkPush(List<Marble> marbles) {
        System.out.println(marbles.get(0).getPlayer().getName());
        if (marbles.get(0) != null && marbles.get(0).getTeam() != player.getTeam()) {
            if (marbles.get(marbles.size() - 2).getTeam() != player.getTeam()) {
                System.err.println("Cannot push with opponent marbles!");
                return false;
            }
            if (marbles.get(marbles.size() - 1).getPlayer() != player) {
                System.err.println("Cannot push with another player's marble!");
                return false;
            }
            switch (marbles.size()) {
                case 3:
                case 4:
                    if (marbles.get(1).getTeam() != player.getTeam()) {
                        System.err.println("Not enough marbles to push!");
                        return false;
                    }
                    break;
                case 5:
                    if (marbles.get(2).getTeam() != player.getTeam()) {
                        System.err.println("Not enough marbles to push!");
                        return false;
                    }
                    if (marbles.get(3).getTeam() != player.getTeam()) {
                        System.err.println("Cannot push with opponent marbles!");
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * Returns true if the move is a pushing move.
     * @param marbles Takes an ArrayList of Marbles as an argument.
     * @return Returns true if the move is a pushing move.
     * @requires marbles != null
     * @pure
     */
    private boolean isPush(ArrayList<Marble> marbles) {
        return marbles.get(0).getTeam() != player.getTeam();
    }

    /**
     * Checks if push Move is valid.
     * @param fields HashMap of fields which take part in Pushing move.
     * @param coords Coordinates which take part in the current move.
     * @return Returns false if push is invalid.
     * @requires fields != null
     * @requires coords != null
     * @requires d1 != null
     * @requires d2 != null
     * @pure
     */
    private boolean isValidPush(HashMap<Integer, Field> fields, ArrayList<Integer> coords, String d1, String d2) {
        if (to == d1) {
            ArrayList<Marble> marbles = new ArrayList<Marble>();
            for (Integer coordinate : coords) {
                marbles.add(fields.get(coordinate).getMarble());
            }
            return isPush(marbles) && checkPush(marbles);

        } else if (to == d2) {
            ArrayList<Marble> marbles = new ArrayList<Marble>();
            for (int i = coords.size() - 1; i >= 0; i--) {
                marbles.add(fields.get(coords.get(i)).getMarble());
            }
            return isPush(marbles) && checkPush(marbles);
        }
        return false;
    }

    /**
     * Checks if performed Move is valid.
     * @return Returns true if it is valid.
     * @pure
     */
    public boolean isValid() {
        //Checks if column is of valid size to move (1 - 5 fields)
        if (this.from.size() > 5 || this.from.size() < 1) {
            return false;
        }
        //Checks if all fields in a column have Marbles
        for (Field field : this.from) {
            if (field.getMarble() == null) {
                return false;
            }
        }
        //Checks if all of the fields are on one line
        //In order for the fields to be on the same line, one of the coordinates has to be the same for all fields.
        //Prepares the lists for coordinates for the next check as well
        if (this.from.size() > 1) {
            int x = this.from.get(0).getX();
            int y = this.from.get(0).getY();
            int z = this.from.get(0).getZ();
            boolean sameX = true;
            boolean sameY = true;
            boolean sameZ = true;
            HashMap<Integer, Field> xs = new HashMap<>();
            HashMap<Integer, Field> ys = new HashMap<>();
            HashMap<Integer, Field> zs = new HashMap<>();
            ArrayList<Integer> xCoords = new ArrayList<>();
            ArrayList<Integer> yCoords = new ArrayList<>();
            ArrayList<Integer> zCoords = new ArrayList<>();
            for (Field field : this.from) {
                if (field.getX() != x) {
                    sameX = false;
                }
                if (field.getY() != y) {
                    sameY = false;
                }
                if (field.getZ() != z) {
                    sameZ = false;
                }
                xs.put(field.getX(), field);
                ys.put(field.getY(), field);
                zs.put(field.getZ(), field);
                xCoords.add(field.getX());
                yCoords.add(field.getY());
                zCoords.add(field.getZ());
            }
            if (!sameX && !sameY && !sameZ) {
                System.err.println("Fields are not on one line!");
                return false;
            }
            Collections.sort(xCoords);
            Collections.sort(yCoords);
            Collections.sort(zCoords);
            //Checks if all Fields are next to each other
            //If all Fields are next to each other, is it a push
            //If its a push, checks if it is valid
            if (sameX) {
                if (xCoords.get(xCoords.size() - 1) - xCoords.get(0) >= xCoords.size()) {
                    System.err.println("Fields are not next to each other!");
                    return false;
                }
                if (from.size() > 2) {
                    if (isValidPush(zs, zCoords, ProtocolMessages.UPLEFT, ProtocolMessages.LOWRIGHT)) {
                        return true;
                    }
                }
            } else if (sameY) {
                if (yCoords.get(yCoords.size() - 1) - yCoords.get(0) >= yCoords.size()) {
                    System.err.println("Fields are not next to each other!");
                    return false;
                }
                if (from.size() > 2) {
                    if (isValidPush(xs, xCoords, ProtocolMessages.LEFT, ProtocolMessages.RIGHT)) {
                        return true;
                    }
                }
            } else {
                if (zCoords.get(zCoords.size() - 1) - zCoords.get(0) >= zCoords.size()) {
                    System.err.println("Fields are not next to each other!");
                    return false;
                }
                if (from.size() > 2) {
                    if (isValidPush(ys, yCoords, ProtocolMessages.LOWLEFT, ProtocolMessages.UPRIGHT)) {
                        return true;
                    }
                }
            }
        }

        if (from.size() > 3) {
            return false;
        }
        
        //If it is not the case, checks if it is a valid move
        //Checks if all marbles belong to the player's team
        for (Field field: from) {
            if (field.getMarble().getTeam() != player.getTeam()) {
                return false;
            }
        }
        //Checks if the target Field exists and if it is it empty
        ArrayList<Field> targets = this.getTargets();
        for (Field field : targets) {
            if (field == null) {
                System.err.println("Target field does not exist!");
                return false;
            }
            if (field.getMarble() != null && !from.contains(board.getField(field.getIndex()))) {
                System.err.println("Target field is already occupied!");
                return false;
            }
        }

        return true;
    }

    /**
     * Gets starting position of the moving column.
     * @return Returns starting position of the moving column.
     * @pure
     */
    public ArrayList<Field> getFrom() {
        return from;
    }

    /**
     * Performing a move on the board.
     */
    public void make() throws Exception {
        this.board.makeMove(this);
    }

    /**
     * Gets the player performing a Move.
     * @return Returns player performing a Move.
     * @pure
     */
    public Player getPlayer() {
        return this.player;
    }

}
