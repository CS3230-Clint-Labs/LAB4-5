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
    protected ServerSocket serverSocket;
    protected DataInputStream input;
    protected Socket incomingConnection;
    protected Socket OutgoingConnection;
    protected DataOutputStream sendUserMessage;


    public ChatConnection(int portnum, ChatWindow win) throws IOException
    {
        this.window = win;
        this.port = portnum;
    }


    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(this.port);

            this.incomingConnection = serverSocket.accept();
            this.input = new DataInputStream(incomingConnection.getInputStream());
            this.sendUserMessage = new DataOutputStream(incomingConnection.getOutputStream());
            while(incomingConnection.isConnected() && !incomingConnection.isClosed())
            {
                try {
                    String incomingMessage = input.readUTF();
                    if (incomingMessage.equals("exit"))
                    {
                            incomingConnection.close();
                            break;
                    }
                    if (!incomingMessage.equals(null))
                    {
                            window.addServerText(incomingMessage);
                    }
                } catch (NullPointerException e)
                {
                        incomingConnection.close();
                }
            }
        }catch (Exception e)
        {
            //don't care
        }
    }


//    @Override
//    public void run()
//    {
//
//        try
//        {
////            OutgoingConnection = new Socket(this.server, this.port);
//            OutgoingConnection = new Socket(this.server,this.port);
//            this.input = new DataInputStream(OutgoingConnection.getInputStream());
//            this.sendUserMessage = new DataOutputStream(OutgoingConnection.getOutputStream());
//            while(OutgoingConnection.isConnected() && !OutgoingConnection.isClosed())
//            {
//                try
//                {
//                    String incomingMessage = input.readUTF();
//                    if (incomingMessage.equals("exit"))
//                    {
//                        OutgoingConnection.close();
//                        break;
//                    }
//                    if (!incomingMessage.equals(null))
//                    {
//                        window.addServerText(incomingMessage);
//                    }
//                }catch (NullPointerException e)
//                {
//                    OutgoingConnection.close();
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            //don't care
//        }
//    }


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