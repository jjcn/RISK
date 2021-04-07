package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomInfoTest {

    @Test
    public void testBasic() {
        List<String> userNames=new ArrayList<>();
        userNames.add("1");
        userNames.add("2");
        RoomInfo test=new  RoomInfo(1, userNames, 2);
        assertEquals(test.getRoomID(),1);
        assertEquals(test.getMaxNumPlayers(),2);
        assertEquals(test.getUserNames(),userNames);
    }

}