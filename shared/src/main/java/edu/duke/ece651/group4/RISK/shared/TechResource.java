package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

public class TechResource extends Resource implements Serializable {
    protected static final long serialVersionUID = 14L;
    
    public TechResource() {
        this(0);
    }

    public TechResource(int quantity) {
        super("tech", quantity);
    }
}
