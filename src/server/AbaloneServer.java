package server;

import shared.ExitProgram;
import shared.ProtocolException;
import shared.ProtocolMessages;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.Game;
import game.Move;
import game.Field;
import player.HumanPlayer;
import player.Player;





public class AbaloneServer implements Runnable {


    // serversocket of abaloneServer
    private ServerSocket ssock;

    // list of the clients connected
    private List<AbaloneClientHandler> clients;
    //player names
    private List<String> players;

    private AbaloneServerTui tui;
    private int next_client;
    //read and write to clients
    private BufferedReader in;
    private BufferedWriter out;

    //create a lobby for players with size 2/3/4
    private Map<String, List<AbaloneClientHandler>> lobby;
    //pair the people in the lobby and reset the value to 0
    private Map<String, List<AbaloneClientHandler>> pairing;

    private List<AbaloneClientHandler> lobby2 = new ArrayList<>();
    private List<AbaloneClientHandler> lobby3 = new ArrayList<>();
    private List<AbaloneClientHandler> lobby4 = new ArrayList<>();

    // used in pairing
    private List<AbaloneClientHandler> pair2 = new ArrayList<>();
    private List<AbaloneClientHandler> pair3 = new ArrayList<>();
    private List<AbaloneClientHandler> pair4 = new ArrayList<>();
    

    /**
     * generate the lobby to get how many people currently playing.
     * generate pairing to start the game if the number of the people is the same as the key
     * run tui
     */
    public AbaloneServer() {
        clients = new ArrayList<>();
        players = new ArrayList<>();
        lobby = new HashMap<String, List<AbaloneClientHandler>>();
        lobby.put("2", lobby2);
        lobby.put("3", lobby3);
        lobby.put("4", lobby4);
        pairing = new HashMap<String, List<AbaloneClientHandler>>();
        pairing.put("2", pair2);
        pairing.put("3", pair3);
        pairing.put("4", pair4);
        tui = new AbaloneServerTui();
    }
    
    /**
     * multithread server where it will accept client then run the correspond clienthandler.
     */
    public void run() {
        boolean newSocket = true;
        while (newSocket) {
            try {
                this.setup();
                while (true) {
                    Socket sock = ssock.accept();
                    in = new BufferedReader(
                            new InputStreamReader(sock.getInputStream()));
                    out = new BufferedWriter(
                            new OutputStreamWriter(sock.getOutputStream()));
                    String hello = in.readLine();
                    try {
                        String clientName = this.getClientName(hello);
                        String respond = this.handleHello(hello);
                        String info = "Client " + String.format("%03d", next_client++);
                        System.out.println(info + " " + clientName + " has entered");
                        AbaloneClientHandler handler = new AbaloneClientHandler(sock, this, clientName);
                        out.write(respond);//regarding hello handshake
                        out.newLine();
                        out.flush();

                        new Thread(handler).start();

                        clients.add(handler);//adds the client object for each new client
                        players.add(handler.getClientName());
                    } catch (ProtocolException e) {
                        System.out.println("handshake failed, client disconnected");
                        break;
                    }
                }
                newSocket = false;
            } catch (ExitProgram e1) {
                newSocket = false;
            } catch (IOException e) {
                System.out.println("A server IO error occurred: "
                        + e.getMessage());

                if (!tui.getBoolean("Do you want to open a new socket?(yes/no)")) {
                    newSocket = false;
                }
            }
        }
        System.out.println("server disconnect");
    }
    
    /**
     * get clienthandler based on clientname.
     * @Param clientName get the clientHandler with the specified name
     * @requires clientName != null
     * @return name of the client
     * 
     */
    public AbaloneClientHandler getClientHandler(String clientName) {
        AbaloneClientHandler res = null;
        for (String a: lobby.keySet()) {
            for (AbaloneClientHandler handler: lobby.get(a)) {
                if (handler.getClientName().equals(clientName)) {
                    res = handler;
                }
            }
        }
        return res;
    }

    /**
     * user asked for an input for the port then runs on the server with
     * specified port.
     */
    public void setup() throws UnknownHostException, ExitProgram {
        ssock = null;
        while (ssock == null) {
            int port = tui.getInt("Please enter the server port.");

            // try to open a new ServerSocket
            InetAddress local = InetAddress.getLocalHost();
            String addr = local.getHostAddress();
            try {
                System.out.println("Attempting to open a socket at " + addr
                        + " on port " + port + "...");
                ssock = new ServerSocket(port);
                System.out.println("Server started at port " + port);
            } catch (IOException e) {
                System.out.println("ERROR: could not create a socket on "
                        + addr + " and port " + port + ".");

                if (!tui.getBoolean("Do you want to try again?")) {
                    throw new ExitProgram("User indicated to exit the "
                            + "program.");
                }
            }
        }
    }

    /**
    * remove the client if the handler is disconnected and.
    * @param client is the clienthandler what will be remove in the clients list and 
    * the name of the clienthandler is used to remove the name in the client name
    * @requires client != null
    * @ensures clients.size() = clients.size() - 1
    * @ensures players.size() = players.size() - 1
    */
    public void removeClient(AbaloneClientHandler client) {
        this.clients.remove(client);
        this.players.remove(client.getClientName());

    }
    
    /**
     * remove the client in the current lobby if the player disconnected or finish playing the game.
     * @param client the clienthandler where it will be remove
     * @requires client != null
     * 
     */
    public void removeClientInLobby(AbaloneClientHandler client) {
        if (lobby.get("2").contains(client)) {
            lobby.get("2").remove(client);
        } else if (lobby.get("3").contains(client)) {
            lobby.get("3").remove(client);
        } else if (lobby.get("4").contains(client)) {
            lobby.get("4").remove(client);
        }

    }


    // =========================Server methods=======================
    /**
     * if name is duplicated change add a number has not been implemented.
     * @param msg get the client command h and get the name of the client that is connected only
     * @return client name
     * @throws ProtocolException Throws ProtocolException
     * @requires msg != null

     */
    public String getClientName(String msg) throws ProtocolException {
        String[] client = msg.split(ProtocolMessages.DELIMITER);
        if (client[0].equals(ProtocolMessages.HELLO) 
                && ProtocolMessages.AVAILABLE.contains(client[1]) 
                && ProtocolMessages.AVAILABLE.contains(client[2])
               && ProtocolMessages.AVAILABLE.contains(client[3]) && client[4] != null) {
            int i = 0;
            String temp = client[4];
            while (true) {

                i++;
                if (players.contains(client[4])) {
                    client[4] = client[4].substring(0, temp.length()) + String.valueOf(i);
                } else {
                    break;
                }
            }
            return client[4];
        } else {
            throw new ProtocolException("is not a right protocol for hello");
        }
    }


    /**
     * if name is duplicated change add a number has not been implemented.
     * @param msg hello command which is h;<0/1>;<0/1>;<0/1> ; name
     * @requires msg != null
     * @ensures result != null
     * @throws ProtocolException Throws ProtocolException
     */
    public synchronized String handleHello(String msg) throws ProtocolException {
        String[] client = msg.split(ProtocolMessages.DELIMITER);
        if (client[0].equals(ProtocolMessages.HELLO)
                && ProtocolMessages.AVAILABLE.contains(client[1])
                && ProtocolMessages.AVAILABLE.contains(client[2])
                && ProtocolMessages.AVAILABLE.contains(client[3]) && client[4] != null) {
            int i = 0;
            String temp = client[4];
            while (true) {

                i++;
                if (players.contains(client[4])) {
                    client[4] = client[4].substring(0, temp.length()) + String.valueOf(i);
                } else {
                    break;
                }
            }
            String res = ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + ProtocolMessages.CHATTING
                    + ProtocolMessages.DELIMITER + ProtocolMessages.CHALLANGE + ProtocolMessages.DELIMITER
                    + ProtocolMessages.LEADERBOARD + ProtocolMessages.DELIMITER + client[4]
                    + ProtocolMessages.DELIMITER + ProtocolMessages.EOC;
            return res;
        } else {
            throw new ProtocolException("is not a right protocol for hello");
        }
    }

    /**
     * receive join from client and send back the number people in the lobby specified by client.
     * @param split the command j
     * @param playerName is the the name of the player in the clientHandler
     * @throws ProtocolException Throws ProtocolException
     * @ensures split != null
     * @ensures playerName != null
     * @requires result != null
     */
    public synchronized String doJoin(String[] split, String playerName) throws ProtocolException {
        String result = null;
        if (split.length == 3 && ProtocolMessages.SIZEAVAILABLE.contains(split[1])) {
            for (AbaloneClientHandler client : clients) {
                if (client.getClientName().equals(playerName)) {
                    if (lobby.containsKey(split[1])) {
                        lobby.get(split[1]).add(client);
                        pairing.get(split[1]).add(client);
                        break;
                    }
                }
            }
            result = ProtocolMessages.JOIN + ProtocolMessages.DELIMITER + split[1]
                    + ProtocolMessages.DELIMITER + lobby.get(split[1]).size()
                    + ProtocolMessages.DELIMITER + ProtocolMessages.EOC;

        } else {
            throw new ProtocolException("format should be j;<queuesize>;* ");
        }
        return result; //call Start
    }

    /**
     * if the player in the pairing map has the same size list with their corresponding key, it will run the game.
     * @param split the command j where it shows the player plays at what lobby
     * @requires split != null
     *
     */
    public void gameStart(String[] split) {
        List<AbaloneClientHandler> temp = new ArrayList<AbaloneClientHandler>();
        List<String> clientsName = new ArrayList<String>();            //create a list containing clientName
        Player h = null;//empty player
        ArrayList<Player> player = new ArrayList<Player>();//used tput player name as a human player object
        
        //check if the lobby is already at the requirements or not (i.e. lobby 2 there are 2 people)
        if (split[1].equals(String.valueOf(pairing.get(split[1]).size()))) {
            //send start to both clienthandlers

            for (AbaloneClientHandler c : pairing.get(split[1])) {
                clientsName.add(c.getClientName());
                temp.add(c);
            }
            // start each clientHandler
            int i = 0;
            for (AbaloneClientHandler c : pairing.get(split[1])) {
                try {
                    c.sendStart(clientsName);
                    h = new HumanPlayer(c.getClientName(), ProtocolMessages.COLORS.get(i));
                    player.add(h);
                    i++;
                } catch (IOException e) {
                    System.out.println("fail");
                }
            }
            // remove pairs that already met requirements (key 2 consist of list size 2 will be remove into 0
            // key 3 remove if the size is 3 and same for 4
            pairing.get(split[1]).clear();
        }
        Game g = new Game(player);
        try {
            g.setup();
        } catch (Exception e) {
            System.out.println("not enough player");
        }
        for (AbaloneClientHandler c: temp) {
            c.setGame(g);
        }
    }

    // m;playername;direction;coord;coord;coord;coord;* <-- maximal
    /**
     * checkMove on current board whether it is valid or not.
     * also prints the board after move is validated
     * @param split the result from the clienthandler read from client
     * @param players to get how many players and get the client handler to send back to their own client
     * @requires split != null
     * @requires players != null
     */
    public synchronized void checkMove(String[] split, List<Player> players) {
        String res = "Invalid move";
        String player = split[1]; //get the playerName
        String direction = split[2];  //get the direction
        ArrayList<String> coord = new ArrayList<String>();
        ArrayList<AbaloneClientHandler> handler = new ArrayList<AbaloneClientHandler>();

        for (int i = 3; i < split.length - 1; i++) {
            coord.add(split[i]); //get the coords
        }
        //add the handler 
        for (Player people: players) {
            handler.add(this.getClientHandler(people.getName()));
        }

    	//run each clientHandler 

    	for (AbaloneClientHandler handle : handler) {
    		Move move = null;
    		ArrayList<Field> fields = handle.getGame().chooseMarble(coord);
    		System.out.println(coord);
    		int i = 0;
    		while (true) {
    			if (handle.getGame().getPlayers().get(i).getName().equals(player)) {
    				break;
    			}
    			i++;
    		}
    	
            move = new Move(fields, direction, handle.getGame().getBoard(), handle.getGame().getPlayers().get(i));
            handle.getGame().increaseTurnCount(); //increase turn
            if (handle.getGame().gameOver()) {
            	this.removeClientInLobby(handle);
            	handle.setGame(null);
            }
            if (move.isValid()) {
            	handle.changeBoard(fields, direction, i);
        		int nextHandler = (i + 1) % handler.size();
				res = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER
						+ handle.getGame().getPlayers().get(nextHandler).getName()
						+ ProtocolMessages.DELIMITER + player
            			+ ProtocolMessages.DELIMITER + direction + ProtocolMessages.DELIMITER;
				for (String loc : coord) {
					res = res + loc + ProtocolMessages.DELIMITER;//get the coodrinates
				}
				res = res + ProtocolMessages.EOC;

            } else {
            	System.out.println("something wrong");
            }
    		try {
				handle.sendMove(res);
			} catch (IOException e) {
				System.out.println("send move fail");
			}
    	
    	}
    }
    
    /**
     * send how many players are in the queues.
     * @param split the command q from client
     * @return current lobby
     * @throws ProtocolException Throws ProtocolException
     * @requires split != null

     */
    public synchronized String doQueue(String[] split) throws ProtocolException {
        String result = null;
        if (split.length > 1) {
            result = ProtocolMessages.QUERY + ProtocolMessages.DELIMITER + lobby.get("2").size()
                    + ProtocolMessages.DELIMITER
                    + lobby.get("3").size() + ProtocolMessages.DELIMITER + lobby.get("4").size()
                    + ProtocolMessages.DELIMITER + ProtocolMessages.EOC;
        } else {
            throw new ProtocolException("Client protocol for query length is more than 1");
        }
        return result;
    }



    /**
     * run the server.
     */
    public static void main(String args[]) {
        AbaloneServer server = new AbaloneServer();

        new Thread(server).start();
    }

}