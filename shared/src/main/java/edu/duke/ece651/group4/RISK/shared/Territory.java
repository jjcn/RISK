package edu.duke.ece651.group4.RISK.shared;


import java.io.Serializable;
import java.util.*;

import static edu.duke.ece651.group4.RISK.shared.Constant.UNIT_NAMES;

public class Territory implements Serializable {
    protected static final long serialVersionUID = 15L;
    /**
     * Territory's name
     */
    private final String name;
    /**
     * Food resource yield each turn
     */
    private int foodSpeed;
    /**
     * Tech resource yield each turn
     */
    private int techSpeed;

    private int area;

    private Troop ownerTroop;

    private final HashMap<String, Troop> enemyOnTerritory;

    private Random rnd;

    public Territory(String name, Player owner, int population, Random rnd) {
        this.name = name;
        this.enemyOnTerritory = new HashMap<>();
        this.ownerTroop = new Troop(population, owner, rnd);
        this.rnd = rnd;
        this.techSpeed = 0;
        this.foodSpeed = 0;
        this.area = 0;

    }

    public Territory(String name, Troop ownerTroop, HashMap<String, Troop> enemyOnTerritory, Random rnd) {
        this.name = name;
        this.enemyOnTerritory = enemyOnTerritory;
        this.ownerTroop = ownerTroop;
        this.rnd = rnd;
        this.techSpeed = 0;
        this.foodSpeed = 0;
        this.area = 0;
    }

    public Territory(String name) {
        this.name = name;
        this.enemyOnTerritory = new HashMap<>();
        this.ownerTroop = new Troop(0, new TextPlayer("")); // default Troop.owner == null, cannot call equals()
        this.rnd = new Random();
        this.techSpeed = 0;
        this.foodSpeed = 0;
        this.area = 0;
    }

    public Territory(String name, int area) {
        this.name = name;
        this.enemyOnTerritory = new HashMap<>();
        this.ownerTroop = new Troop(0, new TextPlayer("")); // default Troop.owner == null, cannot call equals()
        this.rnd = new Random();
        this.techSpeed = 0;
        this.foodSpeed = 0;
        this.area = area;
    }

    public Territory(String name, Random rnd) {
        this.name = name;
        this.enemyOnTerritory = new HashMap<>();
        this.ownerTroop = new Troop(0, new TextPlayer(""), rnd); // default Troop.owner == null, cannot call equals()
        this.rnd = rnd;
        this.techSpeed = 0;
        this.foodSpeed = 0;
        this.area = 0;
    }

    public String getName() {
        return this.name;
    }

    public Player getOwner() {
        return this.ownerTroop.getOwner();
    }

    public int getFoodSpeed() {
        return this.foodSpeed;
    }

    public int getTechSpeed() {
        return this.techSpeed;
    }

    public int getArea() {
        return this.area;
    }

    public void setFoodSpeed(int num) {
        this.foodSpeed = num;
    }

    public void setTechSpeed(int num) {
        this.techSpeed = num;
    }

    public void setArea(int num) {
        this.area = num;
    }

    public void setRandom(Random seed) {
        this.rnd = seed;
    }

    /**
     * Added specific number of unit to territory
     * 
     * @param num shows number added
     */
    public void addUnit(int num) {
        Troop newTroop = new Troop(num, this.getOwner());
        this.sendInTroop(newTroop);
    }

    /**
     * Initialize population and owner of territory
     * 
     * @param num   shows number added
     * @param owner shows owner of territory
     */
    public void initializeTerritory(int num, Player owner) {
        this.ownerTroop = new Troop(num, owner);
    }

    /**
     * Check population of territory
     */
    public int checkPopulation() {
        return this.ownerTroop.checkTroopSize();
    }

    /**
     * Send out specific number of unit from territory
     * 
     * @param subTroop shows the number of unit send out from territory
     */
    public Troop sendOutTroop(Troop subTroop) {
        return this.ownerTroop.sendTroop(subTroop.checkTroopSize());
    }

    /**
     * Send in specific number of unit to territory
     * 
     * @param subTroop shows the number of unit send in to territory
     */
    public void sendInTroop(Troop subTroop) {
        this.ownerTroop.receiveTroop(subTroop);
    }

    /**
     * Send in specific number of enemy to territory
     * 
     * @param enemy shows the enemy troop attack in
     */
    public void sendInEnemyTroop(Troop enemy) {
        String enemyName=enemy.getOwner().getName();
        if(this.enemyOnTerritory.get(enemyName)==null){
            this.enemyOnTerritory.put(enemyName, enemy);
        }else{
            Troop origin=this.enemyOnTerritory.get(enemyName);
            origin.receiveTroop(enemy);
            this.enemyOnTerritory.put(enemyName,origin);
        }

    }

    /**
     * Do battle between two troops
     * 
     * @param enemy shows the enemy troop attack in
     */
    public void doOneBattle(Troop enemy) {
        Troop enemyRemain = this.ownerTroop.combat(enemy);
        this.ownerTroop = this.ownerTroop.checkWin() ? this.ownerTroop : enemyRemain;
    }

    /**
     * Do all the battles with enemies the sequence is random
     * 
     * @return A report of all battles happened
     */
    public String doBattles() {

        StringBuilder report = new StringBuilder();

        if (this.enemyOnTerritory.size() == 0) {
            return "no war on Territory " + this.getName() + "\n";
        }

        ArrayList<String> enemyPlayers = new ArrayList<String>(this.enemyOnTerritory.keySet());
        Collections.sort(enemyPlayers);
        while (enemyPlayers.size() > 0) {

            int diceResult = randInt(0, enemyPlayers.size() - 1);

            String enemy = enemyPlayers.get(diceResult);
            report.append(
                    "Enemy " + enemy + " fight with " + this.getOwner().getName() + " on " + this.getName() + " \n");
            Troop enemyTroop = this.enemyOnTerritory.get(enemy);
            doOneBattle(enemyTroop);
            enemyPlayers.remove(diceResult);
            report.append(this.getOwner().getName() + " wins the fight and owns " + this.getName() + " \n");
        }
        this.enemyOnTerritory.clear();
        return report.toString();
    }

    private int randInt(int min, int max) {
        int randomNum = rnd.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            Territory otherTerritory = (Territory) other;
            return name.equals(otherTerritory.name);
        } else {
            return false;
        }
    }

    public Territory clone() {
        HashMap<String, Troop> cpy = new HashMap<>();
        for (String s : this.enemyOnTerritory.keySet()) {
            cpy.put(new String(s), this.enemyOnTerritory.get(s).clone());
        }
        Territory clone = new Territory(new String(this.name), ownerTroop.clone(), cpy, this.rnd);
        clone.setArea(this.area);
        clone.setFoodSpeed(this.foodSpeed);
        clone.setTechSpeed(this.techSpeed);
        return clone;
    }

    public HashMap<String, Integer> checkTroopInfo() {
        return this.ownerTroop.getDict();
    }

    public int upgradeTroop(int levelBefore, int levelAfter, int nUnit, int nResource) {
        int levelUp = levelAfter - levelBefore;
        return ownerTroop.updateUnit(levelBefore, levelUp, nUnit, nResource);
    }

    public String getInfo(){
        StringBuilder report = new StringBuilder();
        report.append("Owner : "+this.getOwner().getName()+"\n");
        report.append("Terr Name : "+this.name+"\n");
        report.append("Size : "+this.area+"\n");
        report.append("Food production : "+this.foodSpeed+"\n");
        report.append("Tech production : "+this.techSpeed+"\n");
        List<String> list=UNIT_NAMES;
        HashMap<String,Integer> dict=this.ownerTroop.getDict();
        for(String s:list){
            if(dict.get(s)==null){
                report.append(s+" : 0"+"\n");
            }else{
                report.append(s+" : "+dict.get(s)+"\n");
            }
        }
        return report.toString();
    }

}
