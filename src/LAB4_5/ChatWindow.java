package LAB4_5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Clinton Fowler
 * @author Tyler Cazier
 * @version 6/14/15
 */
public class ChatWindow
{
    protected JFrame window;
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

    public ChatWindow()
    {
        // Default IP/Port
        this.serverip = "localhost";
        this.portnum = 8989;

        // Initialize JFrame(window) and Set Parameters
        this.window = new JFrame();
        this.window.setTitle("CS3230 Chat Client");
        this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.window.setSize(new Dimension(800,840));
        this.window.setVisible(true);

        // Initialize JPanel(background) and Set Parameters
        this.background = new JPanel();
        this.background.setBackground(Color.red);
        this.background.setLayout(new BorderLayout());
        this.background.setPreferredSize(new Dimension(790,640));
        this.background.setVisible(true);

        // Initialize JOptionPane (inputPane) and Set Parameters

        // Initialize JMenuBar(menuBar) and Set Parameters
        this.menuBar = new JMenuBar();
        this.window.setSize(new Dimension(790,80));
        this.window.setVisible(true);

        // Initialize JMenu (menu) and Set Parameters
        this.menu = new JMenu("Menu");
        this.menu.setSize(new Dimension(100,80));
        this.menu.setVisible(true);

        // Initialize JMenuItems (for menu) and Set Parameters
        this.createConnection = new JMenuItem("Create Connection");
        this.createConnection.setSize(new Dimension(100, 100));
        this.createConnection.setVisible(true);

        this.exit = new JMenuItem("Exit");
        this.exit.setSize(new Dimension(100,100));
        this.exit.setVisible(true);

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

        //
        this.userinputPanel = new JPanel();
        this.userinputPanel.setBackground(Color.BLACK);
        this.userinputPanel.setVisible(true);
        this.userinputPanel.setLayout(new BorderLayout());

        // Initialze JButton(submitChat) w/ActionListener that calls addText on button click.
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
            this.chatDisplay.append("me: " + this.chatInput.getText() + "\n\n");
            this.chatInput.setText("");
            updateScreen();
        }

    }

    // Display Server Information.
    public void addServerText(String servertext)
    {
        this.chatDisplay.append(serverip + ": " + servertext + "\n\n");
        //updateScreen();
        this.chatDisplay.updateUI();
    }

    //
    public void updateConnectionInfo()
    {
        this.chatUserDisplay.setText("Connection Information:\n\n" +
                "Server: " + serverip + "\n"
                + "Port#: " + portnum + "\n"
                + "Listening on Port: 8989");
        updateScreen();
    }
}
