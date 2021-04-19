package edu.duke.ece651.group4.RISK.shared;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class Knight extends Soldier {

    public Knight(int level) {
        this(new Random(),level);
}

    public Knight(Random rand,int level) {
        super(rand);
        this.level=level;
        this.jobName=KNIGHT_NAMES.get(level);
        this.speed=KNIGHT_SPEED;

    }


    @Override
    public void setLevel(int lv) {
        this.level = lv;
        this.jobName = KNIGHT_NAMES.get(lv);
    }

    @Override
    public void setJob(String newJob) {
        this.jobName = newJob;
        this.level = KNIGHT_NAMES.indexOf(newJob);
    }

    @Override
    public Knight clone() {
        Knight clone = new Knight(this.level);
        return clone;
    }


}
