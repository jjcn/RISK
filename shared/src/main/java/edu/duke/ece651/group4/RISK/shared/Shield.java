package edu.duke.ece651.group4.RISK.shared;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class Shield extends Soldier {

    private int defendPoint;

    public Shield(int level) {
        this(new Random(),level);
    }

    public Shield(Random rand,int level) {
        super(rand);
        this.level=level;
        this.jobName=SHIELD_NAMES.get(level);
        this.defendPoint=SHIELD_HP;

    }

    @Override
    public void setLevel(int lv) {
        this.level = lv;
        this.jobName = SHIELD_NAMES.get(lv);
    }

    @Override
    public void setJob(String newJob) {
        this.jobName = newJob;
        this.level = SHIELD_NAMES.indexOf(newJob);
    }

    @Override
    public boolean fight(Unit enemy) {
        int myRoll = 0, enemyRoll = 0;
        while (myRoll == enemyRoll) {
            myRoll = this.attackPoint();
            enemyRoll = enemy.attackPoint();
        }
        if(this.shieldExist()){
            if(BREAKER_NAMES.contains(enemy.getJobName())){
                Breaker b=(Breaker)enemy;
                this.defend(enemyRoll+b.getBreakBonus());
            }else{
                this.defend(enemyRoll);
            }
        }




        return myRoll > enemyRoll;
    }


    public boolean shieldExist(){
        return this.defendPoint>0;
    }

    public void defend(int damage){
        this.defendPoint-=damage;
    }



    @Override
    public Shield clone() {
        Shield clone = new Shield(this.level);
        return clone;
    }
}
