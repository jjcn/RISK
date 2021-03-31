package edu.duke.ece651.group4.RISK.client;
import java.util.Random;

import android.app.Application;
import android.content.SharedPreferences;

import java.net.Socket;

public class RISKApplication extends Application {
    private Client playerClient;
    private World theWorld;
    private final int totalPopulation;
    private Random rnd;

    @Override
    public void onCreate() {
        super.onCreate();
        this.playerClient=new Client("localhost", "9999");
        this.theWorld=null;
        this.totalPopulation=15;
        this.rnd=new Random();
    }

    public String sendAccountInfo(String name,String pwd,String actName) throws IOException, ClassNotFoundException {
//        Message m= new Message(actName,name,pwd);
        this.playerClient.sendObject(m);
        String response = (String) this.playerClient.recvObject();
        return response;
    }

    public String sendSignIn(String name,String pwd) throws IOException, ClassNotFoundException{
        return sendAccountInfo(name,pwd,"signIn");
    }

}
