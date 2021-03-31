package edu.duke.ece651.group4.RISK.client;

import android.app.Application;
import android.content.SharedPreferences;

import java.net.Socket;

public class RISKApplication extends Application {
    private Socket mySocket;

    @Override
    public void onCreate() {
        super.onCreate();
        mySocket = null;
    }

}
