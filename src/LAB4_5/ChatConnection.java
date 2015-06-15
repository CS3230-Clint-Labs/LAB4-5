package LAB4_5;

import com.sun.corba.se.spi.activation.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Clinton on 6/12/2015.
 */
public class ChatConnection implements Runnable
{
    protected ChatWindow window;
    protected int port;
    protected String server;
    protected ServerSocket serverSocket;
    protected BufferedReader input;
    protected Socket incomingConnection;
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
                        e.printStackTrace();
                    }
                }
            }


        }catch (Exception e)
        {

        }
    }

    public void sendMessage(String message) throws IOException
    {
        try
        {
            this.sendUserMessage = new BufferedWriter(new OutputStreamWriter(this.incomingConnection.getOutputStream()));
            this.sendUserMessage.write(message);
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
            this.serverSocket.close();
        }catch(Exception e)
        {

        }
    }
}