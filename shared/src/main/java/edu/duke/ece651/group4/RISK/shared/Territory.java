package edu.duke.ece651.group4.RISK.shared;


import java.io.Serializable;
import java.util.*;

import static edu.duke.ece651.group4.RISK.shared.Constant.JOB_DICTIONARY;
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

    protected Troop allianceTroop;

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
        this.allianceTroop = null;

    }

    public Territory(String name, Troop ownerTroop, HashMap<String, Troop> enemyOnTerritory, Random rnd) {
        this.name = name;
        this.enemyOnTerritory = enemyOnTerritory;
        this.ownerTroop = ownerTroop;
        this.rnd = rnd;
        this.techSpeed = 0;
        this.foodSpeed = 0;
        this.area = 0;
        this.allianceTroop = null;
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
        this.allianceTroop = null;
    }

    public Territory(String name, Random rnd) {
        this.name = name;
        this.enemyOnTerritory = new HashMap<>();
        this.ownerTroop = new Troop(0, new TextPlayer(""), rnd); // default Troop.owner == null, cannot call equals()
        this.rnd = rnd;
        this.techSpeed = 0;
        this.foodSpeed = 0;
        this.area = 0;
        this.allianceTroop = null;
    }

    public String getName() {
        return this.name;
    }

    public Player getOwner() {
        return this.ownerTroop.getOwner();
    }

    public String getOwnerName() {
        return this.getOwner().getName();
    }

    public Player getAlliance() throws IllegalArgumentException {
        if (this.allianceTroop == null) {
            throw new IllegalArgumentException(
                    String.format("No alliance troop stationed on %s.", name)
            );
        }
        return this.allianceTroop.getOwner();
    }

    public String getAllianceName() {
        return this.getAlliance().getName();
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
    public void setOwnerTroop(int num, Player owner) {
        this.ownerTroop = new Troop(num, owner);
    }

    /**
     * Check the size of the troop that belongs to a player on this territory
     * @param playerName is the name of player
     * @return the size of troop this payer stationed on this territory
     */
    public int getTroopSize(String playerName) throws IllegalArgumentException {
        if (ownerTroop.getOwner().getName().equals(playerName)) {
            return ownerTroop.checkTroopSize();
        } else if (allianceTroop.getOwner().getName().equals(playerName)) {
            return allianceTroop.checkTroopSize();
        } else {
            throw new IllegalArgumentException(
                    String.format("%s does not have troop stationed on %s", playerName, this.name)
            );
        }
    }

    /**
     * Check population of the territory's owner troop
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
        if (!subTroop.getOwner().getName()
                .equals(this.ownerTroop.getOwner().getName())) {
            if (allianceTroop == null) {
                throw new IllegalArgumentException("No alliance troop now");
            } else {
                allianceTroop.sendTroop(subTroop);
                if (allianceTroop.checkTroopSize() == 0) {
                    allianceTroop = null;
                }

            }
        }

        return this.ownerTroop.sendTroop(subTroop);
    }

    /**
     * Send in specific number of unit to territory
     *
     * @param subTroop shows the number of unit send in to territory
     */
    public void sendInTroop(Troop subTroop) {
        if (!subTroop.getOwner().getName()
                .equals(this.ownerTroop.getOwner().getName())) {
            if (allianceTroop == null) {
                allianceTroop = subTroop;
            } else {
                allianceTroop.receiveTroop(subTroop);
            }
        } else {
            this.ownerTroop.receiveTroop(subTroop);
        }
    }

    /**
     * Send in specific number of enemy to territory
     *
     * @param enemy shows the enemy troop attack in
     */
    public void sendInEnemyTroop(Troop enemy) {
        String enemyName = enemy.getOwner().getName();
        if (this.enemyOnTerritory.get(enemyName) == null) {
            this.enemyOnTerritory.put(enemyName, enemy);
        } else {
            Troop origin = this.enemyOnTerritory.get(enemyName);
            origin.receiveTroop(enemy);
            this.enemyOnTerritory.put(enemyName, origin);
        }

    }

    /**
     * Do battle between two troops
     *
     * @param enemy shows the enemy troop attack in
     */
    public void doOneBattle(Troop enemy) {
        Troop enemyRemain = this.ownerTroop.combat(enemy);
        this.ownerTroop = enemyRemain.checkWin() ? enemyRemain : this.ownerTroop;
    }

    public Troop findEnemyPartner(String targetName, Map<String, Set<String>> relation) {
        Troop partner = null;
        for (String n : relation.get(targetName)) {
            if (this.enemyOnTerritory.get(n) != null) {
                partner = this.enemyOnTerritory.get(n);
            }
        }
        return partner;
    }

    public void doOneBattleMix(Troop enemy, Troop partner) {


        boolean attack = true;
        boolean myTurn = true;
        boolean enemyTurn = true;


        while (this.ownerTroop.checkTroopSize() > 0 && enemy.checkTroopSize() > 0) {
            Troop ownerSide = myTurn ? this.ownerTroop : this.allianceTroop;
            Troop enemySide = enemyTurn ? enemy : partner;
            if (attack) {
                ownerSide = enemySide.doOneCombat(ownerSide);
            } else {
                enemySide = ownerSide.doOneCombat(enemySide);
            }

            if (myTurn) {
                this.ownerTroop = ownerSide;
            } else {
                allianceTroop = ownerSide;
            }

            if (enemyTurn) {
                enemy = enemySide;
            } else {
                partner = enemySide;
            }

            if (allianceTroop == null || allianceTroop.checkTroopSize() == 0) {
                myTurn = true;
            } else {
                if (this.ownerTroop.checkTroopSize() == 0) {
                    this.ownerTroop = allianceTroop;
                    this.allianceTroop = null;
                    myTurn = true;
                } else {
                    myTurn = !myTurn;
                }
            }

            if (partner == null || partner.checkTroopSize() == 0) {
                enemyTurn = true;
            } else {
                if (enemy.checkTroopSize() == 0) {
                    enemy = partner;
                    partner = null;
                    enemyTurn = true;
                } else {
                    enemyTurn = !enemyTurn;
                }
            }

        }

        this.ownerTroop = enemy.checkWin() ? enemy : this.ownerTroop;
        this.allianceTroop=enemy.checkWin() ?partner:allianceTroop;
    }


    public String doBattlesMix(Map<String, Set<String>> relation) {
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
                    "Enemy " + enemy + " attack " + this.getOwner().getName() + " on " + this.getName() + " \n");
            Troop partner = findEnemyPartner(enemy, relation);

            Troop enemyTroop = this.enemyOnTerritory.get(enemy);
            report.append(enemyTroop.getSummary());
            if (partner != null) {
                report.append(
                        "Enemy ally " + partner.getOwner().getName() + " help to attack " + this.getOwner().getName() + " on " + this.getName() + " \n");
                report.append(partner.getSummary());
            }
            report.append(this.ownerTroop.getSummary());
            doOneBattleMix(enemyTroop, partner);
            enemyPlayers.remove(diceResult);
            if (partner != null) {
                int ind = enemyPlayers.indexOf(partner.getOwner().getName());
                enemyPlayers.remove(ind);
            }
            report.append(this.getOwner().getName() + " wins the fight and owns " + this.getName() + " \n");
        }
        this.enemyOnTerritory.clear();
        this.archerReady();
        return report.toString();
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
                    "Enemy " + enemy + " attack " + this.getOwner().getName() + " on " + this.getName() + " \n");

            Troop enemyTroop = this.enemyOnTerritory.get(enemy);
            report.append(enemyTroop.getSummary());
            report.append(this.ownerTroop.getSummary());
            doOneBattle(enemyTroop);
            enemyPlayers.remove(diceResult);
            report.append(this.getOwner().getName() + " wins the fight and owns " + this.getName() + " \n");
        }
        this.enemyOnTerritory.clear();
        this.archerReady();
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
        if (this.allianceTroop != null) {
            Troop cloneAlly = this.allianceTroop.clone();
            clone.sendInAlly(cloneAlly);
        }

        return clone;
    }

    public HashMap<String, Integer> checkTroopInfo() {
        return this.ownerTroop.getDict();
    }


    public int upgradeTroop(UpgradeTroopOrder utOrder, int nResource) {
        return ownerTroop.updateUnit(utOrder, nResource);
    }

    /**
     * Get all info of a territory in text form.
     * To be displayed on in TurnActivity UI.
     * @return a String displaying the info of a territory.
     */
    public String getInfo() {
        StringBuilder report = new StringBuilder();
        report.append("Owner : " + this.getOwner().getName() + "\n");
        report.append("Terr Name : " + this.name + "\n");
        report.append("Size : " + this.area + "\n");
        report.append("Food production : " + this.foodSpeed + "\n");
        report.append("Tech production : " + this.techSpeed + "\n");

        report.append(getTroopInfo(this.ownerTroop));
        if (hasAllianceTroop()) {
            report.append(getTroopInfo(this.allianceTroop));
        }

        return report.toString();
    }

    /**
     * Get all info of a troop in text form.
     * @param troop is a troop.
     * @return a String displaying the info of a troop.
     */
    public String getTroopInfo(Troop troop) {
        StringBuilder ans = new StringBuilder();
        ans.append(troop.getOwner().getName() + "'s troop: " + "\n");
        HashMap<String, Integer> dict = troop.getDict();
        for (String jobName : dict.keySet()) {
            int nUnit = dict.get(jobName) == null ? 0 : dict.get(jobName);
            ans.append(jobName + " : " + nUnit + "\n");
        }
        return ans.toString();
    }

    /**
     * From certain job to new Job name with certain number of unit
     *
     * @return cost of transfer
     */
    public int transfer(String from, String to, int unitLevel, int nUnit) {
        return this.ownerTroop.transfer(from, to, unitLevel, nUnit);
    }

    public Troop sendRangedAttack(Troop target) {
        return this.ownerTroop.sendRangedAttack(target);
    }

    public boolean hasRangedTroop() {
        return this.ownerTroop.hasRanged();
    }

    public void archerReady() {
        this.ownerTroop.archerReady();
    }

    public Troop kickOut() {
        Troop target = this.allianceTroop;
        this.allianceTroop = null;
        return target;
    }

    public void sendInAlly(Troop ally) {
        this.allianceTroop = ally;
    }

    /**
     * Check if this territory is stationed a troop of an alliance.
     * @return true, if there is an alliance troop;
     *          false, if not.
     */
    public boolean hasAllianceTroop() {
        return this.allianceTroop != null;
    }


    public int checkUnitNum(String jobName){
        return this.ownerTroop.checkUnitNum(jobName);
    }



    public int checkUnitNumAlly(String jobName){
        if(this.allianceTroop==null){
            return 0;
        }
        return this.allianceTroop.checkUnitNum(jobName);
    }


    public Map<String,Integer> checkTypeNumSpec(String typeName,Troop target){
        if(JOB_DICTIONARY.get(typeName)==null){
            throw new IllegalArgumentException("Wrong Unit Type name "+typeName);
        }

        Map<String,Integer> m=new HashMap<>();
        if(target==null){
            return m;
        }

        for(String s:JOB_DICTIONARY.get(typeName)){
            m.put(s,target.checkUnitNum(s));
        }
        return m;
    }

    public Map<String,Integer> checkTypeNum(String typeName){
        return this.checkTypeNumSpec(typeName,this.ownerTroop);
    }

    public Map<String,Integer> checkTypeNumAllay(String typeName){
        return this.checkTypeNumSpec(typeName,this.allianceTroop);
    }
}
