package LAB4_5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class defines a ChatWindow object.  The ChatWindow object is a GUI application used to exchange messages on a
 * defined IP address and port number. it works in conjunction with the ChatConnection object.
 *
 * @author Clinton Fowler
 * @author Tyler Cazier
 * @version 6/20/15
 */
public class ChatWindow
{
    // STATICS
    private static final Logger LOG = Logger.getLogger(ChatWindow.class.getName());

    // Fields
    private FileHandler fh;
    private int serverPortNum; // Integer for storing port number.  Port used for incoming/outgoing streams. Server.
    private int clientPortNum; // Integer for storing port number.  Port used for incoming/outgoing streams. Client.
    private JFrame mainWindow;  // The main container frame for 'ChatWindow' object.
    private JFrame userServerInputWindow; // The container frame for 'userServerInputPane'. Collect connection info.
    private JPanel mainBackground;  // JPanel that is associated with 'mainWindow'.
    private JOptionPane userServerInputPane; // Called when 'mainMenuCreateConnection' option is used. Prompts for connect info.
    private JMenuBar mainMenuBar; // Menu bar for mainWindow.
    private JMenu mainMenu; // Menu for 'mainWindow'.
    private JMenuItem createServerConnection; // Menu option on 'mainWindow'. Allows user to create a new Server Connection.
    private JMenuItem createClientConnection; // Menu option on 'mainWindow'. Allows user to create a new Client Connection.
    private JMenuItem mainMenuExit; // Menu option that allows user to exit this application.
    private JPanel userInputPanel; // JPanel that contains submit button 'submitChat' and user input chat box area.
    private JTextArea chatInput; // Text area that allows user to enter a chat message.
    private JTextArea chatDisplay; // Displays incoming and outgoing chat messages. Uneditable.
    private JTextArea connectionInfoDisplay; // Displays connection ip and port number.  Is updated with changes.
    private JScrollPane chatScroll; // Scroll pane for 'chatInput'.
    private JScrollPane chatUserScroll; // Scroll pane for 'chatUserScroll'.
    private JScrollPane chatInputScroll; // Scroll pane for 'chatInputScroll'.
    private JButton submitChat; // Button that appends 'chatInput' text to 'chatDisplay'.  Send your message.
    private boolean shouldClearTextField = true; // Used for clearing 'chatInput' default message 'Enter Input Here'.
    private String serverIP; // String for storing IP address.  IP for server connection.
    private ChatConnection connectChat; // A new Server side chat connection for exchanging messages.
    private ClientConnection connectClient; // A new Client side chat connection for exchanging messages.
    private boolean serverIsConnected = false; // Tracks whether there is currently a server connection or not.
    private boolean clientIsConnected = false; // Tracks whether there is currently a client connection or not.

    /**
     * Constructor: Constructs a ChatWindow object. This is the only defined constructor.
     */

    public ChatWindow()
    {
        // Set Logger Level.
        LOG.setLevel(Level.INFO);

        // Try to create a new file handler with simple format.
        try {
            fh = new FileHandler("src/ChatLogFile.log"); // Choose the file to write.
            LOG.addHandler(fh); // Add the file handler to the log.
            SimpleFormatter formatter = new SimpleFormatter(); // Create a formatter for the file handler.
            fh.setFormatter(formatter); // Add the formater to the file handler.

        }
        // If user does not have rights to open, notify in console.
        catch (SecurityException se) {
            LOG.warning("User does not have rights to open/create the log file.");
        }
        // If file cannot be opened, notify in console.
        catch (IOException e) {
            LOG.warning("Unable to connect to file.");
        }

        // Initializes a shutdown hook (thread) on this ChatWindow's runtime.  On application close, regardless of
        // normal exit or application interrupt, a thread will start and execute on the below code.
        // Thread closes all incoming and outgoing streams, connections, and sockets.
        Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
                    try{
                        connectChat.closeConnection();
                    }catch (NullPointerException e){
                        // Deal with connection issues in method closeConnection.
                        LOG.info("Chat connection failed to close properly.");
                    }
                    try{
                        connectClient.closeConnection();
                    }catch (NullPointerException e)
                    {
                        // Deal with connection issues in method closeConnection.
                        LOG.info("Chat connection failed to close properly.");
                    }
                }});
        // Default IP/Port
        this.serverIP = "localhost";
        this.serverPortNum = 8989;
        this.clientPortNum = 8989;

        // Initialize JFrame(mainWindow) and Set Parameters
        this.mainWindow = new JFrame();
        this.mainWindow.setTitle("CS3230 Chat Client");
        this.mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.mainWindow.setSize(new Dimension(800, 840));
        this.mainWindow.setVisible(true);

        // Initialize JFrame(userServerInputWindow) and Set Parameters
        this.userServerInputWindow = new JFrame();
        this.userServerInputWindow.setPreferredSize(new Dimension(100, 200));
        this.userServerInputWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // Hide frame.
        this.userServerInputWindow.setVisible(false); // Should not be visible.

        // Initialize JPanel(mainBackground) and Set Parameters
        this.mainBackground = new JPanel();
        this.mainBackground.setBackground(Color.red);
        this.mainBackground.setLayout(new BorderLayout());
        this.mainBackground.setPreferredSize(new Dimension(790, 640));
        this.mainBackground.setVisible(true);

        // Initialize JOptionPane (userServerInputPane) and Set Parameters
        this.userServerInputPane = new JOptionPane();
        this.userServerInputPane.setPreferredSize(new Dimension(100, 200));
        this.userServerInputPane.setVisible(true);

        // Initialize JMenuBar(mainMenuBar) and Set Parameters
        this.mainMenuBar = new JMenuBar();

        // Initialize JMenu (mainMenu) and Set Parameters
        this.mainMenu = new JMenu("Menu");
        this.mainMenu.setSize(new Dimension(100, 80));
        this.mainMenu.setVisible(true);

        // Initialize JMenuItems (for menu) and Set Parameters
        this.createServerConnection = new JMenuItem("Start Server Connection");
        this.createServerConnection.setSize(new Dimension(100, 100));
        this.createServerConnection.setVisible(true);
        this.createServerConnection.addActionListener(new ActionListener() {
            @Override

            // On ActionListener event,
            public void actionPerformed(ActionEvent e) {
                try {
                    serverPortNum = Integer.parseInt(new JOptionPane().showInputDialog("Start Server on Port Number:"));
                } catch (Exception ex) {
                    LOG.info("Unable to parse integer for port number.  Set to default.");
                    serverPortNum = 8989;
                    chatDisplay.append("***\nSYSTEM: Invalid port number.\n" +
                            "Connecting to default port.\n***\n\n");
                }

                updateConnectionInfo();
                startServerConnection();
            }
        });

        this.createClientConnection = new JMenuItem("Start Client Connection");
        this.createClientConnection.setSize(new Dimension(100,100));
        this.createClientConnection.setVisible(true);
        this.createClientConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverinput = new JOptionPane().showInputDialog("Enter Server IP:");
                if (!serverinput.equals("")) {
                    serverIP = serverinput;
                }

                // Prompt the user for a port number.  Try to parse an Integer from user input and store in portNum.
                try {
                    clientPortNum = Integer.parseInt(new JOptionPane().showInputDialog("Enter Port Number:"));
                }
                // If unable to store, set portNum to default and notify user in ChatDisplay.
                catch (Exception ex) {
                    LOG.info("Unable to parse integer for port number.  Set to default.");
                    clientPortNum = 8989;
                    chatDisplay.append("***\nSYSTEM: Invalid port number.\n" +
                            "Connecting to default port.\n***\n\n");
                }

                updateConnectionInfo();
                startClientConnection();
            }
        });

        // Initialize JMenuItem(mainMenuExit). This menu item closes the application.
        this.mainMenuExit = new JMenuItem("Exit");
        this.mainMenuExit.setSize(new Dimension(100, 100));
        this.mainMenuExit.setVisible(true);
        this.mainMenuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                mainWindow.dispose(); // Frees up resources used by window.
                Runtime.getRuntime().exit(0); // Exit runtime normally
            }
        });

        // Initialize JTextArea(chatDisplay) and Set Parameters
        this.chatDisplay = new JTextArea();
        this.chatDisplay.setEditable(false);
        this.chatDisplay.setWrapStyleWord(true);
        this.chatDisplay.setLineWrap(true);
        this.chatDisplay.setVisible(true);

        // Initialize JScrollPane(chatScroll corresponds to chatdisplay) and Set Parameters
        this.chatScroll = new JScrollPane(this.chatDisplay);
        this.chatScroll.setPreferredSize(new Dimension(400, 650));
        this.chatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.chatScroll.setVisible(true);
        this.chatScroll.setAutoscrolls(true);

        // Initialize JTextArea(connectionInfoDisplay) and Set Parameters
        this.connectionInfoDisplay = new JTextArea("Connection Information:\n\n");
        this.connectionInfoDisplay.setEditable(false);
        this.connectionInfoDisplay.setWrapStyleWord(true);
        this.connectionInfoDisplay.setLineWrap(true);
        this.connectionInfoDisplay.setVisible(true);

        // Initialize JScrollPane(chatUserScroll corresponds to connectionInfoDisplay) and Set Parameters
        this.chatUserScroll = new JScrollPane(this.connectionInfoDisplay);
        this.chatUserScroll.setPreferredSize(new Dimension(200, 650));
        this.chatUserScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.chatUserScroll.setVisible(true);

        // Initialize JPanel(userInputPanel) and Set Parameters
        this.userInputPanel = new JPanel();
        this.userInputPanel.setBackground(Color.BLACK);
        this.userInputPanel.setVisible(true);
        this.userInputPanel.setLayout(new BorderLayout());

        // Initialize JButton(submitChat) w/ActionListener that appends userInputPanel text to ChatDisplay.
        this.submitChat = new JButton("Submit");
        this.submitChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addText();
            }
        });

        // Initialize JTextArea(chatInput) and set Parameters.  Key listener added.  Ctrl+Enter calls addText method.
        this.chatInput = new JTextArea("Enter Input Here");
        this.chatInput.setWrapStyleWord(true);
        this.chatInput.setLineWrap(true);
        this.chatInput.addKeyListener(new KeyListener() {
            // If Ctrl+Enter is pressed call addText().
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_ENTER) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    addText();
                }
            }
            // Unused events.
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        // Add mouse listener. On mouse click, remove text.
        this.chatInput.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                clearTextField();
            }
        });

        // Initialize JScrollPane(chatInputScroll corresponds to chatInput) and set Parameters.
        this.chatInputScroll = new JScrollPane(this.chatInput);
        this.chatInputScroll.setPreferredSize(new Dimension(500, 50));
        this.chatInputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.chatInputScroll.setVisible(true);

        //Add all JComponents
            // Main Window (JFrame)
            this.mainWindow.add(this.mainBackground);
            // Main Background (JPanel)
            this.mainBackground.add(this.mainMenuBar, BorderLayout.NORTH);
            this.mainBackground.add(this.chatScroll);
            this.mainBackground.add(this.chatUserScroll, BorderLayout.WEST);
            this.mainBackground.add(this.userInputPanel, BorderLayout.SOUTH);
            // Main Menu Bar (JMenuBar)
            this.mainMenuBar.add(this.mainMenu);
            // Main Menu (JMenu)
//            this.mainMenu.add(this.mainMenuCreateConnection);
            this.mainMenu.add(this.createServerConnection);
            this.mainMenu.add(this.createClientConnection);
            this.mainMenu.add(this.mainMenuExit);
            // User Input Panel (JPanel)
            this.userInputPanel.add(this.submitChat, BorderLayout.WEST);
            this.userInputPanel.add(this.chatInputScroll, BorderLayout.CENTER);
            // User Server Input Window (JFrame)
            this.userServerInputWindow.add(this.userServerInputPane);

        this.mainWindow.pack();// Pack the mainWindow to size and update the screen.
        updateConnectionInfo();// Initialize/display default connection.
        updateScreen();
//        startChatConnection();
    }

    // Accessor Methods

    /**
     * Returns this ChatWindow object's chatDisplay.
     *
     * @return JTextArea chatDisplay
     */
    public JTextArea getChatDisplay() {
        return chatDisplay;
    }

    // Mutator Methods

    /**
     * Returns a boolean that allows the value of serverIsConnected to be set.
     *
     * @param isConnected boolean
     */
    public void isServerConnected(boolean isConnected) {
        this.serverIsConnected = isConnected;
    }

    /**
     * Returns a boolean that allows the value of serverIsConnected to be set.
     *
     * @param isConnected boolean
     */
    public void isClientConnected(boolean isConnected) {
        this.serverIsConnected = isConnected;
    }

    /**
     * Clears the beginning text, 'Enter Input Here', on mouse click from the chat input box and prevents any future
     * text from being cleared from that field.
     */
    private void clearTextField()
    {
        if (this.shouldClearTextField)
        {
            this.shouldClearTextField = false;
            this.chatInput.setText("");
        }
    }

    /**
     *  Update chat and scroll panes to 'look and feel' version. Set caret to end of string in chat display.
     */
    private void updateScreen() {
        this.mainMenuBar.updateUI();
        this.mainMenu.updateUI();
        this.userServerInputPane.updateUI();
        this.chatDisplay.updateUI();
        this.connectionInfoDisplay.updateUI();
        this.chatUserScroll.updateUI();
        this.chatScroll.updateUI();
        this.mainBackground.updateUI();
        this.chatDisplay.setCaretPosition(this.chatDisplay.getText().length());
    }

    // Append text with 'me: ' + chat input value and clear chat input
    private void addText()
    {
        if(!this.chatInput.getText().equals("") && !this.chatInput.getText().equals(null))
        {
            //try to send chat through either server or client connection
            try
            {
                if(connectChat != null) {
                    connectChat.sendMessage(this.chatInput.getText());
                }
                else
                {
                    connectClient.sendMessage(this.chatInput.getText());
                }
            }catch(Exception e)
            {
                // No one is listening.  Warn the user that no one is listening.
                LOG.info("Chat not active.  No one connected to server.");

                this.chatDisplay.append("***\nSYSTEM: Currently no active chat Connections.\n" +
                        "Please use the mainMenu and select Start Connection\n***\n\n");
            }
            this.chatDisplay.append("me: " + this.chatInput.getText() + "\n\n");
            this.chatInput.setText("");
            updateScreen();
        }

    }

    /**
     * Display server message to chat display
     *
     * @param serverText - the message to be displayed.
     */
    public void addServerText(String serverText) {
        this.chatDisplay.append(serverIP + ": " + serverText + "\n\n");
        updateScreen();
    }

    /**
     * Updates connection information on the connection information display.
     */

    //Updates Server information on the side of the window.
    private void updateConnectionInfo()
    {
        this.connectionInfoDisplay.setText("Connection Information:\n\n");
        if(connectChat != null)
        {
            this.connectionInfoDisplay.append("Server Listening: "+ this.serverPortNum + "\n\n");
        }
        else
        {
            this.connectionInfoDisplay.append("Server Not listening\n\n");
        }
        if(connectClient != null) {
            this.connectionInfoDisplay.append("Client Connection Information:\n\n" +
                    "Server: " + serverIP + "\n"
                    + "Port#: " + clientPortNum + "\n");
        }
        updateScreen();
    }

    /**
     * Closes any open server/client connections and begins a new connection;
     */

    private void startServerConnection()
    {
        // Close current connection if currently opened.
        if (serverIsConnected == true)
        {
            this.connectChat.closeConnection();
        }
        // Try to establish a new ServerChatConnection using 'this' serverPortNum and start a new thread on this
        try {
            connectChat = new ChatConnection(serverPortNum, this);
            Thread startup = new Thread(connectChat);
            startup.start();
            serverIsConnected = true;
            updateConnectionInfo();
        } catch (IOException e) {
            LOG.severe("Unable to connect to server. Check security settings and server IP/port information.");
            serverPortNum = 8989;
        }
    }

        //Method for starting the Client chat connection in a new thread.
    private void startClientConnection()
    {
        // Try to establish a new ClientChatConnection using 'this' serverIP and ClientPortNum and start a new thread on this
        try{
            connectClient = new ClientConnection(serverIP, clientPortNum, this);
            Thread clientStart = new Thread(connectClient);
            clientStart.start();
            clientIsConnected = true;
            updateConnectionInfo();
        }catch (Exception e)
        {
            chatDisplay.append("\n******\nUnable to connect to server\n********\n");
        }
    }
}