package LAB4_5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Clinton on 6/18/2015.
 */
public class ClientConnection implements Runnable {
    protected Socket OutgoingConnection;
    protected DataInputStream input;
    protected DataOutputStream sendUserMessage;
    protected String server;
    protected int port;
    protected ChatWindow window;

    public ClientConnection(String serverip, int portnum, ChatWindow cwindow)
    {
        this.server = serverip;
        this.port = portnum;
        this.window = cwindow;
    }


    @Override
    public void run()
    {

        try
        {
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
    }
}
