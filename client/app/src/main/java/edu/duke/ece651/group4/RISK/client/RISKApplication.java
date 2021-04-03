package edu.duke.ece651.group4.RISK.client;

import android.app.Application;
import android.os.Message;
import android.util.Log;
import edu.duke.ece651.group4.RISK.shared.*;
import edu.duke.ece651.group4.RISK.shared.message.LogMessage;

import java.io.IOException;
import java.util.Random;

import static edu.duke.ece651.group4.RISK.shared.Constant.*;

public class RISKApplication extends Application {
    private static Client playerClient;
    private World theWorld;
    private int totalPopulation;
    private Random rnd;

    @Override
    public void onCreate() {
        System.out.println("Successfully create");
        Log.i("s", "Success");
        super.onCreate();
        new Thread( ()-> {
            try{
                playerClient = new Client("vcm-18527.vm.duke.edu",SOCKET_PORT);
            } catch (IOException e) {
                Log.e("s", "FAIL*******************");
                e.printStackTrace();
            }
        }
        ).start();
        this.theWorld=null;
        this.totalPopulation = 15;
        this.rnd=new Random();
    }

    protected static String sendAccountInfo(String name, String pwd, String actName) {
        LogMessage m= new LogMessage(name,pwd,actName);
        playerClient.sendObject(m);
        String response = (String) playerClient.recvObject();
        return response;
    }

    /*
     * This send SignIn info
     * @param name is username
     * @param pwd is the password
     * @return null if succeed, a error message if false
     * */
    public static String sendSignIn(String name,String pwd) {
        return sendAccountInfo(name, pwd, LOG_SIGNIN);
    }

    /*
    * This send SignUP info
    * @param name is username
    * @param pwd is the password
    * @return null if succeed, a error message if false
    * */
    public static String sendSignUp(String name,String pwd) {
        return sendAccountInfo(name, pwd, LOG_SIGNUP);
    }


}
