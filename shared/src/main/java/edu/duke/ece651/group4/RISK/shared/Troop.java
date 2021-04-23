package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.SHIELD;

public class Troop implements Serializable {
    protected static final long serialVersionUID = 17L;
    private final ArrayList<Unit> population;
    private final HashMap<String, Integer> dict;


    private Player owner;

    public Troop(int number, Player owner, Random rand) {
        this.owner = owner;
        this.population = new ArrayList<>();
        this.owner = new TextPlayer(owner.getName());
        this.dict = new HashMap<>();

        dict.put("Soldier LV0", number);
        for (int i = 0; i < number; i++) {
            Soldier s = new Soldier(rand);
            population.add(s);
        }
    }

    public Troop(int number, Player owner) {
        this.owner = owner;
        this.population = new ArrayList<>();
        this.owner = new TextPlayer(owner.getName());
        this.dict = new HashMap<>();

        dict.put("Soldier LV0", number);

        for (int i = 0; i < number; i++) {
            Soldier s = new Soldier(new Random());
            population.add(s);
        }
    }

    public Troop(ArrayList<Unit> subTroop, Player owner) {
        this.population = subTroop;
        this.owner = new TextPlayer(owner.getName());
        this.dict = new HashMap<>();

        for (Unit u : subTroop) {
            String name = u.getJobName();
            if (dict.get(name) == null) {
                dict.put(name, 1);
            } else {
                dict.put(name, dict.get(name) + 1);
            }
        }
    }

    public Troop(ArrayList<Unit> subTroop, HashMap<String, Integer> myDict, Player owner) {
        this.population = subTroop;
        this.owner = new TextPlayer(owner.getName());
        this.dict = myDict;

    }

    public Troop(HashMap<String, Integer> myDict, Player owner) {
        this.population = new ArrayList<>();
        for (String s : myDict.keySet()) {

            for (int i = 0; i < myDict.get(s); i++) {
                Soldier sol = new Soldier();
                this.population.add(sol);
            }
        }
        this.owner = new TextPlayer(owner.getName());
        this.dict = myDict;

    }

    /**
     * Get the size of a troop.
     *
     * @return number of units in a troop.
     */
    public int size() {
        return this.population.size();
    }

    /**
     * Get the attack range of a troop.
     * The troop MUST be consisted of units of the same type.
     * @return
     */
    public int getRange() {
        int range = 0;
        for (Unit unit : this.population) {
            range = unit.getRange();
        }
        return range;
    }

    /**
     * Do battle between two troops
     *
     * @param enemy shows the enemy troop attack in
     */
    public Troop combat(Troop enemy) {
        boolean attack = true;
        boolean myTurn =true;
        
        while (this.checkTroopSize() != 0 && enemy.checkTroopSize() != 0) {

            Unit myUnit = attack ? this.getWeakest() : this.getStrongest();
            Unit enemyUnit = attack ? enemy.getStrongest() : enemy.getWeakest();

            if (myUnit.fight(enemyUnit)) {
                enemy.loseUnit(enemyUnit);
            } else {

                if (ARROW_NAMES.contains(enemyUnit.getJobName())) {
                    enemy.loseUnit(enemyUnit);
                    this.loseUnit(myUnit);
                } else if (SHIELD_NAMES.contains(enemyUnit.getJobName())) {
                    Shield s = (Shield) enemyUnit;
                    if (!s.shieldExist()) {
                        this.loseUnit(myUnit);
                    }
                }
            }
            attack = !attack;
        }
        return enemy;
    }

    /**
     * Check population of troop
     */
    public int checkTroopSize() {
        return this.population.size();
    }

    /**
     * delete a specific unit from troop
     */
    public void loseUnit(Unit loss) {
        String name = loss.getJobName();
        this.dict.put(name, this.dict.get(name) - 1);
        this.population.remove(loss);
    }

    /**
     * send a unit from troop
     */
    public Unit dispatchUnit() {
        return this.population.get(0);
    }

    /**
     * send a unit from troop
     */
    public Unit dispatchCertainUnit(String name) {
        if (this.dict.get(name) == null || this.dict.get(name) == 0) {
            throw new IllegalArgumentException("No enough Unit at this level");
        }
        for (Unit u : this.population) {
            if (u.getJobName().equals(name)) {
                this.dict.put(name, this.dict.get(name) - 1);
                Unit target = u;
                this.population.remove(u);
                return target;
            }
        }


        return null;
    }

    /**
     * receive a specific unit
     */
    public void receiveUnit(Unit target) {
        String name = target.getJobName();
        if (this.dict.get(name) == null) {
            this.dict.put(name, 1);
        } else {
            this.dict.put(name, this.dict.get(name) + 1);
        }

        this.population.add(target);
    }

    public Unit getUnit(String name) {
        for (Unit u : this.population) {
            if (u.getJobName().equals(name)) {
                return u;
            }
        }

        return null;
    }

    /**
     * Send out specific number of troops
     */
    public Troop sendTroop(int number) {
        ArrayList<Unit> sub = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Unit movedUnit = this.dispatchUnit();
            sub.add(movedUnit);
            this.loseUnit(movedUnit);
        }
        return new Troop(sub, getOwner());
    }

    public Troop sendTroop(Troop target) {
        ArrayList<Unit> sub = new ArrayList<>();
        HashMap<String, Integer> subDict = target.getDict();

        if (!this.checkSend(target)) {
            throw new IllegalArgumentException("Target sending troop has invalid size");
        }

        for (String s : subDict.keySet()) {
            int num = subDict.get(s);
            for (int i = 0; i < num; i++) {
//                try {
                Unit t = this.dispatchCertainUnit(s);
                sub.add(t);
//                }catch(Exception e){
//                    throw new IllegalArgumentException("NULL at dispatch "+i);
//                }
            }
        }
        Troop r = new Troop(sub, subDict, this.owner);
//        for(Unit c:sub){
//            if(c==null){
//                throw new IllegalArgumentException("NULL happen when depart");
//            }
//        }
        return r;
    }

    /**
     * Check if troop win the fight
     */
    public boolean checkWin() {
        return this.checkTroopSize() > 0;
    }

    /**
     * receive specific number of troops
     */
    public void receiveTroop(Troop subTroop) {
        while (subTroop.checkTroopSize() != 0) {
            Unit newMember = subTroop.dispatchUnit();
//            try{
            this.receiveUnit(newMember);
//            }catch(Exception e){
//                throw new IllegalArgumentException("NULL at receive  "+subTroop.checkTroopSize());
//            }
//            try{
            subTroop.loseUnit(newMember);
//            }catch(Exception e){
//                throw new IllegalArgumentException("NULL at receive  "+subTroop.checkTroopSize());
//            }

        }
    }

    /**
     * return owner of troop
     */
    public Player getOwner() {

        return this.owner;
    }

    public Troop clone() {
        ArrayList<Unit> cloneList = new ArrayList<Unit>(population.size());
        for (Unit item : population) {
            cloneList.add(item.clone());
        }

        if (this.owner.getName() == null) {
            return new Troop(cloneList, new TextPlayer(null));
        } else {
            return new Troop(cloneList, new TextPlayer(new String(this.owner.getName())));
        }
    }

    /**
     * Check if a unit exists in troop.
     * @param name the name of unit.
     */
    public void checkUnitExistence(String name) {
        if (this.dict.get(name) == null) {
            throw new NoSuchElementException(String.format("%s does not exist in this troop.", name));
        }
    }

    public int checkUnitNum(String name) {
        if (this.dict.get(name) == null) {
            return 0;
        }
        return this.dict.get(name);
    }

    /**
     * Construct jobName like: Soldier LV0
     *
     * @param unitType is the type of unit defined in shared/Constant.
     * @param unitLevel is the level of unit.
     * @return constructed jobName.
     */
    public static String buildJobName(String unitType, int unitLevel) {
        String jobName;
        List<String> levelNames = Constant.JOB_DICTIONARY.get(unitType);
        if (levelNames == null) {
            throw new IllegalArgumentException(
                    String.format("Type %s does not exist.", unitType)
            );
        }
        try {
            jobName = levelNames.get(unitLevel);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    String.format("Level %d for type %s does not exist.", unitLevel, unitType)
            );
        }
        return jobName;
    }

    /**
     * Upgrades troop using an upgrade troop order. Also checks if there is enough resource.
     * @param utOrder is an upgrade troop order.
     * @param nResource is the number of resource at hand.
     * @return remaining resource after upgrade.
     */
    public int updateUnit(UpgradeTroopOrder utOrder, int nResource) {
        int levelBefore = utOrder.getLevelBefore();
        int levelAfter = utOrder.getLevelAfter();
        int levelUp = levelAfter - levelBefore;

        String from = buildJobName(utOrder.getTypeName(), levelBefore);

        int nUnit = utOrder.getNUnit();

        return updateUnit(from, levelUp, nUnit, nResource);
    }

    public int updateUnit(String from, int levelUp, int num, int resource) {

        if (this.checkUnitNum(from) < num) {
            throw new IllegalArgumentException("No enough Unit to upgrade");
        }

        try {
            while (num > 0) {

                Soldier target = (Soldier) this.getUnit(from);

                resource = target.upGrade(target.getLevel() + levelUp, resource);

                this.dict.put(from, this.dict.get(from) - 1);

                int newNum = this.dict.get(target.getJobName()) == null
                        ? 1
                        : this.dict.get(target.getJobName()) + 1;

                this.dict.put(target.getJobName(), newNum);

                num--;
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage() + "\n" + num + " units not upgraded");
        }

        return resource;
    }

    public HashMap<String, Integer> getDict() {
        return this.dict;
    }

    public boolean checkSend(Troop target) {
        HashMap<String, Integer> check = target.getDict();
        for (String s : check.keySet()) {
            if (this.dict.get(s) == null || this.dict.get(s) < check.get(s))
                return false;
        }
        return true;
    }

    public Unit getStrongest() {
        int maxLevel = -1;
        Unit target = null;

        for (String s : this.dict.keySet()) {

            if (dict.get(s)!= 0) {
                Soldier check = (Soldier) this.getUnit(s);
                if (ARROW_NAMES.contains(check.getJobName())) {
                    return check;
                } else if (BREAKER_NAMES.contains(check.getJobName())) {
                    if(target==null||!BREAKER_NAMES.contains(target.getJobName())) {
                        maxLevel = check.getLevel();
                        target = check;
                    }else if(BREAKER_NAMES.contains(check.getJobName())&&maxLevel < check.getLevel()){
                        maxLevel = check.getLevel();
                        target = check;
                    }

                }else if (maxLevel < check.getLevel()) {
                    if(target==null||!BREAKER_NAMES.contains(check.getJobName())) {
                        maxLevel = check.getLevel();
                        target = check;
                    }
                }
            }
        }
        return target;
    }

    public boolean hasRanged() {
        for (String s : dict.keySet()) {
            if (ARCHER_NAMES.contains(s) && dict.get(s) > 0) {
                return true;
            }
        }
        return false;
    }

    public Unit getWeakest() {

        Unit target = null;
        int minLevel = Integer.MAX_VALUE;
        for (String s : this.dict.keySet()) {

            if (dict.get(s) != 0) {
                Soldier check = (Soldier) this.getUnit(s);
                if (SHIELD_NAMES.contains(check.getJobName())) {
                    return check;
                }
                if (minLevel > check.getLevel()) {
                    minLevel = check.getLevel();
                    target = check;
                }
            }
        }
        return target;
    }

    public String getSummary() {
        StringBuilder report = new StringBuilder();
        report.append("Troop of " + this.owner.getName() + " with :\n");
        for (String s : this.dict.keySet()) {

            report.append(s + " : " + dict.get(s) + "\n");
        }
        for(int i=0;i<this.population.size();i++){

                if(this.checkIsShield(population.get(i))){
                    Shield s=(Shield) population.get(i);
                    report.append(s.getJobName()+"   LP: "+s.checkLP()+ "\n");
                }
        }
        return report.toString();
    }

    public int transfer(String from, String to, int unitLevel, int nUnit) {
        String typeBefore = buildJobName(from, unitLevel);

        if (this.checkUnitNum(typeBefore) < nUnit) {
            throw new IllegalArgumentException(String.format("Not enough %s to transfer", from));
        }

        int unitCost = 0;
        int totalcost = 0;
        if (to.equals(KNIGHT)) {
            unitCost = KNIGHT_COST;
        } else if (to.equals(ARCHER)) {
            unitCost = ARCHER_COST;
        } else if (to.equals(BREAKER)) {
            unitCost = BREAKER_COST;
        } else if (to.equals(SHIELD)) {
            unitCost = SHIELD_COST;
        }

        while (nUnit > 0) {
            Soldier target = (Soldier) this.dispatchCertainUnit(typeBefore);
            Soldier newTarget = target.transfer(to);
            this.receiveUnit(newTarget);
            totalcost += unitCost;
            nUnit--;
        }

        return totalcost;
    }

    public double checkSpeed() {
        for (String s : dict.keySet()) {
            if (KNIGHT_NAMES.contains(s) && dict.get(s) > 0) {
                return KNIGHT_SPEED;
            }
        }
        return NORM_SPEED;
    }

    public Troop sendOutRangedAttack(Troop target) {
        if (!this.hasRanged()) {
            throw new IllegalArgumentException("No ranged attack unit");
        }

        ArrayList<Unit> sub = new ArrayList<>();
        HashMap<String, Integer> subDict = target.getDict();
        HashMap<String, Integer> newDict = new HashMap<>();
        if (!this.checkSend(target)) {
            throw new IllegalArgumentException("Target sending troop has invalid size");
        }

        for (String s : subDict.keySet()) {

            if (ARCHER_NAMES.contains(s)) {
                int num = subDict.get(s);
                int numReady=0;
                int arrowLevel=ARCHER_NAMES.indexOf(s);
                for (int i = 0; i < this.population.size(); i++) {
                    if(population.get(i).getJobName().equals(s)){
                        Archer arc=(Archer)population.get(i);



                        numReady+=(arc.checkReady()?1:0);
                    }
                }
                if(numReady<num){
                    throw new IllegalArgumentException("No enough archer ready to shoot");
                }

                for (int i = 0; i < this.population.size(); i++) {
                    if(population.get(i).getJobName().equals(s)){
                        Archer arc=(Archer)population.get(i);
                        sub.add(arc.shoot());
                    }
                }
                newDict.put(ARROW_NAMES.get(arrowLevel), num);

//                for (int i = 0; i < num; i++) {
//
//                    Arrow t = new Arrow(arrowLevel);
//
//                    sub.add(t);
//
//                }

//
            } else {
                throw new IllegalArgumentException("Not using ranged attack unit");
            }

        }
        Troop r = new Troop(sub, newDict, this.owner);

        return r;
    }

    public void archerReady(){
        for (int i = 0; i < this.population.size(); i++) {
            if(ARCHER_NAMES.contains(population.get(i).getJobName())){
                Archer arc=(Archer)population.get(i);
                arc.active();
            }
        }
    }


    public Troop doOneCombat(Troop enemy) {



            Unit myUnit =this.getStrongest();
            Unit enemyUnit =enemy.getWeakest();
            boolean result=false;


            if(checkIsShield(enemyUnit)){
                System.out.println(enemyUnit.getJobName()+" defend "+myUnit.getJobName());
                result=enemyUnit.fight(myUnit);
            }else{
                System.out.println(myUnit.getJobName()+" defend "+enemyUnit.getJobName());
                result=myUnit.fight(enemyUnit);
            }


            if(result){
                if (ARROW_NAMES.contains(myUnit.getJobName())) {

                    this.loseUnit(myUnit);
                }

                if (SHIELD_NAMES.contains(enemyUnit.getJobName())) {
                    Shield s = (Shield) enemyUnit;
                    if (!s.shieldExist()) {
                        enemy.loseUnit(enemyUnit);
                    }
                }else{
                    enemy.loseUnit(enemyUnit);
                }



            }else {

                if (ARROW_NAMES.contains(enemyUnit.getJobName())) {
                    enemy.loseUnit(enemyUnit);

                }

                if (SHIELD_NAMES.contains(myUnit.getJobName())) {
                    Shield s = (Shield) myUnit;
                    if (!s.shieldExist()) {
                        this.loseUnit(myUnit);
                    }
                }else{
                    this.loseUnit(myUnit);
                }
            }

        return enemy;
    }

    boolean checkIsShield(Unit target){
        return SHIELD_NAMES.contains(target.getJobName());
    }

    public ArrayList<Unit> getPopulation(){
        return this.population;
    }

}