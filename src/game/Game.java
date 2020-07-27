package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import player.HumanPlayer;
import player.Player;
import player.Team;
import shared.ProtocolMessages;

/**
 * Represents an instance of the Game.
 * It has an information of the Board and particular players and teams.
 * Size of the Board is a constant which can be easily changed later on if we want to play a game on a bigger
 * or smaller Board.
 */
public class Game {
    static final int BOARD_SIZE = 4;
    private Board board;
    private ArrayList<Player> players;
    private ArrayList<Team> teams;
    private int turn = 0;

    /**
     * Constructor takes an player list as an argument and creates instance of the Game class.
     * @param players An ArrayList of the players which will play the game.
     */
    public Game(ArrayList<Player> players) {
        this.players = players;
    }

    /**
     * Gets the board of the Game.
     * @return Returns Board of the Game.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the players of the Game.
     * @return Returns ArrayList of the players playing the Game.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }
    
    /**
     * Prints the current board.
     */
    public void currentBoard() {
        System.out.println(this.getBoard().toString());
    }
    

    /**
     * Choose marble corresponds to the index in the coordinates.
     */
    public ArrayList<Field> chooseMarble(List<String> coordinates) {
        ArrayList<Field> moves = new ArrayList<>();
        for (String i: coordinates) {
            int val = Integer.parseInt(i);
            moves.add(getBoard().getFields().get(val));
        }
        return moves;
    }
    
    /**
     * Checks what turn is currently played.
     * @return Returns current turn.
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Increases turn count by one.
     */
    public void increaseTurnCount() {
        turn += 1;
    }

    /**
     * Checks the conditions of ending the game.
     * @return Returns boolean value if the game is over.
     */
    public boolean gameOver() {
        boolean result = false;
        for (Team team: teams) {
            if (team.getScore() == 6 || this.getTurn() == 96) {
                result = true;
            }
        }
        return result;
    }

    /**
     *Gets the name of the winner.
     */
    public String determineWinner() {
        Team winner = null;
        String result = null;
        for (Team team: teams) {
            if (team.getScore() == 6) {
                winner = team;
                result = team.toString();
                break;
            }
        }
        if (winner == null) {
            result = "none";
        }
        return result;
    }

    
    /**
     * Method used for testing the behavior of move, board and game.
     */
    public static void main(String[] args) throws Exception {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new HumanPlayer("Player One", Color.WHITE));
        players.add(new HumanPlayer("Player Two", Color.BLACK));
        //players.add(new HumanPlayer("Player Onez"));
        //players.add(new HumanPlayer("Player Twos"));
        Game game = new Game(players);
        game.setup();
        System.out.println(game.getBoard().toString());

        List<String> coords = Arrays.asList("53","46","59");
        ArrayList<Field> fields = game.chooseMarble(coords);
        Move move1 = new Move(fields, ProtocolMessages.UPLEFT, game.getBoard(), players.get(0));
        System.out.println("M1: " + move1.getResult().get(0).getIndex() + " " + move1.isValid());
        move1.make();

        System.out.println(game.getBoard().toString());

        List<String> coordss = Arrays.asList("38","46","53");
        ArrayList<Field> fieldss = game.chooseMarble(coordss);
        Move move2 = new Move(fieldss, ProtocolMessages.UPLEFT, game.getBoard(), players.get(0));
        System.out.println("M2: " + move1.getResult().get(0).getIndex() + " " + move2.isValid());
        move2.make();

        System.out.println(game.getBoard().toString());
        
        List<String> coordsss = Arrays.asList("38","46","29");
        ArrayList<Field> fieldsss = game.chooseMarble(coordsss);
        Move move3 = new Move(fieldsss, ProtocolMessages.UPLEFT, game.getBoard(), players.get(0));
        System.out.println("M3: " + move1.getResult().get(0).getIndex() + " " + move3.isValid());
        move3.make();

        System.out.println(game.getBoard().toString());
        
        List<String> coordssss = Arrays.asList("38","20","29");
        ArrayList<Field> fieldssss = game.chooseMarble(coordssss);
        Move move4 = new Move(fieldssss, ProtocolMessages.UPLEFT, game.getBoard(), players.get(0));
        System.out.println("M4: " + move1.getResult().get(0).getIndex() + " " + move4.isValid());
        move4.make();

        System.out.println(game.getBoard().toString());
        
        //player 1 push player 2
        List<String> coordsssss = Arrays.asList("12","20","29","5");
        ArrayList<Field> fieldsssss = game.chooseMarble(coordsssss);
        Move move5 = new Move(fieldsssss, ProtocolMessages.UPLEFT, game.getBoard(), players.get(0));
        System.out.println("M5: " + move1.getResult().get(0).getIndex() + " " + move5.isValid());
        move5.make();

        System.out.println(game.getBoard().toString());

        //player 2 push player 1
        List<String> coordssssss = Arrays.asList("5","6","7","8");
        ArrayList<Field> fieldssssss = game.chooseMarble(coordssssss);

        Move move6 = new Move(fieldssssss, ProtocolMessages.LEFT, game.getBoard(), players.get(1));
        System.out.println("M6: " + move1.getResult().get(0).getIndex() + " " + move6.isValid());
        move6.make();

        System.out.println(game.getBoard().toString());
    }

    /**
     * Sets up the game.
     * First it sets up the Board.
     * Later it assigns players to the teams.
     * Finally in sets up the Marbles on the Board.
     * @throws Exception Invalid player count.
     */
    public void setup() throws Exception {
        //Sets up the board
        Board board = new Board(BOARD_SIZE);
        board.createFields();
        this.board = board;
        //Sets up the teams
        this.teams = new ArrayList<>();
        switch (players.size()) {
            case 2: {
                Team team1 = new Team(this.getPlayers().get(0).getName());
                players.get(0).setTeam(team1);
                teams.add(team1);
                Team team2 = new Team(this.getPlayers().get(1).getName());
                players.get(1).setTeam(team2);
                teams.add(team2);
                break;
            }
            case 3: {
                Team team1 = new Team(this.getPlayers().get(0).getName());
                players.get(0).setTeam(team1);
                teams.add(team1);
                Team team2 = new Team(this.getPlayers().get(1).getName());
                players.get(1).setTeam(team2);
                teams.add(team2);
                Team team3 = new Team(this.getPlayers().get(2).getName());
                players.get(2).setTeam(team3);
                teams.add(team3);
                break;
            }
            case 4: {
                Team team1 = new Team(this.getPlayers().get(0).getName() 
                    +  " and " + this.getPlayers().get(2).getName());
                players.get(0).setTeam(team1);
                players.get(2).setTeam(team1);
                teams.add(team1);
                Team team2 = new Team(this.getPlayers().get(1).getName() 
                    +  " and " + this.getPlayers().get(3).getName());
                players.get(1).setTeam(team2);
                players.get(3).setTeam(team2);
                teams.add(team2);
                break;
            }
            default: {
                throw new Exception("Invalid player count!");
            }
        }
        //Sets up marbles on the board
        switch (players.size()) {
            case 2: {
                for (int pos: Position.TWO_1.indexes) {
                    Marble marble = new Marble(Color.WHITE, players.get(0));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                for (int pos: Position.TWO_2.indexes) {
                    Marble marble = new Marble(Color.BLACK, players.get(1));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                break;
            }
            case 3: {
                for (int pos: Position.THREE_1.indexes) {
                    Marble marble = new Marble(Color.WHITE, players.get(0));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                for (int pos: Position.THREE_2.indexes) {
                    Marble marble = new Marble(Color.BLACK, players.get(1));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                for (int pos: Position.THREE_3.indexes) {
                    Marble marble = new Marble(Color.BLUE, players.get(2));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                break;
            }
            case 4: {
                for (int pos: Position.FOUR_1.indexes) {
                    Marble marble = new Marble(Color.WHITE, players.get(0));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                for (int pos: Position.FOUR_2.indexes) {
                    Marble marble = new Marble(Color.BLACK, players.get(1));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                for (int pos: Position.FOUR_3.indexes) {
                    Marble marble = new Marble(Color.BLUE, players.get(2));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                for (int pos: Position.FOUR_4.indexes) {
                    Marble marble = new Marble(Color.RED, players.get(3));
                    this.board.getFields().get(pos).setMarble(marble);
                }
                break;
            }
            default: {
                System.out.println(players.size());
                throw new Exception("Invalid player count!");
            }
        }
    }
}