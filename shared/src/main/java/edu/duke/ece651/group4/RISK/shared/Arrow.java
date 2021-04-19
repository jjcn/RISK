package edu.duke.ece651.group4.RISK.shared;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static edu.duke.ece651.group4.RISK.shared.Constant.BREAKER_NAMES;

public class Arrow extends Soldier {


    public Arrow(int level) {
        this(new Random(),level);
    }

    public Arrow(Random rand,int level) {
        super(rand);
        this.level=level;
        this.jobName=ARROW_NAMES.get(level);

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



    @Override
    public Arrow clone() {
        Arrow clone = new Arrow(this.level);
        return clone;
    }
}
