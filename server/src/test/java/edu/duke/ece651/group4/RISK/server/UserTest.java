package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    public void test_basicFunctions(){
        User u = new User(1,"user0","1");
        assertEquals("user0", u.getUsername());
        User u0 = new User(new UserInfo(0,"user0","1"));
        assertEquals(true,u0.checkUsername("user0"));
        assertEquals(false,u0.checkUsername("user1"));
        assertEquals(false,u0.checkUsernamePassword("user1", "1"));
        assertEquals(false,u0.checkUsernamePassword(null, "1"));
        assertEquals(false,u0.checkUsernamePassword("user0", "123"));
        assertEquals(true,u0.checkUsernamePassword("user0", "1"));
    }
}