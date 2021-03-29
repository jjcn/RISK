package edu.duke.ece651.group4.RISK.shared;

public interface Unit {

    public int upGrade(int targetLevel,int resource);

    public boolean fight(Unit enemy);

    public Unit clone();

    public int attackPoint();
}

