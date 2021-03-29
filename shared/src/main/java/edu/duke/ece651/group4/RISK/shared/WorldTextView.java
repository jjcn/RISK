package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles the textual display of a World by output a String.
 * i.e.
 * Green player:
 * -------------
 * 10 units in Narnia (next to: Elantris, Midkemia)
 * 12 units in Midkemia (next to: Narnia, Elantris, Scadrial, Oz)
 * 8 units in Oz (next to: Midkemia, Scadrial, Mordor, Gondor)
 * <p>
 * Blue player:
 * ------------
 * 6 units in Elantris (next to: Roshar, Scadrial, Midkemia, Narnia)
 * 3 units in Roshar (next to: Hogwarts, Scadrial, Elantris)
 * 5 units in Scadrial (next to: Elantris, Roshar, Hogwats, Mordor Oz, Midkemia, Elantris)
 * <p>
 * Red player:
 * -----------
 * 13 units in Gondor (next to: Oz, Mordor)
 * 14 units in Mordor (next to: Hogwarts, Gondor, Oz, Scadrial)
 * 3 units in Hogwarts (next to: Mordor, Scadrial, Roshar)
 **/
public class WorldTextView implements WorldView<String>, Serializable {
    private World toDisplay;

    public WorldTextView(World toDisplay) {
        this.toDisplay = toDisplay;
    }

    /**
     * The text view of world has two situations:
     * 1. the player can immediately see the moves he did;
     * 2. the player cannot see the results of other players' action.
     **/
    public String displayWorld() {
        StringBuilder display = new StringBuilder();
        HashMap<String, List<Territory>> w = getPlayerToTerr();
        for (Map.Entry player : w.entrySet()) {
            display.append(displayOnePlayer((String) player.getKey(), 
                           (List<Territory>) player.getValue()));
        }
        return display.toString();
    }

    @Override
    public String displayWorld(World world) {
        this.toDisplay = world;
        return this.displayWorld();
    }

    private HashMap<String, List<Territory>> getPlayerToTerr() {
        HashMap<String, List<Territory>> w = new HashMap<String, List<Territory>>();
        //get player - territories map
        for (Territory terr : toDisplay.getAllTerritories()) {
            String ownerName = terr.getOwner().getName();
            List<Territory> curr = w.get(ownerName);
            if (curr == null) {
                curr = new ArrayList<Territory>();
            }
            curr.add(terr);
            w.put(ownerName, curr);
        }
        return w;
    }

    private String displayOnePlayer(String ownerName, List<Territory> w) {
        StringBuilder display = new StringBuilder();
        display.append(ownerName + " Player:\n" + "-----------\n");
        for (Territory terr : w) {
            display.append(displayOneTerr(terr));
        }
        display.append("\n");
        return display.toString();
    }

    //e.g. 13 units in Gondor (next to: Oz, Mordor)
    private String displayOneTerr(Territory terr) {
        StringBuilder display = new StringBuilder(terr.checkPopulation() + " units in " + terr.getName());
        display.append(" (next to: ");
        String sep = "";
        for (Territory near : toDisplay.territories.getAdjacentVertices(terr)) {
            display.append(sep);
            display.append(near.getName());
            sep = ", ";
        }
        display.append(")\n");
        return display.toString();
    }
}