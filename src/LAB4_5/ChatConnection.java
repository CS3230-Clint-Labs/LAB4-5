package LAB4_5;

import java.io.*;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This method opens a server socket on a specified port/IP address.  This server listens for incoming connection
 * requests and tries to complete these requests.  Incoming and outgoing data streams opened on this port.  The
 * ChatWindow object uses this class to open a connection and exchange messages.
 *
 * @author Clinton Fowler
 * @author Tyler Cazier
 * @version 6/20/15
 */
public class ChatConnection implements Runnable
{
    // STATICS
    private static final Logger LOG = Logger.getLogger(ChatWindow.class.getName());

    // Fields
    private FileHandler fh;
    private int portNum; // Port number for data exchange.
    private ChatWindow window; // GUI window object for message exchange.
    private ServerSocket serverSocket; // Used to listen for incoming connection requests to port.  Connects.
    private Socket incomingConnection; // Used for clients that try to connect to this server.
    private DataInputStream incomingData; // Data stream for receiving data.
    private DataOutputStream outgoingData; // Data stream for sending data.


    /**
     * Allows the construction of a new 'ChatConnection' object.  This object can be treated as a Ser
     *
     * @param portNum - Used as the pipe for server/client communication.
     * @param win - A new ChatWindow object used for exchanging messages.
     * @throws IOException
     */
    public ChatConnection(int portNum, ChatWindow win) throws IOException
    {
        this.portNum = portNum; // Port number socket connections.
        this.window = win; // The chat window message exchange.
    }

    // Use if this pc is the server for the chat connection.
    @Override
    public void run()
    {

        // Try to establish a connection to 'this' object's port.
        try
        {
            serverSocket = new ServerSocket(this.portNum);

            // accept any incoming connections and create input/output streams for that connection.
            this.incomingConnection = serverSocket.accept();
            this.incomingData = new DataInputStream(incomingConnection.getInputStream());
            this.outgoingData = new DataOutputStream(incomingConnection.getOutputStream());

            // While the incoming connection is connected and open,
            while(incomingConnection.isConnected() && !incomingConnection.isClosed())
            {
                // Try to receive in coming text from the stream and store it in a String.
                try {
                    String incomingMessage = incomingData.readUTF();
                    // If String equals 'exit', close incoming connection
                    if (incomingMessage.equals("exit"))
                    {
                        incomingData.close();
                        outgoingData.close();
                        incomingConnection.close();
                        break;
                    }
                    // If the string is not 'exit' or null, write the message to 'this' ChatWindow's chat display.
                    if (!incomingMessage.equals(null))
                    {
                        window.addServerText(incomingMessage);
                    }
                } catch (NullPointerException e) // If there is an null pointer exception, close it.
                {
                    incomingData.close();
                    outgoingData.close();
                    incomingConnection.close();
                    // Close connection due to null pointer.
                    LOG.info("Null pointer exception. Unable to read input.");
                }
            }
        }catch (Exception e)
        {
            // Server was unable to make socket connection on port. Nothing can be done.
            LOG.fine("Socket connection error.");
        }
    }

    /**
     * Sends a String on an outgoing data stream to a connected client.
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException
    {
        // Try to send a message on the outgoing stream.
        try
        {
            this.outgoingData.writeUTF(message); // Send message on data stream.
            this.outgoingData.flush(); // Force buffer to send.
        }
        // If failed, throw exception.
        catch(IOException e)
        {
            LOG.severe("Unable to write on outgoing connection.");
            throw e;
        }

    }

    /**
     * Closes incoming/outgoing datastreams, server socket, and sockets.
     */
    public void closeConnection()
    {
        try
        {
            if(incomingData != null)
                this.incomingData.close();
        }catch(IOException e)
        {
            // If unable to close data stream, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
            LOG.warning("Cannot close InputDataStream.");
        }
        try
        {
            if(outgoingData != null)
                this.outgoingData.close();
        }catch(IOException e)
        {
            // If unable to close data stream, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
            LOG.warning("Cannot close OutputDataStream.");
        }
        try
        {
            if(incomingConnection != null)
                this.incomingConnection.shutdownInput();
        }catch(IOException e)
        {
            // If unable to shutdown connection to input stream, it may already be closed, interrupted, or it may have
            // never been opened. Should not affect application or resources.
            LOG.warning("Cannot close Socket input stream connection.");
        }
        try
        {
            if(incomingConnection != null)
                this.incomingConnection.shutdownOutput();
        }catch(IOException e)
        {
            // If unable to shutdown connection to output stream, it may already be closed, interrupted, or it may have
            // never been opened. Should not affect application or resources.
            LOG.warning("Cannot close Socket output stream connection.");
        }
        try
        {
            if(incomingConnection != null)
                this.incomingConnection.close();
        }catch(IOException e)
        {
            // If unable to close socket, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
            LOG.warning("Cannot close Socket connection.");
        }
        try
        {
            if(serverSocket != null) {
                this.serverSocket.close();
                this.window.isServerConnected(false);
            }
        }catch(IOException e)
        {
            // If unable to close server socket, it may already be closed, interrupted, or it may have never been
            // opened. Should not affect application or resources.
            LOG.warning("Cannot close ServerSocket connection.");
        }
    }
}