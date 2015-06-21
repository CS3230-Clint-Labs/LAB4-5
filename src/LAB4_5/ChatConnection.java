package LAB4_5;

import java.io.*;
import java.net.*;

/**
 *
 *
 * @author Clinton Fowler
 * @author Tyler Cazier
 * @version 6/20/15
 */
public class ChatConnection implements Runnable
{
    private String serverIP;
    private int portNum;
    private ChatWindow window;
    private ServerSocket serverSocket;
    private Socket incomingConnection;
    private Socket outgoingConnection;
    private DataInputStream incomingData;
    private DataOutputStream outgoingData;


    /**
     * Allows the construction of a new 'ChatConnection' object.  This object can be treated as a Ser
     *
     * @param serverIP - Used as the 'serverIP' connection IP address.
     * @param portNum - Used as the pipe for server/client communication.
     * @param window - A new ChatWindow object used for exchanging messages.
     * @throws IOException
     */
    public ChatConnection(String serverIP, int portNum, ChatWindow window) throws IOException
    {
        this.serverIP = serverIP; // IP address of the server that listens for the connection.
        this.portNum = portNum; // Port number socket connections.
        this.window = window; // The chat window message exchange.
    }


//    @Override
//    public void run()
//    {
//        try
//        {
//            this.serverSocket = new ServerSocket(this.portNum);
//            serverSocket = new ServerSocket(8080);
//
//            //continuously listen for a incoming connection
//              while(!this.serverSocket.isClosed())
//               {
//                this.incomingConnection = serverSocket.accept();
//
//                this.incomingData = new DataInputStream(incomingConnection.getInputStream());
//            this.outgoingData = new DataOutputStream(incomingConnection.getOutputStream());
//                while(incomingConnection.isConnected() && !incomingConnection.isClosed())
//                {
//                    try {
//                        String incomingMessage = incomingData.readUTF();
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
            outgoingConnection = new Socket(this.serverIP,this.portNum); // Create a new connection on a serverIP.
            this.incomingData = new DataInputStream(outgoingConnection.getInputStream()); // Receiving incomingData from serverIP.
            this.outgoingData = new DataOutputStream(outgoingConnection.getOutputStream());
            while(outgoingConnection.isConnected() && !outgoingConnection.isClosed())
            {
                try
                {
                    String incomingMessage = incomingData.readUTF();
                    if (incomingMessage.equals("exit"))
                    {
                        outgoingConnection.close();
                        break;
                    }
                    if (!incomingMessage.equals(null))
                    {
                        window.addServerText(incomingMessage);
                    }
                }catch (NullPointerException e)
                {
                    outgoingConnection.close();
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
            this.outgoingConnection = new Socket();
            outgoingConnection.connect(new InetSocketAddress(this.serverIP, this.portNum), 10000);
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
            this.outgoingData.writeUTF(message);
            this.outgoingData.flush();
        }catch(IOException e)
        {
            throw e;
        }

    }

    public void closeConnection()
    {
        try
        {
            this.incomingData.close();
        }catch(Exception e)
        {
            //doesn't matter
        }
        try
        {
            this.outgoingData.close();
        }catch(Exception e)
        {
            //don't care about this exception
        }
        try
        {
            this.outgoingConnection.close();
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