package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class Soldier implements Unit, Serializable {
    protected static final long serialVersionUID = 13L;

    protected String jobName;
    protected int level;
    protected final Random dice;
    protected double speed;
    protected int range;

    public Soldier() {
        this(new Random());
    }

    /**
     * Construct a soldier with specific seed
     *
     * @param rand is the random seed.
     */
    public Soldier(Random rand) {
        this.jobName = UNIT_NAMES.get(0);
        this.level = 0;
        this.dice = rand;
        this.speed = NORM_SPEED;
        this.range = NORM_RANGE;
    }

    public Soldier clone() {
        Soldier clone = new Soldier();
        int lv = this.level;
        clone.setLevel(lv);
        return clone;
    }

    public String getJobName() {
        return this.jobName;
    }

    public int getLevel() {
        return this.level;
    }

    public int getBonus() {
        return UNIT_BONUS.get(this.level);
    }

    public void setLevel(int lv) {
        this.level = lv;
        this.jobName = UNIT_NAMES.get(lv);
    }

    public void setJob(String newJob) {
        this.jobName = newJob;
        this.level = UNIT_NAMES.indexOf(newJob);
    }

    /**
     * Soldier fight with another unit by rolling a 20faced dice
     *
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

    /**
     * Try upgrade a soldier to target Level.
     *
     * @param targetLevel is the desired level to upgrade to.
     * @param resource    is the resource quantity at hand.
     * @return remaining resource quantity after the upgrade attempt.
     */
    @Override
    public int upGrade(int targetLevel, int resource) {
        int cost = 0;
        int aim = targetLevel;
        if (targetLevel < this.level) {
            throw new IllegalArgumentException("target level is lower than current level");
        }

        if (targetLevel >= UNIT_COSTS.size()) {
            throw new IllegalArgumentException("Target level exceed max level");
        }

        cost = UNIT_COSTS.get(targetLevel) - UNIT_COSTS.get(this.level);
        if (cost > resource) {
            throw new IllegalArgumentException("No enough resources");
        }
        this.setLevel(aim);
        return resource - cost;

    }

    public int attackPoint() {
        return this.randInt(0, 20) + this.getBonus();
    }

    /**
     * return a random number
     */
    private int randInt(int min, int max) {
        int randomNum = dice.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public double getSpeed() {
        return this.speed;
    }

    public Soldier transfer(String newJob) {
        if (!JOB_NAMES.contains(newJob)) {
            throw new IllegalArgumentException(
                    String.format("Fails to transfer job. Job \"%s\" does not exist.", newJob));
        }

        if (newJob.equals(KNIGHT)) {
            return new Knight(this.level);
        } else if (newJob.equals(ARCHER)) {
            return new Archer(this.level);
        } else if (newJob.equals(BREAKER)) {
            return new Breaker(this.level);
        } else if (newJob.equals(SHIELD)) {
            return new Shield(this.level);
        }

        return null;
    }

}
