package server;

import java.util.InputMismatchException;
import java.util.Scanner;

public class AbaloneServerTui {



    /**
     * prints the question that will be answered by server.
     *
     */
    public String getString(String question) {
        System.out.println(question);
        Scanner in = new Scanner(System.in);
        String res = in.nextLine().toLowerCase();
        return res;
    }

    /**
     * print a number and always ask for a number if the.
     * given input is no number then return number
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
    * return boolean depends of the given input of the question.
    * if yes then return true if no then return false
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
}
