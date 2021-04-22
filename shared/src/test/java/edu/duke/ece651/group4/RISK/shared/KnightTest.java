package edu.duke.ece651.group4.RISK.shared;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;
import static org.junit.jupiter.api.Assertions.*;

class KnightTest {
    @Test
    public void test_upgrade() {
        Random rnd = new Random(0);
        Knight test=new Knight(rnd,1);


        test.upGrade(3,1000);
        assertEquals(test.getJobName(),KNIGHT_NAMES.get(3));

        Knight clone=test.clone();
        assertEquals(clone.getJobName(),KNIGHT_NAMES.get(3));

        test.setJob(KNIGHT_NAMES.get(5));
        assertEquals(test.getJobName(),KNIGHT_NAMES.get(5));
    }
}