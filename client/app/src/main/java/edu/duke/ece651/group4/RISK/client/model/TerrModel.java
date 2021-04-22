package edu.duke.ece651.group4.RISK.client.model;

import edu.duke.ece651.group4.RISK.shared.Territory;

import java.util.ArrayList;
import java.util.List;

import static edu.duke.ece651.group4.RISK.shared.Constant.JOB_NAMES;

// change to adapter
public class TerrModel {
    private Territory territory;

    public TerrModel(Territory terr) {
        this.territory = terr;
    }

    public String getTerrName() {
        return territory.getName();
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public String getSoldierInfo() {
        return "Soldier";
    }

    public List<String> getTypeInfo() {
        List<String> typeInfo = new ArrayList<>();
        for(String job:JOB_NAMES) {
            String typeDetail = job;
           // for(String territory.checkTypeNum(job);
        }
        return typeInfo;
    }
}