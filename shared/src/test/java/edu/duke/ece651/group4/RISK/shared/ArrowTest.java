package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.ARROW_NAMES;
import static edu.duke.ece651.group4.RISK.shared.Constant.BREAKER_NAMES;
import static org.junit.jupiter.api.Assertions.*;

class ArrowTest {
    @Test
    public void test_upgrade() {
        Random rnd = new Random(0);
        Arrow test=new Arrow(rnd,1);


        test.upGrade(3,1000);
        assertEquals(test.getJobName(),ARROW_NAMES.get(3));

        Arrow clone=test.clone();
        assertEquals(clone.getJobName(),ARROW_NAMES.get(3));

        test.setJob(ARROW_NAMES.get(5));
        assertEquals(test.getJobName(),ARROW_NAMES.get(5));
    }
}