
package dev.cameron2134.chatclient.display;

import dev.cameron2134.chatclient.Client;
import dev.cameron2134.chatclient.Client.Command;
import dev.cameron2134.chatclient.TTS;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;


public class GUI_Chat extends GUI {

    private String friendUsername;
    
   
    
    /**
     * Create a new chat window between the specified user and their friend.
     * @param client The client object.
     * @param username The logged in user.
     * @param friendUsername The users friend.
     */
    public GUI_Chat(Client client, String username, String friendUsername) {
        this.feel();
        
        //name = JOptionPane.showInputDialog(null,"Enter your name :", "Username",JOptionPane.PLAIN_MESSAGE);
        initComponents();
        
        this.client = client;
        
        this.client.setChatStatus(true);
        this.client.setChatWindow(this);
        
        this.username = username;
        this.friendUsername = friendUsername;
        this.ttsEnabled = false;
        
        this.setTitle("Chatting with " + friendUsername);
        
        createListeners();
        //this.setTitle("ChitChat Client - " + name);
    }

    
    
    
    @Override
    public void createListeners() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                client.setChatStatus(false);
                client.disableMessengerMode();
            }

            
        });
        
        TF_input.addActionListener((ActionEvent e) -> {

            this.client.sendData(Command.INITIATE_CHAT, username, friendUsername, TF_input.getText());
            out("You: " + TF_input.getText());
            TF_input.setText(null);
            
        });
        
        
        TF_input.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                TF_input.setText(null);
            }

            @Override
            public void focusLost(FocusEvent e) {
                TF_input.setText("Press enter to send your message");
            }
            
        });
        
        
        menu_colourPicker.addActionListener((ActionEvent e) -> {
            
            Color color=JColorChooser.showDialog(this,"Select a color",Color.black); 
            TA_output.setForeground(color);
        });
        
        
        menu_fontPicker.addActionListener((ActionEvent e) -> {
           
            String[] fonts = {"Monospaced", "Calibri", "Times New Roman", "Palatino Linotype", "Trebuchet MS"};
            String font = (String) JOptionPane.showInputDialog(null, 
                            "Pick a new font:",
                            "Font Chooser",
                            JOptionPane.QUESTION_MESSAGE, 
                            null, 
                            fonts, 
                            fonts[0]);
            
            TA_output.setFont(new Font(font, Font.PLAIN, 13));
        });
        
        
        menu_increaseFont.addActionListener((ActionEvent e) -> {
            
            int newSize = TA_output.getFont().getSize();
            newSize +=2;
            
            TA_output.setFont(new Font(TA_output.getFont().getName(), Font.PLAIN, newSize));
        });
        
        
        menu_decreaseFont.addActionListener((ActionEvent e) -> {
            
            int newSize = TA_output.getFont().getSize();
            newSize -=2;
            
            TA_output.setFont(new Font(TA_output.getFont().getName(), Font.PLAIN, newSize));
        });
        
        
        menu_tts.addActionListener((ActionEvent e) -> {
            
            this.ttsEnabled = true;
        });
    }
    
    
    
    /**
     * Output a message to the Text Area. If Text to Speech is enabled, start a new thread to read out
     * the text.
     * @param msg The message to display.
     * 
     */
    public void out(String msg) {

        TA_output.append(msg + "\n");
        
        TA_output.setCaretPosition(TA_output.getDocument().getLength());
        

        if (this.ttsEnabled) 
            new Thread(new TTS(msg)).start();
    }
    

 


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TA_output = new javax.swing.JTextArea();
        TF_input = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menu_colourPicker = new javax.swing.JMenuItem();
        menu_fontPicker = new javax.swing.JMenuItem();
        menu_increaseFont = new javax.swing.JMenuItem();
        menu_decreaseFont = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        menu_tts = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ChitChat Client");
        setResizable(false);

        TA_output.setEditable(false);
        TA_output.setColumns(20);
        TA_output.setRows(5);
        jScrollPane1.setViewportView(TA_output);

        TF_input.setFont(new java.awt.Font("Trebuchet MS", 0, 11)); // NOI18N
        TF_input.setText("Press enter to send your message");

        jMenu1.setText("Customize");

        menu_colourPicker.setIcon(new javax.swing.ImageIcon("E:\\Software\\Dropbox\\Uni Work\\Year 3\\Computing Project\\Development\\Java Programs\\ChitChat - Client\\res\\black.png")); // NOI18N
        menu_colourPicker.setText("Colour Picker");
        jMenu1.add(menu_colourPicker);

        menu_fontPicker.setIcon(new javax.swing.ImageIcon("E:\\Software\\Dropbox\\Uni Work\\Year 3\\Computing Project\\Development\\Java Programs\\ChitChat - Client\\res\\black.png")); // NOI18N
        menu_fontPicker.setText("Font Picker");
        jMenu1.add(menu_fontPicker);

        menu_increaseFont.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_OPEN_BRACKET, java.awt.event.InputEvent.CTRL_MASK));
        menu_increaseFont.setIcon(new javax.swing.ImageIcon("E:\\Software\\Dropbox\\Uni Work\\Year 3\\Computing Project\\Development\\Java Programs\\ChitChat - Client\\res\\black.png")); // NOI18N
        menu_increaseFont.setText("Increase Font Size");
        jMenu1.add(menu_increaseFont);

        menu_decreaseFont.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_CLOSE_BRACKET, java.awt.event.InputEvent.CTRL_MASK));
        menu_decreaseFont.setIcon(new javax.swing.ImageIcon("E:\\Software\\Dropbox\\Uni Work\\Year 3\\Computing Project\\Development\\Java Programs\\ChitChat - Client\\res\\black.png")); // NOI18N
        menu_decreaseFont.setText("Decrease Font Size");
        jMenu1.add(menu_decreaseFont);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Usability");

        menu_tts.setIcon(new javax.swing.ImageIcon("E:\\Software\\Dropbox\\Uni Work\\Year 3\\Computing Project\\Development\\Java Programs\\ChitChat - Client\\res\\black.png")); // NOI18N
        menu_tts.setText("Text To Speech");
        jMenu2.add(menu_tts);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TF_input, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(TF_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea TA_output;
    private javax.swing.JTextField TF_input;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menu_colourPicker;
    private javax.swing.JMenuItem menu_decreaseFont;
    private javax.swing.JMenuItem menu_fontPicker;
    private javax.swing.JMenuItem menu_increaseFont;
    private javax.swing.JMenuItem menu_tts;
    // End of variables declaration//GEN-END:variables

    
    
    
   

}
