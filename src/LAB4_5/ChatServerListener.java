package LAB4_5;

import com.sun.corba.se.spi.activation.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Clinton on 6/12/2015.
 */
public class ChatServerListener implements Runnable
{
    protected ChatWindow window;
    protected int port;
    protected String server;
    protected ServerSocket serverSocket;
    protected Scanner input;


    public ChatServerListener(String serverip, int portnum, ChatWindow win) throws IOException
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
            while(true)
            {
                Socket incomingConnection = serverSocket.accept();

                this.input = new Scanner(incomingConnection.getInputStream());
                while(incomingConnection.isConnected() && !incomingConnection.isClosed())
                {
                    try {
                        String incomingMessage = input.nextLine();
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

                    }
                }
            }


        }catch (Exception e)
        {

        }
    }

    public void closeConnection()
    {
        try
        {
            this.input.close();
            this.serverSocket.close();
        }catch(Exception e)
        {

        }
    }
}