package edu.duke.ece651.group4.RISK.shared;

/**
 * A of resource
 */
public class Resource {
    String name;
    int quantity;

    public Resource() {
        this(null, 0);
    }

    public Resource(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other.getClass().equals(getClass())) {
            Resource otherResource = (Resource)other;
            return otherResource.name.equals(name) &&
                   otherResource.quantity == quantity;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name + quantity;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
