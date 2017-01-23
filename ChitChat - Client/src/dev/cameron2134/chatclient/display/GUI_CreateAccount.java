
package dev.cameron2134.chatclient.display;

import dev.cameron2134.chatclient.Client;
import dev.cameron2134.chatclient.Client.Command;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;


public class GUI_CreateAccount extends GUI {


    /**
     * Create a new account window and send the credentials to the database.
     * @param client The client object.
     */
    public GUI_CreateAccount(Client client) {
        this.feel();
        initComponents();
        
        this.client = client;

        
        createListeners();
    }

    
    @Override
    public void createListeners() {
        btn_availability.addActionListener((ActionEvent e) -> {
            if (Boolean.parseBoolean(this.client.requestData(Command.USERNAME_EXISTS, TF_username.getText()))) {
                lbl_isAvailable.setForeground(Color.red);
                lbl_isAvailable.setText("Already taken!");
            }
            else {
                lbl_isAvailable.setForeground(new Color(0, 163, 14));
                lbl_isAvailable.setText("Valid username.");
            }
        });
            
        btn_create.addActionListener((ActionEvent e) -> {
            
            Pattern charChecker = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher matcher = charChecker.matcher(TF_username.getText() + " " + TF_displayName.getText());
            boolean hasSpecialChars = matcher.find();

            if (!hasSpecialChars && !Boolean.parseBoolean(this.client.requestData(Command.USERNAME_EXISTS, TF_username.getText()))) {
                this.client.sendData(Command.CREATE_ACCOUNT, TF_username.getText(), TF_displayName.getText(), TF_email.getText(), new String(TF_password.getPassword()));
                this.dispose();
            }
               
            else
                JOptionPane.showMessageDialog(null, "No special characters are allowed in Username or Display Name.", "Error", JOptionPane.ERROR_MESSAGE);
            
            
        });
        
    }
    




    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        TF_password = new javax.swing.JPasswordField();
        btn_create = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        TF_displayName = new javax.swing.JTextField();
        TF_username = new javax.swing.JTextField();
        btn_availability = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        TF_email = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lbl_isAvailable = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create an account");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        TF_password.setText("jPasswordField2");

        btn_create.setText("Create");

        jLabel5.setText("Display Name");

        jLabel1.setText("Username");

        TF_displayName.setText("Firstname Lastname");

        TF_username.setText("username");

        btn_availability.setText("Check Availability");

        jLabel2.setText("Email");

        TF_email.setText("email@email.com");

        jLabel3.setText("Password");

        lbl_isAvailable.setFont(new java.awt.Font("Palatino Linotype", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(TF_email, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TF_displayName, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                        .addGap(280, 280, 280))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(TF_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btn_create))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(TF_username, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lbl_isAvailable, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btn_availability))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TF_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_availability))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(lbl_isAvailable, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TF_displayName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TF_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TF_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_create))
                .addGap(97, 97, 97))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TF_displayName;
    private javax.swing.JTextField TF_email;
    private javax.swing.JPasswordField TF_password;
    private javax.swing.JTextField TF_username;
    private javax.swing.JButton btn_availability;
    private javax.swing.JButton btn_create;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbl_isAvailable;
    // End of variables declaration//GEN-END:variables

    

}
