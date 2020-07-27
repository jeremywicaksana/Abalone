package client;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import shared.ExitProgram;
import shared.ProtocolException;
import shared.ServerUnavailableException;





public class AbaloneClientTui {

    private AbaloneClient client;

    public AbaloneClientTui(AbaloneClient a) {
        client = a;
    }

    /**
     * start the tui.
     * @throws ServerUnavailableException Throws ServerUnavailableException
     * @throws ProtocolException Throws ProtocolException
     */
    public void start() throws ServerUnavailableException, ProtocolException {
        this.printHelpMenu();
        while (true) {
            Scanner typing = new Scanner(System.in);
            String input = typing.nextLine();
            try {
                handleUserInput(input);
            } catch (ExitProgram e) {
                System.out.println("ending the tui");
                break;
            } 
        }
        client.sendExit();
        client.clearConnection();
    }

    /**
     * handle the user input.
     * @param input the message from server.
     * @throws ExitProgram Throws ExitProgram exception.
     * @throws ProtocolException Throws ProtocolException
     * @throws ServerUnavailableException Throws ServerUnavailableException
     */ 
    public void handleUserInput(String input) throws ExitProgram, ProtocolException, ServerUnavailableException {
        String[] split;
        split = input.split(" ");
        if (split[0] != null) {
            switch (split[0]) {
                case "j":
                    try {
                        client.joinQueue(split[1]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("list length must be 2!");
                    }
                    break;
                case "q":
                    client.queueNow(); //not yet implemented
                    break;
                case "x":
                    throw new ExitProgram("Program finished");
                default:
                    System.out.println("Command does not exist");
                    try {
                        client.clear();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    this.printHelpMenu();
            }
        } else {
            System.out.println("please enter a correct command");
        }

    }

    /**
     *prints the question that will be answered by client.
     *@Param question what will be asked to the user
     *@requires question != null
     *@return the answer in a string
     */
    public String getString(String question) {
        System.out.println(question);
        Scanner in = new Scanner(System.in);
        String res = in.nextLine().toLowerCase();
        //in.close();
        return res;
    }

    /**
     * print a number and always ask for a number if the given input is no number then return number.
     * @Param question what will be asked to the user
     * @return res in form of int
     */
    public int getInt(String question) {
        int res = 0;
        Scanner in;
        while (true) {
            System.out.println(question);
            in = new Scanner(System.in);
            try {
                res = in.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("should be number!");
            }
        }
        return res;
    }
    /**
     * return boolean depends of the given input of the question if yes then return true if no then return false.
     * @Param question what will be asked to the user
     */
    
    public boolean getBoolean(String question) {
        Scanner in;
        Boolean res;
        while (true) {
            System.out.println(question);
            in = new Scanner(System.in);
            String bool = in.nextLine().toLowerCase();
            res = false;
            if (bool.contains("yes")) {
                res = true;
                break;
            } else if (bool.contains("no")) {
                res = false;
                break;
            } 
        }
        return res;
    }

    /**
     * Prints the help menu with available input options.
     */
    public void printHelpMenu() {
        System.out.println("viable inputs:\n"
                + "j <size> - join to specific lobby size(2/3/4) \n" 
                + "q - asked how many person in the specific queue size\n" 
                + "m <direction> <coords> - send a move to server then wait for validation\n" 
                + "x - exit from the game");
    }
}
