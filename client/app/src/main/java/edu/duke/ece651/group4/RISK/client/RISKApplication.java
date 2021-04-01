package edu.duke.ece651.group4.RISK.client;

import android.app.Application;
import android.os.Message;
import edu.duke.ece651.group4.RISK.shared.*;

import java.io.IOException;
import java.util.Random;

public class RISKApplication extends Application {
    private static Client playerClient;
    private World theWorld;
    private int totalPopulation;
    private Random rnd;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            this.playerClient=new Client("localhost", "9999");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.theWorld=null;
        this.totalPopulation = 15;
        this.rnd=new Random();
    }

    public static String sendAccountInfo(String name, String pwd, String actName) throws IOException, ClassNotFoundException {
        Message m= new Message();
        playerClient.sendObject(m);
        String response = (String) playerClient.recvObject();
        return response;
    }

    public static String sendSignIn(String name,String pwd) {
        try {
            return sendAccountInfo(name, pwd, "signIn");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
