package LAB4_5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Clinton on 6/18/2015.
 */
public class ClientConnection implements Runnable {
    // STATICS
    private static final Logger LOG = Logger.getLogger(ChatWindow.class.getName());

    // Fields
    private FileHandler fh;
    private Socket OutgoingConnection;
    private DataInputStream input;
    private DataOutputStream sendUserMessage;
    private String server;
    private int port;
    private ChatWindow window;

    /**
     * Allows the construction of a new 'ClientConnection' object.  This object can be treated as a client Connection
     *
     * @param serverip - Used as the 'serverIP' connection IP address.
     * @param portnum - Used as the pipe for server/client communication.
     * @param cwindow - A new ChatWindow object used for exchanging messages.
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
                    // Close connection due to null pointer.
                    LOG.info("Null pointer exception. Unable to read input.");
                }
            }
        }
        catch (Exception e)
        {
            // Server was unable to make socket connection on port. Nothing can be done.
            LOG.info("Socket connection error.");
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
            LOG.severe("Unable to write on outgoing connection.");
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
            // If unable to close data stream, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
            LOG.warning("Cannot close InputDataStream.");
        }
        try
        {
            this.sendUserMessage.close();
        }catch(Exception e)
        {
            // If unable to close data stream, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
            LOG.warning("Cannot close OutputDataStream.");
        }
        try
        {
            this.OutgoingConnection.close();
        }catch(Exception e)
        {
            // If unable to close socket, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
            LOG.warning("Cannot close Outgoing connection.");
        }
        window.isClientConnected(false);

    }
}
