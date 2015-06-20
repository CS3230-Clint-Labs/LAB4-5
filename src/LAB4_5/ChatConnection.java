package LAB4_5;

import com.sun.corba.se.spi.activation.Server;

import java.io.*;
import java.net.*;

/**
 * All
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


    /**
     * Allows the construction of a new 'ChatConnection' object.  This object can be treated as a Ser
     *
     * @param serverip - Used as the 'server' connection IP address.
     * @param portnum - Used as the sock for Server/Client to communicate on.
     * @param win - A new ChatWindow object used for exchanging messages.
     * @throws IOException
     */
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
//            this.serverSocket = new ServerSocket(this.port);
//            serverSocket = new ServerSocket(8080);
//
//            //continuously listen for a incoming connection
//              while(!this.serverSocket.isClosed())
//               {
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
            OutgoingConnection = new Socket(this.server,this.port); // Create a new connection on a server.
            this.input = new DataInputStream(OutgoingConnection.getInputStream()); // Receiving input from server.
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
        }catch(Exception e)
        {
            //doesn't matter
        }
        try
        {
            this.sendUserMessage.close();
        }catch(Exception e)
        {
            //don't care about this exception
        }
        try
        {
            this.OutgoingConnection.close();
        }catch(Exception e)
        {
            //don't care about this exception
        }
        try
        {
            this.incomingConnection.shutdownInput();
        }catch(Exception e)
        {
            //don't care about this exception
        }
        try
        {
            this.incomingConnection.shutdownOutput();
        }catch(Exception e)
        {
            //don't care about this exception
        }
        try
        {
            this.incomingConnection.close();
        }catch(Exception e)
        {
            //don't care about this exception
        }
        try
        {
            this.serverSocket.close();
        }catch(Exception e)
        {
            //don't care about this exception
        }
    }
}