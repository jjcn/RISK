package edu.duke.ece651.group4.RISK.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ResourceTest {
    
    @Test
    public void testGetName() {
        Resource food = new Resource("food", 1);
        Resource tech = new Resource("tech", 0);
        Resource empty = new Resource("", 0);
        assertEquals(food.getName(), "food");
        assertEquals(tech.getName(), "tech");
        assertEquals(empty.getName(), "");
    }

    @Test
    public void testGetQuantity() {
        Resource food = new Resource("food", 1);
        Resource tech = new Resource("tech", 0);
        Resource neg = new Resource("neg", -1);
        assertEquals(food.getQuantity(), 1);
        assertEquals(tech.getQuantity(), 0);
        assertEquals(neg.getQuantity(), -1);
    }

    @Test
    public void testSetQuantity() {
        Resource food = new Resource("food", 1);
        Resource tech = new Resource("tech", 0);
        food.setQuantity(99);
        tech.setQuantity(-1);
        assertEquals(food.getQuantity(), 99);
        assertEquals(tech.getQuantity(), -1);
    }

    @Test
    public void testModifyQuantity() {
        Resource food = new Resource("food", 1);
        Resource tech = new Resource("tech", 1);
        food.modifyQuantity(99);
        assertThrows(IllegalArgumentException.class,
                       () -> tech.modifyQuantity(-1));
        assertEquals(food.getQuantity(), 100);
        assertEquals(tech.getQuantity(), 1);
    }

    @Test
    public void testIsSameType() {
        Resource food1 = new Resource("food", 1);
        Resource food2 = new Resource("food", 0);
        Resource food3 = new Resource("food", 0);
        Resource tech1 = new Resource("tech", 1);
        Resource tech2 = new Resource("tech", 0);
        assertTrue(food1.isSameType(food2));
        assertTrue(food2.isSameType(food3));
        assertFalse(food1.isSameType(tech1));
        assertFalse(food1.isSameType(tech2));
        assertTrue(tech1.isSameType(tech2));
    }

    @Test
    public void testEquals() {
        Resource food1 = new Resource("food", 1);
        Resource food2 = new Resource("food", 0);
        Resource food3 = new Resource("food", 0);
        Resource tech1 = new Resource("tech", 1);
        Resource tech2 = new Resource("tech", 0);
        assertFalse(food1.equals(food2));
        assertTrue(food2.equals(food3));
        assertFalse(food1.equals(tech1));
        assertFalse(food1.equals(tech2));
        assertFalse(tech1.equals(tech2));
    }
    
    @Test
    public void testToString() {
    	Resource food0 = new FoodResource();
    	assertEquals("food: 0", food0.toString());
    	
    	Resource tech0 = new TechResource();
    	assertEquals("tech: 0", tech0.toString());
    	
    	Resource wood = new Resource("wood");
    	assertEquals("wood: 0", wood.toString());
    }
    
    @Test
    public void testHashCode() {
    	int hash1 = new Resource("wood").hashCode();
    	int hash2 = new Resource("wood", 1).hashCode();
    	int hash3 = new Resource("wool").hashCode();
    	
    	assertNotEquals(hash1, hash2);
    	assertNotEquals(hash1, hash3);
    }
}