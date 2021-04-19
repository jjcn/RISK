package edu.duke.ece651.group4.RISK.shared;

import java.io.Serializable;

/**
 * A resource, can be gained or consumed.
 */
public class Resource implements Serializable {
    /**
     * Auto-generated serial version UID
     */
    protected static final long serialVersionUID = 10L;
    /**
     * Name of the resource.
     */
    private String name;
    /**
     * Integer quantity of the resource.
     * Should be non-negative.
     */
    private int quantity;

    /**
     * Error mesages
     */
    final String NEG_MSG = 
        "Error: %s resource's gain and consume input value shall be non-negative.";

    final String NEG_AFTER_CONSUME_MSG =
        "Error: Not enough %s resource to consume.";
       

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

    /**
     * A resource is gained by a number N.
     * @param gain is the number added to resource.
     *              should be a non-negative number.
     */
    public void gain(int gain) {
        checkNonNegative(gain);
        quantity += gain;
    }

    /**
     * Check if a resource can be consumed by a number N.
     * @param consumption is the quantity of resource consumed.
     */
    public void checkConsume(int consumption) throws IllegalArgumentException {
        checkNonNegative(consumption);
        if (quantity - consumption < 0) {
            throw new IllegalArgumentException(
                    String.format(NEG_AFTER_CONSUME_MSG, name));
        }
    }

    /**
     * Try consume a resource by a number N.
     * @param consumption is the quantity of resource consumed.
     */
    public void consume(int consumption) {
        checkConsume(consumption);
        quantity -= consumption;
    }

    /**
     * Check non-negativity of the resource's quantity.
     * @param i is the resource quantity to check.
     */
    protected void checkNonNegative(int i) {
        if (i < 0) {
            throw new IllegalArgumentException(String.format(NEG_MSG, name));
        }
    }
  
    /**
     * Check if two resources are of the same type.
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
