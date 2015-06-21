package LAB4_5;

import java.io.*;
import java.net.*;

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
    private String serverIP; // IP address of the chat server.
    private int portNum; // Port number for data exchange.
    private ChatWindow window; // GUI window object for message exchange.
    private ServerSocket serverSocket; // Used to listen for incoming connection requests to port.  Connects.
    private Socket incomingConnection; // Used for clients that try to connect to this server.
    private Socket outgoingConnection; // Used to send data on established port connection.
    private DataInputStream incomingData; // Data stream for receiving data.
    private DataOutputStream outgoingData; // Data stream for sending data.


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

    // Use if this pc is the server for the chat connection.
    @Override
    public void run()
    {
        // Try to establish a connection to 'this' object's port.
        try {
            this.serverSocket = new ServerSocket(this.portNum);

            // While the server connection is open,
            while (!this.serverSocket.isClosed()) {
                // accept any incoming connections and create input/output streams for that connection.
                this.incomingConnection = serverSocket.accept();
                this.incomingData = new DataInputStream(incomingConnection.getInputStream());
                this.outgoingData = new DataOutputStream(incomingConnection.getOutputStream());

                // While the incoming connection is connected and open,
                while (incomingConnection.isConnected() && !incomingConnection.isClosed()) {
                    // Try to receive in coming text from the stream and store it in a String.
                    try {
                        String incomingMessage = incomingData.readUTF();
                        // If String equals 'exit', close incoming connection and return to listening.
                        if (incomingMessage.equals("exit")) {
                            incomingConnection.close();
                            break;
                        }
                        // If the string is not 'exit' or null, write the message to 'this' ChatWindow's chat display.
                        if (!incomingMessage.equals(null))
                            window.addServerText(incomingMessage);
                    }
                    // If there is an error incoming connection, close it.
                    catch (IOException e) {
                        incomingConnection.close();
                    }
                }
            }
        }
        // If socket bind fails, notify the user and connect to the default port.
        catch (IOException e) {
            window.getChatDisplay().append("***\nSYSTEM: Unable to establish a connection on this port. \n" +
                    "Please enter a valid port number.\n***\n\n");
        }
    }

    // Use if this pc is only a client.

//    @Override
//    public void run()
//    {
//
//        try
//        {
//            outgoingConnection = new Socket(this.serverIP,this.portNum); // Create a new connection on 'this' IP/port.
//            this.incomingData = new DataInputStream(outgoingConnection.getInputStream()); // Open input stream.
//            this.outgoingData = new DataOutputStream(outgoingConnection.getOutputStream()); // Open output stream.
//
//            // While the socket is connected and not closed,
//            while(outgoingConnection.isConnected() && !outgoingConnection.isClosed())
//            {
//
//                // Try to read a String from the input stream.
//                try
//                {
//                    String incomingMessage = incomingData.readUTF();
//                    // If String equals 'exit', close incoming connection and return to listening.
//                    if (incomingMessage.equals("exit"))
//                    {
//                        outgoingConnection.close();
//                        break;
//                    }
//                }
//                // If there is an error in receiving data connection, close the output stream.
//                catch (IOException e)
//                {
//                    incomingConnection.close();
//                }
//            }
//        }
//        // If socket bind fails or IP connection fails, notify the user and connect to the default port and IP address.
//        catch (IOException e)
//        {
//            window.getChatDisplay().append("***\nSYSTEM: Unable to use this port. \n" +
//                "Default port and IP numbers will be used. Please enter a valid port number and IP address.\n***\n\n");
//        }
//    }

    /**
     * Unused
     */
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
        }
        try
        {
            if(outgoingData != null)
                this.outgoingData.close();
        }catch(IOException e)
        {
            // If unable to close data stream, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
        }
        try
        {
            if(outgoingConnection != null) {
                this.outgoingConnection.close();
                this.window.getIsConnected(false);
            }
        }catch(IOException e)
        {
            // If unable to close socket, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
        }
        try
        {
            if(incomingConnection != null)
                this.incomingConnection.shutdownInput();
        }catch(IOException e)
        {
            // If unable to shutdown connection to input stream, it may already be closed, interrupted, or it may have
            // never been opened. Should not affect application or resources.
        }
        try
        {
            if(incomingConnection != null)
                this.incomingConnection.shutdownOutput();
        }catch(IOException e)
        {
            // If unable to shutdown connection to output stream, it may already be closed, interrupted, or it may have
            // never been opened. Should not affect application or resources.
        }
        try
        {
            if(incomingConnection != null)
                this.incomingConnection.close();
        }catch(IOException e)
        {
            // If unable to close socket, it may already be closed, interrupted, or it may have never been opened.
            // Should not affect application or resources.
        }
        try
        {
            if(serverSocket != null) {
                this.serverSocket.close();
                this.window.getIsConnected(false);
            }
        }catch(IOException e)
        {
            // If unable to close server socket, it may already be closed, interrupted, or it may have never been
            // opened. Should not affect application or resources.
        }
    }
}