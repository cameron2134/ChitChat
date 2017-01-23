
package dev.cameron2134.chatclient.display;

import dev.cameron2134.chatclient.Client;
import dev.cameron2134.chatclient.Client.Command;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JOptionPane;



public class GUI_LogOn extends GUI {
   

    public GUI_LogOn() {
        this.feel();
        initComponents();
        
        createListeners();
        
        this.client = new Client();
        
        if (this.client.getConnection() == null || this.client.getConnection().isClosed()) {
            lbl_serverStatus.setForeground(Color.red);
            lbl_serverStatus.setText("Offline");
        }
        
        else {
            lbl_serverStatus.setForeground(new Color(0, 163, 14));
            lbl_serverStatus.setText("Online");
        }
            

        
  
    }


    
    /**
     * Creates the button action listeners.
     */
    @Override
    public void createListeners() {
        
        // Login button
        btn_login.addActionListener((ActionEvent e) -> {
            String username = TF_username.getText();
            String password = new String(TF_password.getPassword());
            
            System.out.println("Username: " + username + ", password: " + password);
            
            if (!Boolean.parseBoolean(this.client.requestData(Command.IS_LOGGED_ON, username))) {
                
                if (this.client.connectToService(username, password)) {

                    String displayName = this.client.requestData(Command.GET_DISPLAYNAME, username);

                    // Update the last time logged on for this user
                    this.client.sendData(Command.UPDATE_LOGON, username);

                    this.dispose();
                    new GUI_FriendView(this.client, username, displayName).setVisible(true);

                    System.out.println("Successful login!");
                }
                else
                    JOptionPane.showMessageDialog(null, "Incorrect details or connection issues.", "Error", JOptionPane.ERROR_MESSAGE);
            
            }
            
            else
                JOptionPane.showMessageDialog(null, "This user is already logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            
        });
        
        
        // Create account button
        btn_create.addActionListener((ActionEvent e) -> {
            new GUI_CreateAccount(this.client).setVisible(true);
        });
        
        
        
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                
                // Terminate the connection to the server before exiting the application
                try {
            
                    if (client != null || (client.getConnection() != null || !client.getConnection().isClosed())) {
                        if (client.getConnection()!= null)
                            client.getConnection().close();
                    }

                    
                } 

                catch (IOException ex) {
                    System.err.println("No Connection - cannot close");
                }
                
                System.exit(0);
            }

            
        });
        
    }
    
    
    
    
    


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lbl_serverStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        TF_username = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        TF_password = new javax.swing.JPasswordField();
        jSeparator2 = new javax.swing.JSeparator();
        btn_login = new javax.swing.JButton();
        btn_create = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ChitChat");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel4.setFont(new java.awt.Font("Palatino Linotype", 1, 12)); // NOI18N
        jLabel4.setText("Server Status:");

        lbl_serverStatus.setFont(new java.awt.Font("Palatino Linotype", 1, 12)); // NOI18N
        lbl_serverStatus.setText("Online");

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel2.setText("Username");

        TF_username.setText("cameron2134");

        jLabel3.setText("Password");

        TF_password.setText("millwall1");

        btn_login.setText("Log In");

        btn_create.setText("Create Account");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TF_password, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(57, 57, 57)
                                        .addComponent(jLabel2))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(56, 56, 56)
                                        .addComponent(jLabel3)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addContainerGap())))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(TF_username, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_login)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(btn_create)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TF_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TF_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_login)
                    .addComponent(btn_create))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_serverStatus)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lbl_serverStatus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel1.setText("Welcome to the ChitChat Instant Messaging Service");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(73, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(65, 65, 65))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField TF_password;
    private javax.swing.JTextField TF_username;
    private javax.swing.JButton btn_create;
    private javax.swing.JButton btn_login;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lbl_serverStatus;
    // End of variables declaration//GEN-END:variables

    

}
