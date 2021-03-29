package edu.duke.ece651.group4.RISK.shared;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;


public class Troop implements Serializable {

    private final ArrayList<Unit> population;

    private Player owner;

    public Troop(int number, Player owner, Random rand) {
        this.owner = owner;
        this.population = new ArrayList<>();
        this.owner=new TextPlayer(owner.getName());
        for (int i = 0; i < number; i++) {
            population.add(new Soldier(rand));
        }
    }

    public Troop(int number, Player owner) {
        this.owner = owner;
        this.population = new ArrayList<>();
        this.owner=new TextPlayer(owner.getName());
        for (int i = 0; i < number; i++) {
            population.add(new Soldier(new Random()));
        }
    }

    public Troop(ArrayList<Unit> subTroop, Player owner){
        this.population = subTroop;
        this.owner = owner;
    }

    /**
     * Do battle between two troops
     * @param enemy shows the enemy troop attack in
     */
    public Troop combat(Troop enemy) {
        while (this.checkTroopSize() != 0 && enemy.checkTroopSize() != 0) {
            Unit myUnit = this.dispatchUnit();
            Unit enemyUnit = enemy.dispatchUnit();

            if (myUnit.fight(enemyUnit)) {
                enemy.loseUnit(enemyUnit);
            }
            else {
                this.loseUnit(myUnit);
            }
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
    private void loseUnit(Unit loss) {
        this.population.remove(loss);
    }

    /**
     * send a unit from troop
     */
    public Unit dispatchUnit() {
        return this.population.get(0);
    }

    /**
     * receive a specific unit
     */
    public void receiveUnit(Unit target) {
        this.population.add(target);
    }

    /**
     * Send out specific number of troops
     */
    public Troop sendTroop(int number){
        ArrayList<Unit> sub = new ArrayList<>();
        for(int i = 0; i < number; i++){
            Unit movedUnit = this.dispatchUnit();
            sub.add(movedUnit);
            this.loseUnit(movedUnit);
        }
        return new Troop(sub, getOwner());
    }

    /**
     * Check if troop win the fight
     */
    public boolean checkWin(){
        return this.checkTroopSize()>0;
    }

    /**
     * receive specific number of troops
     */
    public void receiveTroop(Troop subTroop){
        while(subTroop.checkTroopSize() != 0){
            Unit newMember=subTroop.dispatchUnit();
            this.receiveUnit(newMember);
            subTroop.loseUnit(newMember);
        }
    }

    /**
     * return owner of troop
     */
    public Player getOwner(){

        return this.owner;
    }

    public Troop clone(){
        ArrayList<Unit> cloneList = new ArrayList<Unit>(population.size());
        for (Unit item : population) cloneList.add(item.clone());
        Troop clone=new Troop(cloneList,new TextPlayer(new String(this.owner.getName())));

        return clone;
    }

}

