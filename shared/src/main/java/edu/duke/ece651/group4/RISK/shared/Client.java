package edu.duke.ece651.group4.RISK.shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/*
* This Class combine Socket with the outputStream and InputStream.
* It will make it easier for player to send object to server or server send anything to client
* Warning: You need to create the ObjectOutputStream before the ObjectInputStream,
* at both ends, for reasons described in the Javadoc concerning the object stream header.
*/
public class Client  {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    /*
    * This construct a client with socket s that is output from serverSocket.accept()
    * You will use this constructor for the server part
    * @param s is the connected socket returned by serverSocket
    * */
    public Client(Socket s) throws IOException {
        this.socket = s;
        setUpObjectStream();
    }

    /*
    *  This construct a client with a hostname and port.
    *  You will use this constructor for the client Player part
    *  @param hostName is the server hostname
    *  @param port is the server port
    */
    public Client(String hostName, String port) throws IOException {
        InetAddress host = InetAddress.getByName(hostName);
        this.socket = new Socket(host.getHostAddress(), Integer.parseInt(port));
        setUpObjectStream();
    }

    /*
    * This sets up the ObjectOutputStream and ObjectInputStream
    * */
    private void setUpObjectStream() throws IOException {
        out = new ObjectOutputStream(this.socket.getOutputStream());
        in = new ObjectInputStream(this.socket.getInputStream());
    }


    /*
    * This send an object to a connected remote socket
    * @param o is the object to send
    * */
    public void sendObject(Object o) throws IOException {
        out.writeObject(o);
        out.flush();
    }

    /*
    * This recv an object from a connected remote socket
    * @return is to receive an object sent by the remote connected socket
    * */
    public Object recvObject() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    /*
    * This is to close the socket and all related streams
    * If you do out.close. It will automatically close socket,
    * ObjectInputStream and ObjectOutputStream
    * */
    public void close(){
        try{
            out.close();
        }catch (IOException e) {
        }
    }

}
