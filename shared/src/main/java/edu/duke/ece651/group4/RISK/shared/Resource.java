package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

/**
 * Template of a generic resource.
 */
public class Resource implements Serializable {
    final String NEG_MSG = 
        "Error: The quantity of %s resource will be negative after this action.";

    private String name;
    private int quantity;

    public Resource(String name) {
        this(name, 0);
    }

    public Resource(String name, int quantity) {
        checkNonNegative(quantity);
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int i) {
        checkNonNegative(i);
        quantity = i;
    }

    /**
     * change the quantity of a resource.
     * @param i is the number to add to resource quantity. 
     *          Can be positive, 0, or negative.
     */
    public void modifyQuantity(int i) {      
        checkNonNegative(quantity + i);
        quantity += i;
    }

    /**
     * Check the non-negativity of resource quantity.
     * @param i is the resource quantity to check.
     */
    protected void checkNonNegative(int i) {
        if (i < 0) {
            throw new IllegalArgumentException(String.format(NEG_MSG, name));
        }
    }
  
    /**
     * Check if the resources are of the same type.
     * @return true, if the resources have the same name;
     *         false, if not.
     */
    public boolean isSameType(Resource otherResource) {
        return otherResource.getName().equals(name);
    }
  
    @Override
    public boolean equals(Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            Resource otherResource = (Resource)other;
            return otherResource.getName().equals(name) &&
                   otherResource.getQuantity() == quantity;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name + ": " + quantity;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
