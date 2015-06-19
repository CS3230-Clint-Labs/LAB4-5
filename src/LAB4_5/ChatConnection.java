package LAB4_5;

import com.sun.corba.se.spi.activation.Server;

import java.io.*;
import java.net.*;

/**
 * Authored by Clinton on 6/12/2015.
 * Edited by Tyler Cazier
 *
 */
public class ChatConnection implements Runnable
{
    protected ChatWindow window;
    protected int port;
    protected String server;
    protected ServerSocket serverSocket;
    protected DataInputStream input;
    protected Socket incomingConnection;
    protected Socket OutgoingConnection;
    protected DataOutputStream sendUserMessage;


    public ChatConnection(String serverip, int portnum, ChatWindow win) throws IOException
    {
        this.window = win;
        this.port = portnum;
        this.server = serverip;
    }


//    @Override
//    public void run()
//    {
//        try
//        {
////            this.serverSocket = new ServerSocket(this.port);
//            serverSocket = new ServerSocket(8080);
//
//            //continuously listen for a incoming connection
////            while(!this.serverSocket.isClosed())
////            {
//                this.incomingConnection = serverSocket.accept();
//
//                this.input = new DataInputStream(incomingConnection.getInputStream());
//            this.sendUserMessage = new DataOutputStream(incomingConnection.getOutputStream());
//                while(incomingConnection.isConnected() && !incomingConnection.isClosed())
//                {
//                    try {
//                        String incomingMessage = input.readUTF();
//                        if (incomingMessage.equals("exit"))
//                        {
//                            incomingConnection.close();
//                            break;
//                        }
//                        if (!incomingMessage.equals(null))
//                        {
//                            window.addServerText(incomingMessage);
//                        }
//                    } catch (NullPointerException e)
//                    {
//                        incomingConnection.close();
//                    }
//                }
////            }
//
//
//        }catch (Exception e)
//        {
//            //don't care
//        }
//    }


    @Override
    public void run()
    {

        try
        {
//            OutgoingConnection = new Socket(this.server, this.port);
            OutgoingConnection = new Socket(this.server,this.port);
            this.input = new DataInputStream(OutgoingConnection.getInputStream());
            this.sendUserMessage = new DataOutputStream(OutgoingConnection.getOutputStream());
            while(OutgoingConnection.isConnected() && !OutgoingConnection.isClosed())
            {
                try
                {
                    String incomingMessage = input.readUTF();
                    if (incomingMessage.equals("exit"))
                    {
                        OutgoingConnection.close();
                        break;
                    }
                    if (!incomingMessage.equals(null))
                    {
                        window.addServerText(incomingMessage);
                    }
                }catch (NullPointerException e)
                {
                    OutgoingConnection.close();
                }
            }
        }
        catch (Exception e)
        {
            //don't care
        }
    }

    /*
    public void runConnection()
    {
        try
        {
            this.OutgoingConnection = new Socket();
            OutgoingConnection.connect(new InetSocketAddress(this.server, this.port), 10000);

        }catch (Exception e)
        {
            System.out.println(e.getStackTrace());
        }

    }
    */

    public void sendMessage(String message) throws IOException
    {
        try
        {
            this.sendUserMessage.writeUTF(message);
            this.sendUserMessage.flush();
        }catch(IOException e)
        {
            throw e;
        }

    }

    public void closeConnection()
    {
        try
        {
            this.input.close();
            this.sendUserMessage.close();
            this.OutgoingConnection.close();
            this.incomingConnection.shutdownInput();
            this.incomingConnection.shutdownOutput();
            this.incomingConnection.close();
            this.serverSocket.close();
        }catch(Exception e)
        {
            //don't care about this exception
        }
    }
}