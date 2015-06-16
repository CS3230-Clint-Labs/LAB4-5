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
public class ChatWindow
{
    protected JFrame window;
    protected JFrame serverInfoWindow;
    protected JPanel background;
    protected JOptionPane inputPane;
    protected JMenuBar menuBar;
    protected JMenu menu;
    protected JMenuItem createConnection;
    protected JMenuItem exit;
    protected JPanel userinputPanel;
    protected JTextArea chatInput;
    protected JTextArea chatDisplay;
    protected JTextArea chatUserDisplay;
    protected JScrollPane chatScroll;
    protected JScrollPane chatUserScroll;
    protected JScrollPane chatInputScroll;
    protected JButton submitChat;
    private boolean clearTextField = true;
    private String serverip;
    private int portnum;
    private ChatConnection connectChat;

    public ChatWindow()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
                    try
                    {
                        connectChat.closeConnection();
                    }catch(Exception e)
                    {

                    }
                }});
        // Default IP/Port
        this.serverip = "localhost";
        this.portnum = 8989;

        // Initialize JFrame(window) and Set Parameters
        this.window = new JFrame();
        this.window.setTitle("CS3230 Chat Client");
        this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.window.setSize(new Dimension(800, 840));
        this.window.setVisible(true);

        // Initialize JFrame(serverInfoWindow) and Set Parameters
        this.serverInfoWindow = new JFrame();
        this.serverInfoWindow.setPreferredSize(new Dimension(100, 200));
        this.serverInfoWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.serverInfoWindow.setVisible(false);

        // Initialize JPanel(background) and Set Parameters
        this.background = new JPanel();
        this.background.setBackground(Color.red);
        this.background.setLayout(new BorderLayout());
        this.background.setPreferredSize(new Dimension(790, 640));
        this.background.setVisible(true);

        // Initialize JOptionPane (inputPane) and Set Parameters
        this.inputPane = new JOptionPane();
        this.inputPane.setPreferredSize(new Dimension(100, 200));
        this.inputPane.setVisible(true);
        this.serverInfoWindow.add(this.inputPane);

        // Initialize JMenuBar(menuBar) and Set Parameters
        this.menuBar = new JMenuBar();

        // Initialize JMenu (menu) and Set Parameters
        this.menu = new JMenu("Menu");
        this.menu.setSize(new Dimension(100, 80));
        this.menu.setVisible(true);

        // Initialize JMenuItems (for menu) and Set Parameters
        this.createConnection = new JMenuItem("Create Connection");
        this.createConnection.setSize(new Dimension(100, 100));
        this.createConnection.setVisible(true);
        this.createConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //serverInfoWindow.setVisible(true);
                String serverinput = new JOptionPane().showInputDialog("Enter Server IP:");
                if (!serverinput.equals(""))
                {
                    serverip = serverinput;
                }
                try
                {
                    portnum = Integer.parseInt(new JOptionPane().showInputDialog("Enter Port Number:"));
                }catch (Exception ex)
                {
                    portnum = 8989;
                }

                updateConnectionInfo();
                startChatConnection();
            }
        });
        //this can be removed. leaving for reference right now. may try a couple things with the option pane itself.
        /*this.createConnection.addMouseListener(new MouseAdapter(){

                @Override
                public void mouseClicked(MouseEvent e) {
                    //inputPane.setSize(new Dimension(100, 200));
                    inputPane.showInputDialog(inputPane, "Enter server IP address.", "IP address");
                    serverInfoWindow.setVisible(true);
                    inputPane.updateUI();
                }});
*/
        this.exit = new JMenuItem("Exit");
        this.exit.setSize(new Dimension(100,100));
        this.exit.setVisible(true);
        this.exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                try
                {
                    connectChat.closeConnection();
                }catch (Exception ex)
                {

                }
                window.dispose();
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

        // Initialize JTextArea(chatUserDisplay) and Set Parameters
        this.chatUserDisplay = new JTextArea("Connection Information:\n\n");
        this.chatUserDisplay.setEditable(false);
        this.chatUserDisplay.setWrapStyleWord(true);
        this.chatUserDisplay.setLineWrap(true);
        this.chatUserDisplay.setVisible(true);

        // Initialize JScrollPane(chatUserScroll corresponds to chatUserDisplay) and Set Parameters
        this.chatUserScroll = new JScrollPane(this.chatUserDisplay);
        this.chatUserScroll.setPreferredSize(new Dimension(200,650));
        this.chatUserScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.chatUserScroll.setVisible(true);

        // Unused.
        this.userinputPanel = new JPanel();
        this.userinputPanel.setBackground(Color.BLACK);
        this.userinputPanel.setVisible(true);
        this.userinputPanel.setLayout(new BorderLayout());

        // Initialize JButton(submitChat) w/ActionListener that calls addText on button click.
        this.submitChat = new JButton("Submit");
        this.submitChat.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e)
            {
                //updateChat();
                addText();
            }
        });

        // Initialize JTextArea(chatInput) and set Parameters.  Key listener added.  Ctrl+Enter calls addText method.
        this.chatInput = new JTextArea("Enter Input Here");
        this.chatInput.setWrapStyleWord(true);
        this.chatInput.setLineWrap(true);
        this.chatInput.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if((e.getKeyCode() == KeyEvent.VK_ENTER) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
                {
                    addText();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // Add mouse listener. On mouse click, remove text.
        this.chatInput.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                clearTextField();
            }});

        // Initialize JScrollPane(chatInputScroll corresponds to chatInput) and set Parameters.
        this.chatInputScroll = new JScrollPane(this.chatInput);
        this.chatInputScroll.setPreferredSize(new Dimension(500, 50));
        this.chatInputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.chatInputScroll.setVisible(true);

        //Add components to the window
        this.window.add(this.background);
        this.background.add(this.menuBar, BorderLayout.NORTH);
        this.background.add(this.chatScroll);
        this.background.add(this.chatUserScroll, BorderLayout.WEST);
        this.background.add(this.userinputPanel, BorderLayout.SOUTH);
        this.menuBar.add(this.menu);
        this.menu.add(this.createConnection);
        this.menu.add(this.exit);
        this.userinputPanel.add(this.submitChat, BorderLayout.WEST);
        this.userinputPanel.add(this.chatInputScroll, BorderLayout.CENTER);

        // New thread for input/output.
//        Thread listener = new Thread(this);
//        listener.start();

        //pack the window to size and update the screen.
        this.window.pack();
        updateConnectionInfo();
        updateScreen();

    }

    // Clears text from chat input (for initial text) and prevents this action from being taken again.
    private void clearTextField()
    {
        if(this.clearTextField)
        {
            this.clearTextField = false;
            this.chatInput.setText("");
        }
    }

    // Update chat and scroll panes to 'look and feel' version. Set caret to end of string in chat display.
    private void updateScreen()
    {
        this.menuBar.updateUI();
        this.menu.updateUI();
        this.inputPane.updateUI();
        this.chatDisplay.updateUI();
        this.chatUserDisplay.updateUI();
        this.chatUserScroll.updateUI();
        this.chatScroll.updateUI();
        this.background.updateUI();
        this.chatDisplay.setCaretPosition(this.chatDisplay.getText().length());
    }

    // Append text with 'me: ' + chat input value and clear chat input
    private void addText()
    {
        if(!this.chatInput.getText().equals("") && !this.chatInput.getText().equals(null))
        {
            try
            {
                connectChat.sendMessage(this.chatInput.getText());
            }catch(Exception e)
            {
                this.chatDisplay.append("***\nSYSTEM: Currently no active chat Connections.\n" +
                                        "Please use the menu and select Start Connection\n***\n\n");
            }
            this.chatDisplay.append("me: " + this.chatInput.getText() + "\n\n");
            this.chatInput.setText("");
            updateScreen();
        }

    }

    // not sure this is being used right now. may modify or remove all together
    // Display Server Information.
    public void addServerText(String servertext)
    {
        this.chatDisplay.append(serverip + ": " + servertext + "\n\n");
        updateScreen();
    }

    //will call this after we get the input connection information working.
    private void updateConnectionInfo()
    {
        this.chatUserDisplay.setText("Connection Information:\n\n" +
                "Server: " + serverip + "\n"
                + "Port#: " + portnum + "\n");
        updateScreen();
    }

    //Method for starting the chat connection in a new thread.
    private void startChatConnection()
    {
        try {
            connectChat = new ChatConnection(serverip, portnum, this);
            Thread startup = new Thread(connectChat);
            startup.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //will need to add another menu item for closing the connection to close all the sockets so we can close the connection.
    //need to add some checks to the connection portion and see if the port is already active. if so, close it so we can establish a new connection.
    //if i get a wild hare going, i may set up the client to establish multiple connections on different ports, allow simultaneous chats in the same window
}
