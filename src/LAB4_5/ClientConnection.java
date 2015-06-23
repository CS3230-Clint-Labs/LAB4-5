package LAB4_5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Clinton on 6/18/2015.
 */
public class ClientConnection implements Runnable {
    private Socket OutgoingConnection;
    private DataInputStream input;
    private DataOutputStream sendUserMessage;
    private String server;
    private int port;
    private ChatWindow window;

    /**
     * Allows the construction of a new 'ClientConnection' object.  This object can be treated as a client Connection
     *
     * @param server - Used as the 'serverIP' connection IP address.
     * @param port - Used as the pipe for server/client communication.
     * @param window - A new ChatWindow object used for exchanging messages.
     *
     */

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
        window.isClientConnected(false);

    }
}
