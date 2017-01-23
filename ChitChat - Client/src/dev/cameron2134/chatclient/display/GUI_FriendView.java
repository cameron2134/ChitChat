
package dev.cameron2134.chatclient.display;

import dev.cameron2134.chatclient.Client;
import dev.cameron2134.chatclient.Client.Command;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;


public class GUI_FriendView extends GUI {

    private String username, displayName;
    
    private JPopupMenu friendMenu, chatroomMenu;
    private JMenuItem deleteFriendItem, deleteChatroomItem;
    
    private DefaultMutableTreeNode selectedNode;
    

    /**
     * Initialise the main GUI and load in required data for this user.
     * @param client The client object to communicate with the server.
     * @param username The username of this user.
     * @param displayName The display name of this user.
     */
    public GUI_FriendView(Client client, String username, String displayName) {
        this.feel();
        initComponents();

        this.client = client;
        
        this.username = username;
        this.displayName = displayName;
        
        lbl_userName.setText(this.username);
        lbl_displayName.setText(this.displayName);
        
        DefaultTreeCellRenderer friendsRenderer = (DefaultTreeCellRenderer)tree_friends.getCellRenderer();
        DefaultTreeCellRenderer chatroomsRenderer = (DefaultTreeCellRenderer)tree_chatrooms.getCellRenderer();
        
        friendsRenderer.setLeafIcon(new ImageIcon("res/offline.png"));
        friendsRenderer.setOpenIcon(new ImageIcon("res/folder-close.png"));
        friendsRenderer.setClosedIcon(new ImageIcon("res/folder-open.png"));
        
        // https://cdn3.iconfinder.com/data/icons/pix-glyph-set/50/520643-group-512.png
        chatroomsRenderer.setLeafIcon(new ImageIcon("res/chatroom.png"));
        chatroomsRenderer.setOpenIcon(new ImageIcon("res/folder-close.png"));
        chatroomsRenderer.setClosedIcon(new ImageIcon("res/folder-open.png"));
        
        // Right click menu
        this.friendMenu = new JPopupMenu();
        this.chatroomMenu = new JPopupMenu();

        this.deleteFriendItem = new JMenuItem("Delete");
        this.deleteChatroomItem = new JMenuItem("Delete");

        this.friendMenu.add(this.deleteFriendItem);
        this.chatroomMenu.add(this.deleteChatroomItem);
        
        try {
            lbl_avatar.setIcon(new ImageIcon(ImageIO.read(new File("res/temp/temp.png"))));
        } catch (IOException ex) {
            Logger.getLogger(GUI_FriendView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        
        createListeners();
        loadFriendsList();
        loadChatroomList();
    }

    
    
    
   
    
    
    /**
     * Formats a selected Tree Node by removing the display name and brackets,
     * obtaining the username.
     * @return The username extracted from the node name.
     */
    public String formatNode() {
        
        String selectedNodeName = null;


        if (selectedNode != null) {
            selectedNodeName = selectedNode.toString();
        }

        if (selectedNode != null && selectedNode.isLeaf()) {

            // Separate the username inside the brackets
            String[] temp = selectedNodeName.split("[\\(\\)]");
          selectedNodeName = temp[1];
        
        }
        
        return selectedNodeName;
    }
    
    
    
    
    /**
     * Obtain a users friends list and group names from the database and display it into the Friend tab and load the friends JTree.
     */
    private void loadFriendsList() {
        
        List<String> friendsList = this.client.requestDataAsArray(Command.GET_FRIEND_LIST, username);
        List<String> groupsList = null;
        
        DefaultTreeModel model = (DefaultTreeModel)tree_friends.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        
        int noOfGroups = model.getChildCount(root);
        
 

        
        // Place the users friends in the correct grouping folders. If the group doesnt exist, create it
        // Instead of having the groups already created in the UI, delete them all and create them here programmatically with the
        // info from the database
        String[] temp;
        DefaultMutableTreeNode child = null;
        boolean match;
        
        Collections.sort(friendsList);
        
        for (String x : friendsList) {
            System.out.println("receiving " + x);
            temp = x.split(" ");
            // Sort the list by alphabetical group name so 
            
            // Add group folder nodes
            // Create new node if the group does not already exist. Otherwise, find that group node and add to it
            //match = false;
            if (child != null) {
                
                for (int i = 0; i < root.getChildCount(); i++) {
                    
                    if (root.getChildAt(i).toString().equals(temp[0])) {
                        
                        child = (DefaultMutableTreeNode) root.getChildAt(i);
                        child.add(new DefaultMutableTreeNode(temp[2] + " " + temp[3] + " (" + temp[1] + ")"));
                       
                    }
                    
                    else
                        child = null;

                }
 
                // Catches any groups and users that are null on the current iteration
                if (child == null) {
                    child = new DefaultMutableTreeNode(temp[0]);
                    root.add(child);
                    child.add(new DefaultMutableTreeNode(temp[2] + " " + temp[3] + " (" + temp[1] + ")"));
                  
                }
            }
            
            else {
                child = new DefaultMutableTreeNode(temp[0]);
                root.add(child);
                child.add(new DefaultMutableTreeNode(temp[2] + " " + temp[3] + " (" + temp[1] + ")"));
                
            }
            

        }

        model.reload();
  
    }
    
    
    
    
    /**
     * Obtain the list of chat rooms and display it into the Chatroom tab and load the chatroom JTree.
     */
    private void loadChatroomList() {
        
        List<String> chatroomList = this.client.requestDataAsArray(Command.GET_CHATROOM_LIST);
        
        DefaultTreeModel model = (DefaultTreeModel)tree_chatrooms.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        
        for (String chatroom : chatroomList) {
            root.add(new DefaultMutableTreeNode(chatroom));
        }
        
        model.reload();
    }
    
    
    

    
    @Override
    public void createListeners() {
      
        
        this.deleteFriendItem.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e){
                client.sendData(Command.DELETE_FRIEND, formatNode());

                DefaultTreeModel model = (DefaultTreeModel)tree_friends.getModel();
                model.removeNodeFromParent(selectedNode);
                model.reload();
            }
        });
        
        
        this.deleteChatroomItem.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e){
                
                // Check who owns the chatroom
                if (Boolean.parseBoolean(client.requestData(Command.GET_CHATROOM_CREATOR, username, selectedNode.toString()))) {
                    client.sendData(Command.DELETE_CHATROOM, selectedNode.toString());

                    DefaultTreeModel model = (DefaultTreeModel)tree_chatrooms.getModel();
                    model.removeNodeFromParent(selectedNode);
                    model.reload();
                }
                
                else
                    JOptionPane.showMessageDialog(null, "You can't delete a chatroom you didn't create.", "Error", JOptionPane.ERROR_MESSAGE);
                
                
            }
        });
        
        
        
        btn_createChatroom.addActionListener((ActionEvent e) -> {
            String chatroomName = JOptionPane.showInputDialog(this,
                        "Enter a name for your chatroom:",
                        "Create Chatroom",
                        JOptionPane.INFORMATION_MESSAGE);
            
            Pattern charChecker = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher matcher = charChecker.matcher(chatroomName);
            boolean hasSpecialChars = matcher.find();
            
            
            
            if (!Boolean.parseBoolean(this.client.requestData(Command.CHATROOM_EXISTS, chatroomName)) && !chatroomName.contains(" ") && !hasSpecialChars) {
                System.out.println("Chatroom does not exist!");
                this.client.sendData(Command.CREATE_CHATROOM, chatroomName, username);
                
                DefaultTreeModel model = (DefaultTreeModel)tree_chatrooms.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();

               
                root.add(new DefaultMutableTreeNode(chatroomName));
                

                model.reload();
            }
            
            else
                JOptionPane.showMessageDialog(null, "That name is invalid. Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
        });
        
        
        btn_addFriend.addActionListener((ActionEvent e) -> {
            boolean friendAlreadyAdded = false;
            
            // Check if this friend actually exists
            String friendUsername = JOptionPane.showInputDialog(this,
                        "Enter someones username to add them as a friend:",
                        "Add a friend",
                        JOptionPane.INFORMATION_MESSAGE);
            
            String group = JOptionPane.showInputDialog(this,
                        "Enter the group name you wish to add this friend to:",
                        "Add friend to group",
                        JOptionPane.INFORMATION_MESSAGE);
            
            if (Boolean.parseBoolean(this.client.requestData(Command.USERNAME_EXISTS, friendUsername))) {
                System.out.println("Your friend exists!");
                
                // Make sure the user doesnt already have this person as a friend
                
                DefaultTreeModel model = (DefaultTreeModel)tree_friends.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
                
                int childCount = 0;
                for (int i = 0; i < root.getChildCount(); i++) {
                    
                    childCount = root.getChildAt(i).getChildCount();
                    
                    for (int x = 0; x < childCount; x++) {
                        if (root.getChildAt(i).getChildAt(x).toString().equals(friendUsername)) {
                            JOptionPane.showMessageDialog(null, "You already have this user as a friend.", "Error", JOptionPane.ERROR_MESSAGE);
                            friendAlreadyAdded = true;
                        }
                    }

                }
                
                
                if (!friendAlreadyAdded) {
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(friendUsername);
                    DefaultMutableTreeNode groupNode = null;
                    
                    String displayName = this.client.requestData(Command.GET_DISPLAYNAME, friendUsername);
                    
                    for (int i = 0; i < root.getChildCount(); i++) {
                        if (root.getChildAt(i).toString().equals(group))
                            groupNode = (DefaultMutableTreeNode) root.getChildAt(i);
                    }
                    
                    
                    // If the group doesnt exist, create it
                    if (groupNode == null) {
                        groupNode = new DefaultMutableTreeNode(group);
                        root.add(groupNode);
                    }
                    
                    groupNode.add(new DefaultMutableTreeNode(displayName + " (" + friendUsername + ")"));
                    model.reload();
                    
                    //int userID = Integer.parseInt(this.client.requestData(Command.GET_USERID, username));
                    this.client.sendData(Command.ADD_FRIEND, username, friendUsername, group);
                }
                
                
            }
            else
                JOptionPane.showMessageDialog(null, "Couldn't find your friend.", "Error", JOptionPane.ERROR_MESSAGE);
        });
        
        
        btn_setAvatar.addActionListener((ActionEvent e) -> {

            JFileChooser fileChooser = new JFileChooser();
            
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png"));

            
            int result = fileChooser.showDialog(fileChooser, "Select");
        
            if (result == JFileChooser.APPROVE_OPTION) {
               
                try {
                    BufferedImage img = ImageIO.read(fileChooser.getSelectedFile());
                    
                    if ((img.getWidth() > 90 && img.getHeight() > 90) && (img.getWidth() < 90 && img.getHeight() < 90)) {
                        JOptionPane.showMessageDialog(this,
                        "Avatar must be 90x90",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                    }
                    
                    else {
                        lbl_avatar.setIcon(new ImageIcon(img));
                        this.client.storeImg(Command.STORE_AVATAR, this.username, fileChooser.getSelectedFile());
                    }
                    
                } 
                
                catch (IOException ex) {
                    Logger.getLogger(GUI_FriendView.class.getName()).log(Level.SEVERE, null, ex);
                }  
            } 
        });
        
        

        tree_friends.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                
                JTree tree = (JTree) e.getSource();
                selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                
                if (e.getClickCount() == 2) {
                    
                    // Tell the server to start a private chat with this user
                   new GUI_Chat(client, username, formatNode()).setVisible(true);
                    
                }


                if (SwingUtilities.isRightMouseButton(e)) {

                    
                    
                    friendMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
        
        
        
        tree_chatrooms.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                
                String selectedNodeName = null;

                JTree tree = (JTree) e.getSource();
                selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                
                if (e.getClickCount() == 2) {

                    if (selectedNode != null) {
                        selectedNodeName = selectedNode.toString();
                    }

                    if (selectedNode != null && selectedNode.isLeaf()) {
                        
                        // Separate the username inside the brackets
                        //String[] temp = selectedNodeName.split("[\\(\\)]");
                      //System.out.println(temp[1]);

                      // Tell the server to start a private chat with this user
                     new GUI_ChatRoom(client, username, selectedNode.toString()).setVisible(true);
                    }
                }


                if (SwingUtilities.isRightMouseButton(e)) {

                    // If the logged in user is the owner of the chat room, they can delete it
                    
                    chatroomMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
   
        
        
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                
                client.sendData(Command.USER_LOGGED_OFF, username);
                
                if (new File("res/temp/temp.png").exists())
                    new File("res/temp/temp.png").delete();
                
                // Terminate the connection to the server before exiting the application
                try {
            
                    if (client != null || !client.getConnection().isClosed() || !client.getImgSocket().isClosed()) {
                        client.getConnection().close();
                        client.getImgSocket().close();
                    }

                    else
                        System.exit(0);
                } 

                catch (IOException ex) {
                    Logger.getLogger(GUI_Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            
        });
        
        
        
    }
    
    


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree_friends = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        tree_chatrooms = new javax.swing.JTree();
        btn_addFriend = new javax.swing.JButton();
        btn_createChatroom = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        panel_avatar = new javax.swing.JPanel();
        lbl_avatar = new javax.swing.JLabel();
        lbl_userName = new javax.swing.JLabel();
        btn_setAvatar = new javax.swing.JButton();
        lbl_displayName = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ChitChat");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jScrollPane1.setMaximumSize(new java.awt.Dimension(95, 200));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(95, 200));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Friends List");
        tree_friends.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(tree_friends);

        jTabbedPane1.addTab("Friends", jScrollPane1);

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Chatrooms");
        tree_chatrooms.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane2.setViewportView(tree_chatrooms);

        jTabbedPane1.addTab("Chatrooms", jScrollPane2);

        btn_addFriend.setText("Add Friend");

        btn_createChatroom.setText("Create Chatroom");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_addFriend)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_createChatroom)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_addFriend)
                    .addComponent(btn_createChatroom))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        panel_avatar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panel_avatar.setMaximumSize(new java.awt.Dimension(98, 90));

        lbl_avatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_avatar.setMaximumSize(new java.awt.Dimension(98, 90));

        javax.swing.GroupLayout panel_avatarLayout = new javax.swing.GroupLayout(panel_avatar);
        panel_avatar.setLayout(panel_avatarLayout);
        panel_avatarLayout.setHorizontalGroup(
            panel_avatarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_avatar, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
        );
        panel_avatarLayout.setVerticalGroup(
            panel_avatarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_avatar, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
        );

        lbl_userName.setFont(new java.awt.Font("Palatino Linotype", 0, 12)); // NOI18N
        lbl_userName.setText("Username");

        btn_setAvatar.setText("Set Avatar");

        lbl_displayName.setFont(new java.awt.Font("Palatino Linotype", 0, 12)); // NOI18N
        lbl_displayName.setText("Display Name");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_avatar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_userName)
                    .addComponent(lbl_displayName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_setAvatar)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl_userName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_displayName))
                    .addComponent(panel_avatar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(btn_setAvatar)
                .addContainerGap())
        );

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_addFriend;
    private javax.swing.JButton btn_createChatroom;
    private javax.swing.JButton btn_setAvatar;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbl_avatar;
    private javax.swing.JLabel lbl_displayName;
    private javax.swing.JLabel lbl_userName;
    private javax.swing.JPanel panel_avatar;
    private javax.swing.JTree tree_chatrooms;
    private javax.swing.JTree tree_friends;
    // End of variables declaration//GEN-END:variables

    

}
