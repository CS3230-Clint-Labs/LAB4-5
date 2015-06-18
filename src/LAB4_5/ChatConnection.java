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
    protected BufferedReader input;
    protected Socket incomingConnection;
    protected Socket OutgoingConnection;
    protected BufferedWriter sendUserMessage;


    public ChatConnection(String serverip, int portnum, ChatWindow win) throws IOException
    {
        this.window = win;
        this.port = portnum;
        this.server = serverip;
    }

    @Override
    public void run()
{
    try
    {
        this.serverSocket = new ServerSocket(this.port);

        //continuously listen for a incoming connection
        while(!this.serverSocket.isClosed())
        {
            this.incomingConnection = serverSocket.accept();

            this.input = new BufferedReader(new InputStreamReader(incomingConnection.getInputStream()));
            while(incomingConnection.isConnected() && !incomingConnection.isClosed())
            {
                try {
                    String incomingMessage = input.readLine();
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
        }


    }catch (Exception e)
    {
        //don't care
    }
}

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

    public void sendMessage(String message) throws IOException
    {
        try
        {
            this.sendUserMessage = new BufferedWriter(new OutputStreamWriter(this.incomingConnection.getOutputStream()));
            this.sendUserMessage.write(message);
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