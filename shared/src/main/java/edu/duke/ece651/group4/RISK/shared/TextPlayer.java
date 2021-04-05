package edu.duke.ece651.group4.RISK.shared;

import java.io.*;
import java.util.*;

public class TextPlayer implements Player, Serializable {
    protected static final long serialVersionUID = 16L;
    private String playerName;
    final private PrintStream out;
    final private BufferedReader inputReader;
    final private HashMap<Character, String> actionTypes;
    final private Random rnd;
    final private boolean testMode;

    public TextPlayer(PrintStream out, Reader inputReader, String playerName, Random rnd,boolean mode) {
        this.playerName = playerName;
        this.inputReader = (BufferedReader) inputReader;
        this.out = out;
        this.actionTypes = new HashMap<>();
        actionTypes.put('D', "(D)one");
        actionTypes.put('M', "(M)ove");
        actionTypes.put('A', "(A)ttack");
        this.rnd = rnd;
        this.testMode = mode;
    }

    public TextPlayer(PrintStream out, Reader inputReader, String playerName) {
        this(out, inputReader, playerName, new Random(),false);
    }

    public TextPlayer(String playerName) {
        this.playerName = playerName;
        this.inputReader = null;
        this.out = null;
        this.actionTypes = new HashMap<>();
        this.rnd = null;
        this.testMode = false;
    }

    /**
     * This asks the user to input their name for this game and then constructs the Player. Output:
     * <p>
     * "Please enter your name in this game:
     *
     * @param out         the output stream.
     * @param inputReader to read from user.
     * @throws IOException
     */
    public TextPlayer(PrintStream out, Reader inputReader) throws IOException {
        this(out, inputReader, "");
        this.playerName = readInput("Please enter your name in this game:");
    }

    /**
     * Print instructions out to user and reader one line of input.
     *
     * @return String of user's input
     * @throws IOException
     */
    private String readInput(String instr) throws IOException {
        out.print(instr + "\n");
        String input = inputReader.readLine();
        if (input == null) {
            throw new EOFException("Can't read input.\n");
        }
        return input;
    }

    /**
     * Output sample:
     * <p>
     * what would you like to do?\n
     * (A)ttack\n
     * (M)ove\n
     * (D)one\n
     * <\p>
     * The action type is checked here. The validity of action is not checked here.
     * After choose a valid 'M' or 'A' action, ask user to input following:
     * <p>
     * Please input the territory name you would like to send out troop from:\n
     * Please input the territory name you would like to send troop to:\n
     * Please input the number of soldiers you would like to send:\n
     * <\p>
     * Territory name is not checked here. Number for troop only requires input an integer here.
     * If input is not an integer, ask the user to input again until receive a valid number:
     * <p>
     * Please choose a valid action type:\n
     * </p >
     *
     * @return an Order containing the information of the action; null if the user has done their actions in this turn.
     * Order information for move and attack includes source, destination and action name.
     * @throws IOException
     */
    @Override
    public BasicOrder doOneAction() throws IOException {
        StringBuilder instr = new StringBuilder(this.playerName + " what would you like to do?\n");
        for (String act : actionTypes.values()) {
            if (act != "(D)one") {
                instr.append(act + "\n");
            }
        }
        instr.append("(D)one\n");
        Character actionName = readActionName(instr.toString());
        if (actionName == 'D') {
            return new BasicOrder(null, null, null, actionName);
        } else {
            String src = readInput("Please input the territory name you would like to send out troop from:");
            String des = readInput("Please input the territory name you would like to send troop to:");
            int pop = readInteger("Please input the number of soldiers you would like to send:");

            Troop troop = testMode?new Troop(pop, this, this.rnd):new Troop(pop, this);
            return new BasicOrder(src, des, troop, actionName);
        }
    }

    /**
     * Here checks if the input action name is in the action types.
     * If not belong to any action type, output:
     * <p>
     * Please choose a valid action type:\n
     * <\p>
     * until a valid action type is received.
     *
     * @return a character representing the action name.
     * @throws IOException
     */
    private Character readActionName(String instr) throws IOException {
        char action = ' ';
        while (!actionTypes.keySet().contains(action)) {
            action = getInChar(instr);
            instr = "Please choose a valid action type:";
        }
        return action;
    }

    private char getInChar(String instr) throws IOException {
        char inChar = ' ';
        String Str = "";
        while(Str.length() == 0){
            Str = readInput(instr);
            Str = Str.trim();
            instr = "Nothing input, Please input again:";
        }
        inChar = Str.charAt(0);
        return inChar;
    }

    /**
     * Displays the int to territories mapping to user and asks them to choose one group.
     * Input of users must be an integer and should belong to the group.
     *
     * @param map containing the info of created territories.
     * @return an int the user input representing the territory they choose.
     * @throws IOException
     */
    @Override
    public int chooseTerritory(HashMap<Integer, List<Territory>> map) throws IOException {
        StringBuilder info = new StringBuilder("The world has following groups of territories:\n");
        for (Map.Entry<Integer, List<Territory>> entry : map.entrySet()) {
            info.append(entry.getKey()).append(":");
            String sep = " ";
            for (Territory terr : entry.getValue()) {
                info.append(sep).append(terr.getName());
                sep = ", ";
            }
            info.append("\n");
        }
        out.println(info);
        String instr = "Please input the group number you would like to choose:";
        int choice = readInteger(instr);
        while (!map.keySet().contains(choice)) {
            instr = "You have chose an invalid group number, please choose again:";
            choice = readInteger(instr);
        }
        return choice;
    }

    /**
     * Asks the user to re-input the total number of soldiers are not equal to the number they have.
     *
     * @param terrs that player own
     * @param total number of soldiers player can place.
     * @return an Order list.
     */
    public List<Order> doPlacement(List<Territory> terrs, int total) throws IOException {
        List<Order> orders = tryPlacement(terrs, total);
        while (orders == null) {
            out.print("Please input again.\n");
            orders = tryPlacement(terrs, total);
        }
        return orders;
    }

    /**
     * @param terrs
     * @param total
     * @return List<Order> if total of soldiers are place; null if exceeded or not enough soldier are placed.
     * @throws IOException
     */
    private List<Order> tryPlacement(List<Territory> terrs, int total) throws IOException {
        List<Order> orders = new ArrayList<Order>();
        int remain = total;
        for (Territory terr : terrs) {
            String name = terr.getName();
            int add = readInteger("You have "+remain+" more soldiers to place.\n" +
                    "Please input the number of soldiers you want to place in " + name + ":");
            remain -= add;
            if (remain < 0) {
                out.print("You have placed more soldier than you have.\n");
                return null;
            }
            Troop troop = testMode?new Troop(add, this, this.rnd):new Troop(add, this);
            orders.add(new PlaceOrder(name, troop));
        }
        if (remain > 0) {
            out.print("You have not use up all your soldiers. Don't be too confident you will win!!\n");
            return null;
        }
        return orders;
    }

    private int readInteger(String instr) throws IOException {
        String inputInt = "";
        while (!isNumeric(inputInt)) {
            inputInt = readInput(instr);
            instr = "The input is not one int, please input again:";
        }
        return Integer.parseInt(inputInt);
    }

    /**
     * Blanks before and after the string is accepted.
     *
     * @param str from the input source.
     * @return true is the input string contains one and only one valid integer.
     */
    private boolean isNumeric(String str) {
        str = str.trim();
        if (!str.equals("")) {
            for (int i = 0; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @return name of a player.
     **/
    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(this.getClass())) {
            TextPlayer c = (TextPlayer) o;
            return c.getName().equals(playerName);
        }
        return false;
    }

    /**
     * asks the user if he want to exit the game. valid choices: string starts with Y/y or N/n.
     *
     * @return true if receive input start with 'Y' or 'y', false if recieve 'N' or 'n'
     * @throws IOException
     */
    public boolean checkExit() throws IOException {
        String instr = "Do you want to exit game? y/n";
        Character e = getInChar(instr);
        while (e != 'Y' && e != 'y' && e != 'N' && e != 'n') {
            e = getInChar("Please input a valid choice:");
        }
        if (e == 'Y' || e == 'y') {
            return true;
        } else {
            return false;
        }
    }

}