package LAB4_5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Clinton on 6/12/2015.
 */
public class ChatWindow
{
    protected JFrame window;
    protected JPanel background;
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
        //creating default settings for testing to local host
        this.serverip = "localhost";
        this.portnum = 8989;

        //set up window defaults
        this.window = new JFrame();
        this.window.setTitle("CS3230 Chat Client");
        this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.window.setSize(new Dimension(800,800));
        this.window.setVisible(true);

        this.background = new JPanel();
        this.background.setBackground(Color.red);
        this.background.setLayout(new BorderLayout());
        this.background.setPreferredSize(new Dimension(790,790));
        this.background.setVisible(true);

        this.chatDisplay = new JTextArea();
        this.chatDisplay.setEditable(false);
        this.chatDisplay.setWrapStyleWord(true);
        this.chatDisplay.setLineWrap(true);
        this.chatDisplay.setVisible(true);

        this.chatScroll = new JScrollPane(this.chatDisplay);
        this.chatScroll.setPreferredSize(new Dimension(400, 650));
        this.chatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.chatScroll.setVisible(true);
        this.chatScroll.setAutoscrolls(true);

        this.chatUserDisplay = new JTextArea("Connection Information:\n\n");
        this.chatUserDisplay.setEditable(false);
        this.chatUserDisplay.setWrapStyleWord(true);
        this.chatUserDisplay.setLineWrap(true);
        this.chatUserDisplay.setVisible(true);

        this.chatUserScroll = new JScrollPane(this.chatUserDisplay);
        this.chatUserScroll.setPreferredSize(new Dimension(200,650));
        this.chatUserScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.chatUserScroll.setVisible(true);

        this.userinputPanel = new JPanel();
        this.userinputPanel.setBackground(Color.BLACK);
        this.userinputPanel.setVisible(true);
        this.userinputPanel.setLayout(new BorderLayout());

        this.submitChat = new JButton("Submit");
        this.submitChat.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e)
            {
                //updateChat();
                addText();
            }
        });

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
        this.chatInput.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                clearTextField();
            }});

        this.chatInputScroll = new JScrollPane(this.chatInput);
        this.chatInputScroll.setPreferredSize(new Dimension(500, 50));
        this.chatInputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.chatInputScroll.setVisible(true);

        //Add components to the window
        this.window.add(this.background);
        this.background.add(this.chatScroll);
        this.background.add(this.chatUserScroll, BorderLayout.WEST);
        this.background.add(this.userinputPanel, BorderLayout.SOUTH);
        this.userinputPanel.add(this.submitChat, BorderLayout.WEST);
        this.userinputPanel.add(this.chatInputScroll, BorderLayout.CENTER);

        //pack the window to size and update the screen.
        this.window.pack();
        updateConnectionInfo();
        updateScreen();

    }

    private void clearTextField()
    {
        if(this.clearTextField)
        {
            this.clearTextField = false;
            this.chatInput.setText("");
        }
    }

    private void updateScreen()
    {
        this.chatDisplay.updateUI();
        this.chatUserDisplay.updateUI();
        this.chatUserScroll.updateUI();
        this.chatScroll.updateUI();
        this.background.updateUI();
        this.chatDisplay.setCaretPosition(this.chatDisplay.getText().length());
    }

    private void addText()
    {
        if(!this.chatInput.getText().equals("") && !this.chatInput.getText().equals(null))
        {
            this.chatDisplay.append("me: " + this.chatInput.getText() + "\n\n");
            this.chatInput.setText("");
            updateScreen();
        }

    }

    public void addServerText(String servertext)
    {
        this.chatDisplay.append(serverip + ": " + servertext + "\n\n");
        //updateScreen();
        this.chatDisplay.updateUI();
    }

    public void updateConnectionInfo()
    {
        this.chatUserDisplay.setText("Connection Information:\n\n" +
                "Server: " + serverip + "\n"
                + "Port#: " + portnum + "\n"
                + "Listening on Port: 8989");
        updateScreen();
    }
}
