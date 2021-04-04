package edu.duke.ece651.group4.RISK.shared;

/**
 * Template of a generic resource.
 */
public class Resource {
    private String name;
    private int quantity;

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

    public void setQuantity(int i) {
        quantity = i;
    }

    /*
     * Change the quantity of a resource.
     * @param resource is the enum type of the resource.
     * @param i is the number to add to resource quantity. 
     *          Can be positive, 0, or negative.
     */
    public void modifyQuantity(int i) {
        final String RESOURCE_INVALID_MODIFY_MSG = 
            "Modifying resource quantity by %s is not supported.";
        if (0 < quantity + i) {
            quantity += i;
        }
        else {
            throw new IllegalArgumentException(
                String.format(RESOURCE_INVALID_MODIFY_MSG, i));
        }
    }

    /**
     * Check if the resources are of the same type.
     * @return true, if the resources have the same name;
     *         false, if not.
     */
    public boolean equalsName(Resource otherResource) {
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
        return name + quantity;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
