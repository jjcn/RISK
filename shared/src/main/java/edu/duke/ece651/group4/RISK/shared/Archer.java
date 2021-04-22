package edu.duke.ece651.group4.RISK.shared;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class Archer extends Soldier {

    private boolean ready;

    public Archer(int level) {
        this(new Random(), level);
    }

    public Archer(Random rand, int level) {
        super(rand);
        this.level=level;
        this.jobName=ARCHER_NAMES.get(level);
        this.range=ARCHER_RANGE;
        this.ready=true;

    }

    @Override
    public void setLevel(int lv) {
        this.level = lv;
        this.jobName = ARCHER_NAMES.get(lv);
    }

    @Override
    public void setJob(String newJob) {
        this.jobName = newJob;
        this.level = ARCHER_NAMES.indexOf(newJob);
    }

    public void active(){
        this.ready=true;
    }

    public boolean checkReady(){
        return this.ready;
    }



    public Arrow shoot(){

        this.ready=false;
        Arrow t = new Arrow(this.level);
        return t;
    }


    @Override
    public Archer clone() {
        Archer clone = new Archer(this.level);
        if(this.ready){
            clone.active();
        }else{
            clone.shoot();
        }
        return clone;
    }
}
