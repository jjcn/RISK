package edu.duke.ece651.group4.RISK.shared;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class Breaker extends Soldier {

    private final int breakBonus;

    public Breaker(int level) {
        this(new Random(), level);
    }

    public Breaker(Random rand, int level) {
        super(rand);
        this.level = level;
        this.jobName = BREAKER_NAMES.get(level);
        this.range = ARCHER_RANGE;
        this.breakBonus = BREAKER_BONUS;
    }

    @Override
    public void setLevel(int lv) {
        this.level = lv;
        this.jobName = BREAKER_NAMES.get(lv);
    }

    @Override
    public void setJob(String newJob) {
        this.jobName = newJob;
        this.level = BREAKER_NAMES.indexOf(newJob);
    }

    public int getBreakBonus() {
        return this.breakBonus;
    }


    @Override
    public Breaker clone() {
        Breaker clone = new Breaker(this.level);
        return clone;
    }
}
