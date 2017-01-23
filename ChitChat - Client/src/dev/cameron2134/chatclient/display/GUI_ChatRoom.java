
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
import java.util.List;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class GUI_ChatRoom extends GUI {

    /**
     * The name of this chat room.
     */
    private String chatroomName;

    
    
    /**
     * Initialise a chat room.
     * @param client The client object to communicate with the server.
     * @param username The username of this user.
     * @param chatroomName The name of the chatroom joined.
     */
    public GUI_ChatRoom(Client client, String username, String chatroomName) {
        this.feel();
        
        this.client = client;
        
        this.username = username;
        this.chatroomName = chatroomName;
        this.ttsEnabled = false;
        
        this.client.sendData(Command.JOIN_CHATROOM, username, this.chatroomName);
        
        this.client.setChatStatus(true);
        this.client.setGroupChatWindow(this);
        

        initComponents();
        loadUserList();
        createListeners();

    }

    
    
    /**
     * Get the list of users connected to this chat room and display them in the user list JTable.
     */
    public void loadUserList() {
        
        List<String> users = this.client.requestDataAsArray(Command.GET_USERS_IN_CHATROOM, this.chatroomName);
        
        DefaultTableModel model = (DefaultTableModel) tbl_userList.getModel();
        model.setRowCount(0);
       

        int rowCount = model.getRowCount();

        for (String user : users) {
                if (!user.equals(Command.BREAK_LISTENER.toString()))
                    model.addRow(new Object[]{user});

        }

    }
    
    
    
    
    @Override
    public void createListeners() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                
                client.sendData(Command.LEAVE_CHATROOM, username, chatroomName);
                
                client.setChatStatus(false);
                client.disableMessengerMode();
            }

        });
        
        
        TF_input.addActionListener((ActionEvent e) -> {

            // Server will iterate through all the members in the
            this.client.sendData(Command.INITIATE_GROUP_CHAT, username, chatroomName, TF_input.getText());
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
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_userList = new javax.swing.JTable();
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

        tbl_userList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "User List"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbl_userList);
        if (tbl_userList.getColumnModel().getColumnCount() > 0) {
            tbl_userList.getColumnModel().getColumn(0).setResizable(false);
        }

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
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(TF_input))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TF_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem menu_colourPicker;
    private javax.swing.JMenuItem menu_decreaseFont;
    private javax.swing.JMenuItem menu_fontPicker;
    private javax.swing.JMenuItem menu_increaseFont;
    private javax.swing.JMenuItem menu_tts;
    private javax.swing.JTable tbl_userList;
    // End of variables declaration//GEN-END:variables

    

}
