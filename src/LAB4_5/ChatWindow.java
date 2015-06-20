package LAB4_5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * @author Clinton Fowler
 * @author Tyler Cazier
 * @version 6/14/15
 */
public class ChatWindow {
    private JFrame mainWindow;  // The main container frame for 'ChatWindow' object.
    private JFrame userServerInputWindow; // The container frame for 'userServerInputPane'. Collect connection info.
    private JPanel mainBackground;  // JPanel that is associated with 'mainWindow'.
    private JOptionPane userServerInputPane; // Called when 'mainMenuCreateConnection' option is used. Prompts for connect info.
    private JMenuBar mainMenuBar; // Menu bar for mainWindow.
    private JMenu mainMenu; // Menu for 'mainWindow'.
    private JMenuItem mainMenuCreateConnection; // Menu option on 'mainWindow'. Allows user to create a new Connection.
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
    private int portNum; // String for storing port number.  Port used for incoming/outgoing streams. Server/client.
    private ChatConnection connectChat; // A new chat connection for exchanging messages.

    public ChatWindow() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                connectChat.closeConnection();
            }
        });
        // Default IP/Port
        this.serverIP = "localhost";
        this.portNum = 8989;

        // Initialize JFrame(mainWindow) and Set Parameters
        this.mainWindow = new JFrame();
        this.mainWindow.setTitle("CS3230 Chat Client");
        this.mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.mainWindow.setSize(new Dimension(800, 840));
        this.mainWindow.setVisible(true);

        // Initialize JFrame(userServerInputWindow) and Set Parameters
        this.userServerInputWindow = new JFrame();
        this.userServerInputWindow.setPreferredSize(new Dimension(100, 200));
        this.userServerInputWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.userServerInputWindow.setVisible(false);

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
        this.userServerInputWindow.add(this.userServerInputPane);

        // Initialize JMenuBar(mainMenuBar) and Set Parameters
        this.mainMenuBar = new JMenuBar();

        // Initialize JMenu (mainMenu) and Set Parameters
        this.mainMenu = new JMenu("Menu");
        this.mainMenu.setSize(new Dimension(100, 80));
        this.mainMenu.setVisible(true);

        // Initialize JMenuItems (for mainMenu) and Set Parameters
        this.mainMenuCreateConnection = new JMenuItem("Create Connection");
        this.mainMenuCreateConnection.setSize(new Dimension(100, 100));
        this.mainMenuCreateConnection.setVisible(true);
        this.mainMenuCreateConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //userServerInputWindow.setVisible(true);
                String serverinput = new JOptionPane().showInputDialog("Enter Server IP:");
                if (!serverinput.equals("")) {
                    serverIP = serverinput;
                }
                try {
                    portNum = Integer.parseInt(new JOptionPane().showInputDialog("Enter Port Number:"));
                } catch (Exception ex) {
                    portNum = 8989;
                }

                updateConnectionInfo();
                startChatConnection();
            }
        });

        this.mainMenuExit = new JMenuItem("Exit");
        this.mainMenuExit.setSize(new Dimension(100, 100));
        this.mainMenuExit.setVisible(true);
        this.mainMenuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                try {
                    connectChat.closeConnection();
                } catch (Exception ex) {
                    //don't care about this Exception
                }
                mainWindow.dispose();
                Runtime.getRuntime().exit(0);
            }
        });

//

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

        // Unused.
        this.userInputPanel = new JPanel();
        this.userInputPanel.setBackground(Color.BLACK);
        this.userInputPanel.setVisible(true);
        this.userInputPanel.setLayout(new BorderLayout());

        // Initialize JButton(submitChat) w/ActionListener that calls addText on button click.
        this.submitChat = new JButton("Submit");
        this.submitChat.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //updateChat();
                addText();
            }
        });

        // Initialize JTextArea(chatInput) and set Parameters.  Key listener added.  Ctrl+Enter calls addText method.
        this.chatInput = new JTextArea("Enter Input Here");
        this.chatInput.setWrapStyleWord(true);
        this.chatInput.setLineWrap(true);
        this.chatInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_ENTER) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    addText();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
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

        //Add components to the mainWindow
        this.mainWindow.add(this.mainBackground);
        this.mainBackground.add(this.mainMenuBar, BorderLayout.NORTH);
        this.mainBackground.add(this.chatScroll);
        this.mainBackground.add(this.chatUserScroll, BorderLayout.WEST);
        this.mainBackground.add(this.userInputPanel, BorderLayout.SOUTH);
        this.mainMenuBar.add(this.mainMenu);
        this.mainMenu.add(this.mainMenuCreateConnection);
        this.mainMenu.add(this.mainMenuExit);
        this.userInputPanel.add(this.submitChat, BorderLayout.WEST);
        this.userInputPanel.add(this.chatInputScroll, BorderLayout.CENTER);


        //pack the mainWindow to size and update the screen.
        this.mainWindow.pack();
        updateConnectionInfo();
        updateScreen();

    }

    // Clears text from chat input (for initial text) and prevents this action from being taken again.
    private void clearTextField() {
        if (this.shouldClearTextField) {
            this.shouldClearTextField = false;
            this.chatInput.setText("");
        }
    }

    // Update chat and scroll panes to 'look and feel' version. Set caret to end of string in chat display.
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
    private void addText() {
        if (!this.chatInput.getText().equals("") && !this.chatInput.getText().equals(null)) {
            try {
                connectChat.sendMessage(this.chatInput.getText());
            } catch (Exception e) {
                this.chatDisplay.append("***\nSYSTEM: Currently no active chat Connections.\n" +
                        "Please use the mainMenu and select Start Connection\n***\n\n");
            }
            this.chatDisplay.append("me: " + this.chatInput.getText() + "\n\n");
            this.chatInput.setText("");
            updateScreen();
        }

    }

    // Display Input from Server.
    public void addServerText(String servertext) {
        this.chatDisplay.append(serverIP + ": " + servertext + "\n\n");
        updateScreen();
    }

    //Updates Server information on the side of the mainWindow.
    private void updateConnectionInfo() {
        this.connectionInfoDisplay.setText("Connection Information:\n\n" +
                "Server: " + serverIP + "\n"
                + "Port#: " + portNum + "\n");
        updateScreen();
    }

    //Method for starting the chat connection in a new thread.
    private void startChatConnection() {
        try {
            connectChat = new ChatConnection(serverIP, portNum, this);
            Thread startup = new Thread(connectChat);
            startup.start();
            //this is where i tried to connect to the server IP. still can't get it to work.
            //connectChat.runConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//will need to add another mainMenu item for closing the connection to close all the sockets so we can close the connection.
//need to add some checks to the connection portion and see if the port is already active. if so, close it so we can establish a new connection.
//if i get a wild hare going, i may set up the client to establish multiple connections on different ports, allow simultaneous chats in the same mainWindow

}