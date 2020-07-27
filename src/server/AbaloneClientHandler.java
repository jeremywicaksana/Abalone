package server;

import game.Field;
import game.Game;
import game.Move;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.AbaloneServer;

import shared.ProtocolException;
import shared.ProtocolMessages;


public class AbaloneClientHandler implements Runnable {
    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;

    /** The connected AbaloneServer. */
    private AbaloneServer srv;

    /** Name of this ClientHandler. */
    private String name;
    
    private Game game;

    /** initialized the in and out and shutdown if it is catch exception.
     * @Param sock client that is accepted by server
     * @Param srv to access server methods
     * @Param name to know who is this player name of this handler
     */
    public AbaloneClientHandler(Socket sock, AbaloneServer srv, String name) {
        try {
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            this.sock = sock;
            this.srv = srv;
            this.name = name;
        } catch (IOException e) {
            shutdown();
        }
    }

    /**
     * return to get the client name.
     * @return string name
     */
    public String getClientName() {
        return name;
    }

    /**
     * run this handler where it always read from client response then act accordingly by handleCommand.
     */
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null) {
                System.out.println("> [" + getClientName() + "] Incoming: " + msg);
                handleCommand(msg);
                msg = in.readLine();
            }
            shutdown();
        } catch (IOException e) {
            shutdown();
        } catch (ProtocolException e) {
            shutdown();
        }
    }

    /**
     * handle client protocols and call the right function.
     * @param msg protocol messages from client
     * @throws IOException Throws IOException
     * @throws ProtocolException Throws ProtocolException
     * @requires msg != null
     */
    
    public void handleCommand(String msg) throws IOException, ProtocolException {
        String[] split = null;
        System.out.println(msg);
        split = msg.split(ProtocolMessages.DELIMITER);
        String res = "a";
        if (split[0] != "x") {
            switch (split[0]) {
                case "j":
                    res = srv.doJoin(split, getClientName());
                    break;
                case "q":
                    res = srv.doQueue(split);
                    break;
                case "m":
                    srv.checkMove(split, getGame().getPlayers());
                    break;
                default:
                    //throw new ProtocolException("commmand is not valid");
                    System.out.println("Command is not recognized");
            }
            if (!split[0].equals("m")) {
                out.write(res);
                out.newLine();
                out.flush();
            }

            if (split[0].equals("j")) {
                srv.gameStart(split);
            }

        } else {
            shutdown();
        }


    }
    
    /** set game to g.
     * @param g to setup the current game of the clienthandler and set the game to g
     * @requires g != null
     * 
     */
    public void setGame(Game g) {
        this.game = g;
    }
    
    /**
     * get game to be used in the checkmoves.
     * @return this.game
     */
    public Game getGame() {
        return this.game;
    }
    
    /**
     * send the protocol messages to the server.
     * @Param res string containing the protocol with delimiters (i.e. m;ur;1;2;3)
     * @throws IOException Throws IOException
     * @requires res != null
     */
    public void sendMove(String res) throws IOException {
        out.newLine();
        out.flush();
    }
    
    /**
     * update the board for this clientHandler.
     * @param fields the coordinates of the chosen fields
     * @param direction the direction movement of the marble
     * @param currentPlayer the number to get which player right now
     */
    public void changeBoard(ArrayList<Field> fields, String direction, int currentPlayer) {
        Move move = new Move(fields, direction, this.getGame().getBoard(), 
            this.getGame().getPlayers().get(currentPlayer));
        try {
            move.make();
        } catch (Exception e) {
            System.out.println("wrong move");
        }
    }

    /**
     * Send start to the client when all of the players in the lobby already met the requirements.
     * (lobby for 4 and there are 4 players).
     * @param names the list with string conatining the users
     * @throws IOException Throws IOException
     */
    public void sendStart(List<String> names) throws IOException {
        String res = ProtocolMessages.START;
        for (String name: names) {
            res = res + ProtocolMessages.DELIMITER + name;
        }
        res = res + ProtocolMessages.DELIMITER + ProtocolMessages.EOC;
        out.write(res);
        out.newLine();
        out.flush();
    }

    /**
     * close the connection of the clienthandler.
     */
    private void shutdown() {
        System.out.println("> [" + name + "] Shutting down.");
        try {
            in.close();
            out.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        srv.removeClient(this);
        srv.removeClientInLobby(this);
    }

}
