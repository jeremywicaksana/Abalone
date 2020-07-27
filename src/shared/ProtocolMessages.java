package shared;

import game.Color;

import java.util.Arrays;
import java.util.List;

public class ProtocolMessages {

    /*
     * End of command
     */
    public static final String EOC = "*";

    /*
     * Separator
     */
    public static final String DELIMITER = ";";
    /* commands
     *
     */
    public static final String HELLO = "h";
    public static final String JOIN = "j";
    public static final String MOVE = "m";
    public static final String EXIT = "x";
    public static final String FINISH = "f";
    public static final String WIN = "w";
    public static final String DRAW = "d";
    public static final String QUERY = "q";
    public static final String ERROR = "e";
    public static final String START = "s";

    /*
     * Current available challenge 0 means not applied while 1 is applied
     */

    public static final String CHATTING = "0";
    public static final String CHALLANGE = "0";
    public static final String LEADERBOARD = "0";

    /*
     * Available lobby
     */
    public static final List<String> SIZEAVAILABLE = Arrays.asList("2", "3", "4");
    /*
     * AVAILABLE FOR CHAT/CHALLANGE/LEADERBOARD
     */
    public static final List<String> AVAILABLE = Arrays.asList("0", "1"); //to check whether responds contain 0/1
    
    /*
     * DIRECTIONS
     */
    public static final List<String> DIRECTIONAVAILABLE = Arrays.asList("R", "L", "UR", "LR", "UL", "LL");
    public static final String RIGHT = "R";
    public static final String LEFT = "L";
    public static final String UPRIGHT = "UR";
    public static final String LOWRIGHT = "LR";
    public static final String UPLEFT = "UL";
    public static final String LOWLEFT = "LL";
    
    
    /*
     * Colors
     */
    
    public static final List<Color> COLORS = Arrays.asList(Color.WHITE, Color.BLACK, Color.BLUE, Color.RED);
}
