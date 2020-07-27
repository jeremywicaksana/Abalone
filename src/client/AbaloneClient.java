package client;

import game.Game;
import game.Move;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.InetAddress;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

import player.HumanPlayer;
import player.Player;

import shared.ProtocolException;
import shared.ProtocolMessages;
import shared.ServerUnavailableException;

public class AbaloneClient {
    private Socket serverSock;
    private BufferedReader in;
    private BufferedWriter out;
    private AbaloneClientTui tui;
    private String yourName;

    public AbaloneClient() {
        tui = new AbaloneClientTui(this);
    }

    /**
     * create connection then handshake if not fail it will create a tui.
     * @throws ProtocolException Throws ProtocolException
     * @throws ServerunAvailableException Throws ServerunAvailableException
     */
    public void start() throws ServerUnavailableException, ProtocolException {
        this.createConnection();
        this.helloHandshake();
        tui.start();
    }
    /**
     * send "b" or any other string to the server if the command in tui is wrong.
     * @throws IOException Throws IOException
     */
    
    public void clear() throws IOException {
        out.write("b");
        out.newLine();
        out.flush();
    }

    /**
     * create a connection with server.
     */
    public void createConnection() {
        this.clearConnection();

        while (serverSock == null) {
            String host = tui.getString("which host to connect?");
            int port = tui.getInt("which port to connect?");

            try {
                InetAddress addr = InetAddress.getByName(host);
                System.out.println("Attempting to connect to " + addr + ":"
                        + port + "...");
                serverSock = new Socket(addr, port);
                in = new BufferedReader(new InputStreamReader(
                        serverSock.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(
                        serverSock.getOutputStream()));
            } catch (IOException e) {
                System.out.println("ERROR: could not create a socket on "
                        + host + " and port " + port + ".");

                Boolean res = tui.getBoolean("want to connect again?(yes/no))");
                if (res) {
                    createConnection();
                } else {
                    System.out.println("connection is not created");
                }
            }
        }
    }
    /**
     * to create a new connection, the old connection must be clean.
     */
    
    public void clearConnection() {
        serverSock = null;
        in = null;
        out = null;
    }

    /**
     * close the current connection with server.
     */
    public void closeConnection() {
        System.out.println("Closing the connection...");
        try {
            in.close();
            out.close();
            serverSock.close();
            System.out.println("disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * send message to the server.
     * @param msg the protocol messages to be send to server
     * @requires msg != null
     * @throws ServerUanvailableException Throws ServerUanvailableException
     */
    public void sendMessage(String msg) throws ServerUnavailableException {
        if (out != null) {
            try {
                out.write(msg);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new ServerUnavailableException("Could not write "
                        + "to server.");
            }
        } else {
            throw new ServerUnavailableException("Could not write "
                    + "to server.");
        }
    }

    /**
     * read the command from server then return it.
     * @throws ServerUnavailableException Throws ServerUnavailableException
     */
    public String readLineFromServer() throws ServerUnavailableException {
        if (in != null) {
            try {
                String answer = in.readLine();
                if (answer == null) {
                    throw new ServerUnavailableException("Could not read from server");
                }
                return answer;
            } catch (IOException e) {
                throw new ServerUnavailableException("Could not read from server");
            }
        } 
        throw new ServerUnavailableException("Could not read from server");
    }
    
    /**
     * set the name of the this client get from server.
     * @param name the name of the client
     * @requires name != null
     * 
     */
    public void setYourName(String name) {
        this.yourName = name;
    }
    
    /**
     * get the name of current client.
     * @pure
     * @return yourName
     */
    public String yourName() {
        return yourName;
    }
    //=================================================Client method============================
   
    /**
     * send hello to the server with given name and current available extensions 0 = not available and 1 = available.
     * @throws ServerUnavailableException Throws ServerUnavailableException
     * @throws ProtocolException Throws ProtocolException
     */
    public void helloHandshake() throws ServerUnavailableException, ProtocolException {
        String playerName = tui.getString("What is your name?");
        String hello = ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + ProtocolMessages.CHATTING 
            + ProtocolMessages.DELIMITER + ProtocolMessages.CHALLANGE + ProtocolMessages.DELIMITER
                + ProtocolMessages.LEADERBOARD + ProtocolMessages.DELIMITER + playerName 
                + ProtocolMessages.DELIMITER + ProtocolMessages.EOC;

        //send to server
        sendMessage(hello);
        String respond;
        respond = readLineFromServer(); //answer from server

        //System.out.println(respond);
        String[] responds = respond.split(ProtocolMessages.DELIMITER);

        if (responds[0].equals(ProtocolMessages.HELLO) && responds[4] != null 
            && responds[5].equals(ProtocolMessages.EOC)) {
            if (ProtocolMessages.AVAILABLE.contains(responds[1]) && ProtocolMessages.AVAILABLE.contains(responds[2]) 
                && ProtocolMessages.AVAILABLE.contains(responds[3])) {
                System.out.println("your name is " + responds[4]);
                this.setYourName(responds[4]);
                if (responds[1].equals("0")) {
                    System.out.println("chatting is not available");

                    if (responds[2].equals("0")) {
                        System.out.println("challange is not available");

                        if (responds[3].equals("0")) {
                            System.out.println("leaderboard is not available");
                        } else {
                            System.out.println("leaderboard is available");
                        }
                    } else {
                        System.out.println("challange is available");
                    }
                } else {
                    System.out.println("chatting is available");
                }
            } else {
                throw new ProtocolException("the challange/leaderboard/chat is not 0/1");
            }
        } else {
            throw new ProtocolException("name is null/first message is not h/not end with * ");
        }
    }

    /**
     * sends which lobby do you want 2/3/4 then wait until the game is started.
     * @param qsize indication for which lobby do you want 2/3/4 players
     * @requires qsize >= 2 && qsize <= 4
     * @throws ProtocolException Throws ProtocolException
     * @throws ServerUnavailableException Throws ServerUnavailableException
     */
    public void joinQueue(String qsize) throws ProtocolException, ServerUnavailableException {

        if (ProtocolMessages.SIZEAVAILABLE.contains(qsize)) {
            String join = ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + qsize 
                + ProtocolMessages.DELIMITER + ProtocolMessages.EOC;
            this.sendMessage(join);

            String respond = null;
            respond = this.readLineFromServer();

            String[] responds = respond.split(ProtocolMessages.DELIMITER);
            if (responds[0].equals(ProtocolMessages.JOIN) && ProtocolMessages.SIZEAVAILABLE.contains(responds[1])) {
                if (responds[1].equals(qsize)) {
                    System.out.println("you are now in lobby " + qsize + " currently there are " 
                        + responds[2] + " people");
                    boolean a = tui.getBoolean("are you using ai?(yes/no)");
                    if (a) {
                        this.startGame(a);
                    } else {
                        this.startGame(a);
                    }
                } else {
                    throw new ProtocolException("should be in room " + qsize);
                }
            } else {
                throw new ProtocolException("qsize from server is not 2/3/4 or message is not j"); //should not happen
            }
        } else {
            //throw new ProtocolException("qsize must be 2/3/4");
            System.out.println("queue size must be 2/3/4!");
        }

    }


    /**
     * if recieve start from server it this will run the game if.
     * @param ai true that means playing with ai and ai false means playing as a humanplayer
     * @requires ai != null
     * @throws ServerUnavailableException Throws ServerUnavailableException
     * @throws ProtocolException Throws ProtocolException
     */
    public void startGame(boolean ai) throws ServerUnavailableException, ProtocolException {
        String respond = null;
        String[] responds = null;

        while (true) {
            respond = this.readLineFromServer();
            responds = respond.split(ProtocolMessages.DELIMITER);
            if (responds[0].equals("s")) {
                break;
            }
        }
        ArrayList<Player> players = new ArrayList<Player>();

        if (responds[0].equals(ProtocolMessages.START)) {
            System.out.println("Players in this game: \n");
            int j = 0;
            for (int i = 1; i < responds.length - 1; i++) {
                System.out.println(i + "." + responds[i]);
                Player h = new HumanPlayer(responds[i],ProtocolMessages.COLORS.get(j));// add the names into humanPlayer
                players.add(h);// consist of players
                j++;
            }
            if (ai) {
                computerPlayer(players, 1000); // delay for 1 sec
            } else {
                humanPlayer(players);
            }

        } else {
            throw new ProtocolException("Server should sent start");
        }
    }
    
    /**
     * start as human player.
     * @param players the current players in this board
     * @requires playes != null
     */
    public void humanPlayer(ArrayList<Player> players) {
        Game g = new Game(players);
        try {
            g.setup();
            g.currentBoard();
            while (!g.gameOver()) {
                g.increaseTurnCount();
                for (Player current: players) {
                    //if currently it is your turn
                    if (this.yourName().equals(current.getName())) {
                        System.out.println("Turn: " + g.getTurn());
                        System.out.println(current.getName() + " your marble is " + current.getColor());
                        System.out.println("your turn! start your move with letter m and give coordinates with spaces");
                        while (true) {
                            System.out.println("these are the coordinates: ");
                            System.out.println("ur: up right, ul: up left, ll: lower left, lr: "
                                 + "lower right, r: right, l: left");
                            String moves = tui.getString("which coordinates to be moved along with "
                                       + "direction(m <direction> <coord> <coord> .. maximum 5 coords)");
                            try {
                                this.sendMove(moves);
                                ArrayList<String> res = this.validateMove();
                                String direction = res.get(0); //get direction
                                res.remove(0); //remove direction
                                System.out.println(res);
                                System.out.println(current.getName());
                                Move move = new Move(g.chooseMarble(res), direction, g.getBoard(), current);
                                move.make();
                                g.currentBoard();
                                break;
                            } catch (ProtocolException e) {
                                System.out.println("wrong command");
                            } catch (NullPointerException e) {
                                System.out.println("there is no marble");
                            }
                        }
                        // give move IF IT IS NOT YOUR TURN YET
                    } else {
                        System.out.println("Turn: " + g.getTurn());
                        System.out.println("Currently is " + current.getName() + " turn");
                        ArrayList<String> res = this.otherPlayerMove();
                        String direction = res.get(0); //get the direction
                        res.remove(0); //remove direction
                        Move move = new Move(g.chooseMarble(res), direction, g.getBoard(), current);
                        move.make();
                        g.currentBoard();
                        // get move to be run
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("fail");
        }
        System.out.println("the winner is " + g.determineWinner()); 
    }
    
    /**
     * run the player as an ai, this ai only reads the first coordinate that can move. (only one coordinate)
     * @param players get the board of the players
     * @requires players != null
     * @requires delay >= 0
     */
    public void computerPlayer(ArrayList<Player> players, int delay) {
        Game g = new Game(players);
        try {
            Thread.sleep(1000);
            g.setup();
            g.currentBoard();
            while (!g.gameOver()) {
                g.increaseTurnCount();
                for (Player current: players) {
                    //if currently it is your turn
                    if (this.yourName().equals(current.getName())) {
                        System.out.println("Turn: " + g.getTurn());
                        System.out.println(current.getName() + " your marble is " + current.getColor());
                        System.out.println("your turn! start your move with letter m and give coordinates with spaces");
                        boolean a = true;
                        while (a) {
                            String moves = "m";
                            int i = 0;
                            while (i < 60) {
                                for (int j = 0; j < ProtocolMessages.DIRECTIONAVAILABLE.size(); j++) {
                                    //System.out.println(direction);
                                    boolean next = false;
                                    List<String> coord = new ArrayList<String>();
                                    coord.add(String.valueOf(i));
                                    Move move = new Move(g.chooseMarble(coord), 
                                           ProtocolMessages.DIRECTIONAVAILABLE.get(j), g.getBoard(), current);
                                    try {
                                        next = move.isValid();	
                                    } catch (Exception e){
                					}
                					if (next) {
                						moves = moves + " " 
                					        + ProtocolMessages.DIRECTIONAVAILABLE.get(j) + " " + i;
                						i = 60;
                						j = ProtocolMessages.DIRECTIONAVAILABLE.size();
                					}
        						}
        						i++;
        					}
            				try {
            					this.sendMove(moves);
            					ArrayList<String> res = this.validateMove();
            					String direction = res.get(0); //get direction
            					res.remove(0); //remove direction
            					System.out.println(res);
            					System.out.println(current.getName());
            					Move move = new Move(g.chooseMarble(res), direction, 
            							g.getBoard(), current);
                				move.make();
            					g.currentBoard();
            					a = false;
            				} catch (ProtocolException e) {
            					System.out.println("wrong command");
            				} catch (NullPointerException e) {
            					System.out.println("there is no marble");
            				}
        				}

        			// give move IF IT IS NOT YOUR TURN YET
        			} else {
        				System.out.println("Turn: " + g.getTurn());
            			System.out.println("Currently is " + current.getName() + " turn");
            			ArrayList<String> res = this.otherPlayerMove();
    					String direction = res.get(0); //get the direction
    					res.remove(0); //remove direction
    					Move move = new Move(g.chooseMarble(res), direction, g.getBoard(), current);
    					move.make();
    					g.currentBoard();
         // get move to be run
        			}
        		}
        	}
        	
		} catch (Exception e) {
			System.out.println("fail");
		}
        System.out.println("the winner is " + g.determineWinner()); 
    	
    }
    //handle sending move m name <direction> <coord> <coord>
    /**
     * send move to the server.
     * @Param sentence the protocol sent to server an will be checked
     * @requires sentence != null
     * @throws ProtocolException Throws ProtocolException
     * @throws IOException Throws IOException
     * @throws ServerUnavailableException throws ServerUnavailableException
     */
    
    public void sendMove(String sentence) throws ProtocolException, IOException, ServerUnavailableException {
    	String[] split = sentence.split(" ");
    	if (split[0].equals(ProtocolMessages.MOVE) 
    			&& ProtocolMessages.DIRECTIONAVAILABLE.contains(split[1].toUpperCase()) && split.length <= 7) {
        	String res = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + this.yourName() 
        	    + ProtocolMessages.DELIMITER + split[1].toUpperCase() + ProtocolMessages.DELIMITER;
        	for (int i = 2; i < split.length; i++) {
        		res = res + split[i] + ProtocolMessages.DELIMITER;
        	}
        	res = res + ProtocolMessages.EOC;
        	this.sendMessage(res);
    	} else {
    		throw new ProtocolException("Protocol is not correct");
    	}  	
    }
    // check whether the message send by server is your moves or not
    /**
     *  expect a response back to move the marbles on the board.
     *  @return moveDirection
     *  @throws ProtocolException Throws ProtocolException
     *  @throws ServerUnavailableException Throws ServerUnavailableException
     */
    
    public ArrayList<String> validateMove() throws ProtocolException, ServerUnavailableException {
    	ArrayList<String> moveDirection = new ArrayList<String>();//create a new array to put direction, and coords
    	String res = this.readLineFromServer();
    	System.out.println(res);
    	String[] split = res.split(ProtocolMessages.DELIMITER);
    	// maximum response = m;NEXTPLAYER;YOU;DIRECTION;COORD;COORD;COORD;COORD;COORD;* SO LENGTH IS 10
    	if (split[0].equals(ProtocolMessages.MOVE) && split[2].equals(this.yourName()) 
    			&& ProtocolMessages.DIRECTIONAVAILABLE.contains(split[3]) && split.length <= 10) {	
    		for (int i = 3; i < split.length - 1; i++) {
    			moveDirection.add(split[i]);
    		}
    	} else {
    		throw new ProtocolException("Wrong protocol");
    	}
    	return moveDirection;
    }
    
    //needs to get m;NEXTPLAYERAFTERPLAYERBEFORE;PLAYERBEFORE;DIRECTION;COORD;COORD;...;* WITH MAX LENGTH 10
    /**
     * if currently not the client turn it will return the other player that will be used to move.
     * @return other player moves
     * @throws ServerUnavailableException Throws ServerUnavailableException
     */
    public ArrayList<String> otherPlayerMove() throws ServerUnavailableException {
    	String respond = null;
    	String[] responds = null;
    	ArrayList<String> res = new ArrayList<String>();
    	while (true) {
            respond = this.readLineFromServer();
            System.out.println(respond);
            responds = respond.split(ProtocolMessages.DELIMITER);
            if (responds[0].equals("m") && ProtocolMessages.DIRECTIONAVAILABLE.contains(responds[3]) 
            		&& responds[1] != null && responds[2] != null && responds.length < 10) {
        		for (int i = 3; i < responds.length - 1; i++) {
        			res.add(responds[i]);
        		}
            	break;
            }
    	}
    	return res;
    	
    }

    /**
     * get how many people are in the lobbies.
     * @throws ServerUnavailableException Throws ServerUnavailableException
     * @throws ProtocolException Throws ProtocolException
     */
    public void queueNow() throws ServerUnavailableException, ProtocolException {
        String sent = ProtocolMessages.QUERY + ProtocolMessages.DELIMITER + ProtocolMessages.EOC;
        this.sendMessage(sent);
        String result = this.readLineFromServer();
        String[] split = result.split(ProtocolMessages.DELIMITER);
        if (split.length == 5 && split[0].equals(ProtocolMessages.QUERY)) {
            System.out.println("There are " + split[1] + " people in lobby for 2 players \n"
                    + "There are " + split[2] + " people in lobby for 3 players \n"
                    + "There are " + split[3] + " people in lobby for 4 players");
        } else {
            throw new ProtocolException("Wrong protocol from server");
        }
    }
   
    /**
     * send exit to the server.
     * @throws ServerUnavailableException Throws ServerUnavailableException
     */
    public void sendExit() throws ServerUnavailableException {
        // To be implemented
        this.sendMessage(ProtocolMessages.EXIT);
        this.closeConnection();
    }

    /**
     * run the client.
     * @throws ServerUnavailableException Throws ServerUnavailableException
     * @throws ProtocolException Throws ProtocolException
     */
    public static void main(String args[]) throws ServerUnavailableException, ProtocolException {
        (new AbaloneClient()).start();
    }

}
