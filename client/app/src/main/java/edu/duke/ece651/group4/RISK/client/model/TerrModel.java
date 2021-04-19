package edu.duke.ece651.group4.RISK.client.model;

import edu.duke.ece651.group4.RISK.shared.Territory;

public class TerrModel {
    private Territory territory;

    public TerrModel(Territory terr){
        this.territory = terr;
    }

    public String getTerrName(){
        return territory.getName();
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }
}