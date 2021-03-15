/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.duke.ece651.group4.RISK.client;


import edu.duke.ece651.group4.RISK.shared.*;

import java.io.*;
import java.net.Socket;

public class PlayerApp {
    private Client playerClient;
    private TextPlayer myPlayer;
    private World theWorld;

    public PlayerApp(Client myClient,String name,PrintStream out, Reader inputReader,World theWorld) {
        this.playerClient=myClient;
        this.myPlayer=new TextPlayer(out,inputReader, name);
        this.theWorld=theWorld;
    }

    public TextPlayer getMyPlayer() {
        return myPlayer;
    }

    public void doPlacementPhase() throws IOException{


    }

    public void doActionPhase() throws IOException{
        boolean turnEnd=false;

        while(!turnEnd){
            boolean received=false;
            Order receiveMessage = null;
            while(!received) {

                try {
                    receiveMessage=this.myPlayer.doOneAction();
                    if (receiveMessage.getActionName() == 'A') {
                        this.theWorld.moveTroop(theWorld.findTerritory(receiveMessage.getSrcName()), receiveMessage.getActTroop(), theWorld.findTerritory(receiveMessage.getDesName()));
                    } else if (receiveMessage.getActionName() == 'M') {
                        this.theWorld.attackATerritory(theWorld.findTerritory(receiveMessage.getSrcName()), receiveMessage.getActTroop(), theWorld.findTerritory(receiveMessage.getDesName()));
                    } else {
                        turnEnd = true;
                    }
                    received=true;
                }catch(Exception e) {
                    System.out.println("Please enter correct order!");
                }
            }
           sendInfo(receiveMessage,this.playerClient);
        }

        World newWorld=null;
        this.theWorld=(World) receiveInfo(newWorld,this.playerClient);
    }





    public static void main(String[] args) {
        String instruct1 = "Please enter the hostName";
        String instruct2 = "Please enter the port";
        BufferedReader inRead=new BufferedReader(new InputStreamReader(System.in));

        Client myClient=null;
        boolean setConnect = false;

        while (!setConnect) {
            try {
                System.out.println(instruct1);
                String hostName = inRead.readLine();
                System.out.println(instruct2);
                String port = inRead.readLine();
                myClient = new Client(hostName, port);
                setConnect = true;
            } catch (Exception e) {
                System.out.println("Please enter correct host information!");

            }
        }

        String name=null;
        World gameWorld=null;
        name=(String) receiveInfo(name,myClient);
        gameWorld=(World) receiveInfo(gameWorld,myClient);
//        while(name==null){
//            try{
//                name=(String) myClient.recvObject();
//
//            }catch(Exception e){
//                System.out.println("Socket name problem!");
//            }
//        }

        PlayerApp myApp=new PlayerApp(myClient,name,System.out,inRead,gameWorld);




    }

    public static Object receiveInfo(Object o, Client c){
        while(o==null) {

            try {
                o = c.recvObject();
            } catch (Exception e) {
                System.out.println("Socket name problem!");
            }
        }
        return o;
    }

    public static void sendInfo(Object o, Client c){
        while(o==null) {

            try {
                c.sendObject(o);
            } catch (Exception e) {
                System.out.println("Socket problem!");
            }
        }

    }

    }

