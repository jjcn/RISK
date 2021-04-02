package edu.duke.ece651.group4.RISK.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game g;
    private Game createAGame(int gid, int numUser){
        Game g = new Game(gid,numUser);
        for(int i = 0; i < numUser; i++){
            User u = new User(i,null,null);
            g.addUser(u);
        }
        return g;
    }

    @Test
    public void test_barrier(){
        this.g = createAGame(1,3);
        new Thread(() -> {
            System.out.println("wait for others");
            this.g.barrierWait();
            System.out.println("Done wait");
        }).start();
        new Thread(() -> {
            System.out.println("wait for others");
            this.g.barrierWait();
            System.out.println("Done wait");
        }).start();
        new Thread(() -> {
            System.out.println("wait for others");
            this.g.barrierWait();
            System.out.println("Done wait");
        }).start();
    }

    @Test
    public void test_latch(){

    }
}