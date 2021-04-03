package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Soldier implements Unit, Serializable {

    private String jobName;
    private int level;
    private final List<String> levelNames = Arrays.asList("Soldier LV0","Soldier LV1",
            "Soldier LV2","Soldier LV3","Soldier LV4","Soldier LV5","Soldier LV6");
    private final List<Integer> levelCost = Arrays.asList(0,3,11,30,55,90,140);
    private final List<Integer> levelBonus = Arrays.asList(0,1,3,5,8,11,15);
    private final Random dice;

    public Soldier() {
        this(new Random());
    }

    /**
     * Construct a soldier with specific seed
     * @param rand is the random seed.
     */
    public Soldier(Random rand) {
        this.jobName = this.levelNames.get(0);
        this.level = 0;
        this.dice = rand;
    }

    public Soldier clone(){
        Soldier clone = new Soldier();
        int lv = this.level;
        clone.setLevel(lv);
        return clone;
    }

    public String getJobName(){
        return this.jobName;
    }

    public int getLevel(){
        return this.level;
    }

    public ArrayList<String> getLevelNames(){
        return (ArrayList<String>)this.levelNames;
    }

    public int getBonus(){
        return this.levelBonus.get(this.level);
    }

    public void setLevel(int lv){
        this.level = lv;
        this.jobName = levelNames.get(lv);
    }

    public void setJob(String newJob){
        this.jobName = newJob;
        this.level = this.levelNames.indexOf(newJob);
    }

    /**
     * Soldier fight with another unit by rolling a 20faced dice
     * @param enemy is the enemy unit.
     */
    @Override
    public boolean fight(Unit enemy) {
        int myRoll = 0, enemyRoll = 0;

        while (myRoll == enemyRoll) {
            myRoll = this.attackPoint();
            enemyRoll = enemy.attackPoint();
        }

        return myRoll > enemyRoll;
    }

    @Override
    public int upGrade(int targetLevel, int resource) {
        if (targetLevel < this.level) {
            throw new IllegalArgumentException("target level is lower than current level");
        }
        if (targetLevel >= this.levelNames.size()) {
            throw new IllegalArgumentException("Target level exceed max level");
        }
        /*
        while (targetLevel > this.level) {
            cost += this.levelCost.get(targetLevel);
            targetLevel--;
        }
        */
        int cost = this.levelCost.get(targetLevel) - this.levelCost.get(this.level);
        if (cost > resource) {
            throw new IllegalArgumentException("No enough resources");
        }
        this.setLevel(targetLevel);
        return resource - cost;
    }


    public int attackPoint(){
        return this.randInt(0,20)+this.getBonus();
    }


    /**
     *return a random number
    */
    private int randInt(int min, int max) {
        int randomNum = dice.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
