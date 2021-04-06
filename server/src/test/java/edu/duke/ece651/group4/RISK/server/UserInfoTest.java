package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserInfoTest {

    @Test
    public void test_basicFunctions(){
        UserInfo userInfo = new UserInfo(1,"user0","123");
        assertEquals("user0",userInfo.getUsername());
        assertEquals("123",userInfo.getPassword());
        assertEquals(1,userInfo.getUserID());
    }

}