
package dev.cameron2134.chat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class QueryHandler {

    private Connection con;
    
    /**
     * Database login details.
     */
    private final String url = "jdbc:mysql://localhost/chitchat",
                         username = "cameron2134", 
                         password = "millwall1";
    
    
    public QueryHandler() {
        connect();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Test ">   
    // </editor-fold>
    
    /**
     * Establish a connection to the database.
     */
    private void connect() {

        try {
            
            con = DriverManager.getConnection(url, username, password);
            
            System.out.println("Database connection successful.");
            System.out.println("-------------------------------");
            System.out.println("");
        } 
        
        catch (SQLException ex) {
            System.out.println("Database connection failed! Reason: ");
            System.out.println("");
            System.out.println(ex.toString()); 
        }
        
        
    }
    
    
    
    

    /**
     * Used to verify that the credentials the user has entered match the credentials stored in the database.
     * 
     * @param username The username to authenticate.
     * @param pass The password to authenticate.
     * @return Whether or not the username and password match the entries in the database.
     */
    public boolean authenticateUser(String username, String pass) {
        
        boolean userMatch = false, 
                passMatch = false;
        
        PreparedStatement prepStatement;
        
        try {
            
            prepStatement = con.prepareStatement("SELECT username, password FROM tbl_userDetails;");
            ResultSet rs = prepStatement.executeQuery();
            
            //Statement stmt = con.createStatement();
            //ResultSet rs = stmt.executeQuery("SELECT username, password FROM tbl_userDetails;");

            
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {

                for (int i = 1; i <= columnsNumber; i++) {
                    if (rs.getString(i).equals(username)) 
                        userMatch = true; 

                    else if (rs.getString(i).equals(pass)) 
                        passMatch = true;

                }

            }

        
        }
        
        catch (SQLException ex) {
           System.out.println(ex.toString()); 
        }
        
        
        return userMatch && passMatch;
    }
    
    
    
    
    /**
     * Checks the database to see if the specified username already exists in the database.
     * 
     * @param username The username to check.
     * @return Whether or not the username exists in the database.
     */
    public boolean usernameExists(String username) {
        
        try {
 
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM tbl_userDetails;");


            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {

                for (int i = 1; i <= columnsNumber; i++) {
                    if (rs.getString(i).equals(username)) 
                        return true;

                }

            }

        }
        
        catch (SQLException ex) {
           System.out.println(ex.toString()); 
        }
        
        return false;
    }
    
    
    
    /**
     * Check whether the user is currently logged on.
     * @param username The user to check.
     * @return User online status.
     */
    public boolean userIsLoggedOn(String username) {
        
        try {
 
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT isOnline FROM tbl_userDetails WHERE username = '" + username + "';");


            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

    
            while(rs.next()) {
                if (rs.getInt(1) == 1) 
                    return true;
            }


        }
        
        catch (SQLException ex) {
           System.out.println(ex.toString()); 
           ex.printStackTrace();
        }
        
        return false;
    }
    
    
    
    
    /**
     * Check whether or not a chatroom exists.
     * @param chatroom The chatroom name to check.
     * @return Whether the chatroom exists or not.
     */
    public boolean chatroomExists(String chatroom) {
        
        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT roomName FROM tbl_chatroom;");


            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {

                for (int i = 1; i <= columnsNumber; i++) {
                    if (rs.getString(i).equals(chatroom)) 
                        return true;

                }

            }

        }
        
        catch (SQLException ex) {
           System.out.println(ex.toString()); 
        }
        
        return false;
    }
    
    
    
    
    
    /**
     * Creates a new user account and stores it in the database.
     * 
     * @param details The account details to store into the database.
     */
    public void createAccount(String... details) {
        
        PreparedStatement prepStatement;
        
        String sql = "INSERT INTO tbl_userDetails (username, displayName, email, password) VALUES(?, ?, ?, ?);";

        try {
            prepStatement = con.prepareStatement(sql);
            
            for (int i = 0; i < details.length; i++) {
            
                prepStatement.setString(i+1, details[i]);
                
            }
            
            new Thread(new Query(prepStatement)).start();
            
        } 
        
        catch (SQLException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        
    }
    
    
    
    
    /**
     * Creates a chatroom with the specified name and creator.
     * @param chatroomName The chatroom name.
     * @param username The creator's username.
     */
    public void createChatroom(String chatroomName, String username) {
        
        PreparedStatement prepStatement;
        
        String sql = "INSERT INTO tbl_chatroom (roomName, creator) VALUES(?, ?);";

        try {
            prepStatement = con.prepareStatement(sql);
            
            prepStatement.setString(1, chatroomName);
            prepStatement.setString(2, username);
            
            new Thread(new Query(prepStatement)).start();
            
        } 
        
        catch (SQLException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    
    /**
     * Checks if the specified user owns the specified chatroom.
     * @param username The user to check.
     * @param roomName The room to check.
     * @return Whether the user owns the chatroom or not.
     */
    public boolean getChatroomOwner(String username, String roomName) {
        
        String creator = "";
        
        try {
 
            Statement stmt = con.createStatement();
            PreparedStatement prepStatement;
            
            prepStatement = con.prepareStatement("SELECT creator FROM tbl_chatroom where roomName=?;");
            prepStatement.setString(1, roomName);
            
            ResultSet rs = prepStatement.executeQuery();
            
            if (rs.next())
                creator = rs.getString(1);

        }
           
        
        catch (SQLException ex) {
           System.out.println(ex.toString()); 
        }
        

        return creator.equals(username);
        
    }
    
    
    
    
    /**
     * Deletes the specified chatroom.
     * @param chatroomName The name of the chatroom to delete.
     */
    public void deleteChatroom(String chatroomName) {
        
        PreparedStatement prepStatement;
        
        String sql = "DELETE FROM tbl_chatroom WHERE roomName = ?;";

        try {
            prepStatement = con.prepareStatement(sql);
            
            prepStatement.setString(1, chatroomName);
            
            new Thread(new Query(prepStatement)).start();
            
        } 
        
        catch (SQLException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    
    /**
     * Stores an avatar created from the specified File into the record of the specified user.
     * @param username The user to add the avatar to.
     * @param file The file to create the image from.
     */
    public void storeAvatar(String username, File file) {
        
        int len;
        String query;
        PreparedStatement prepStatement;

        try
        {
            FileInputStream fis = new FileInputStream(file);
            len = (int)file.length();

            query = ("update tbl_userDetails set avatar=? where username=?");
            prepStatement = con.prepareStatement(query);

            // Method used to insert a stream of bytes
            
            prepStatement.setBinaryStream(1, fis, len); 
            prepStatement.setString(2, username);
            
            new Thread(new Query(prepStatement)).start();
            

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    
    
    /**
     * Obtains the display name of a user from the database using their username.
     * 
     * @param username The username of the user account that we want to get the display name of.
     * @return Display name corresponding to the username.
     */
    public String retrieveDisplayName(String username) {
        
        try {
 
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT displayName FROM tbl_userDetails where username='" + username + "';");

            if (rs.next()) {

                return rs.getString(1);
            }

            else
                return "n/a";

        }
        
        catch (SQLException ex) {
           System.out.println(ex.toString()); 
        }

        return "n/a";
    }
    
    
    
    
    /**
     * Check whether or not the specified user has an avatar.
     * @param username The user to check.
     * @return Whether or not the user has an avatar.
     */
    public boolean avatarExists(String username) {
        
        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT avatar FROM tbl_userDetails WHERE username = '" + username + "';");


            if (rs.next()) {
                if (rs.getBinaryStream(1) != null)
                    return true;

            }

        }
        
        catch (SQLException ex) {
           System.out.println(ex.toString()); 
        }
        
        return false;
    }
    
    
    
    /**
     * Retrieves a users avatar from the database.
     * 
     * @param username The username to retrieve the avatar from.
     * @return The avatar as an ImageIcon
     * @see ImageIcon
     */
    public BufferedImage retrieveAvatar(String username) {
        
        byte[] fileBytes;
        String query;
        
        File tempFile = new File("res/temp/avatar.jpg");
        
        if (!new File("res/temp").exists())
            new File("res/temp").mkdir();
        
        PreparedStatement prepStatement;
        System.out.println(username);
        try
        {
                query = "select avatar from tbl_userDetails where username=?";

                prepStatement = con.prepareStatement(query);


                prepStatement.setString(1, username); 
                ResultSet rs = prepStatement.executeQuery();
                
                
                
                if (rs.next())
               {
                   if (rs.getBinaryStream(1) != null) {
                       
                   
                    InputStream binaryStream = rs.getBinaryStream(1);
                    OutputStream toFile = new FileOutputStream(tempFile);

                     int read = 0;
                     byte[] bytes = new byte[1024];

                     while ((read = binaryStream.read(bytes)) != -1) {
                         toFile.write(bytes, 0, read);
                     }


                     toFile.close();
                   }
               }        

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
        try {
            BufferedImage img = (ImageIO.read(tempFile));
            return img;
        } catch (IOException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    
    
    /**
     * Updates a users account to change the last time they logged on to the current date and time.
     * 
     * @param username The username of the account to update.
     */
    public void updateLastLogOn(String username) {
        
        String query;
        PreparedStatement prepStatement;
        
        Date currDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
        
            query = ("update tbl_userDetails set lastLogOn='" + dateFormat.format(currDate) + "', isOnline = '1' where username=?");
            prepStatement = con.prepareStatement(query);

            
            prepStatement.setString(1, username);
            
            
            new Thread(new Query(prepStatement)).start();
            
            //prepStatement.executeUpdate();

        }
        
        catch (Exception e) {
        
            e.printStackTrace();
        }
        
    }
    
    
    
    
    /**
     * Set the specified users online status to offline.
     * @param username The user to change.
     */
    public void userIsOffline(String username) {
        
        String query;
        PreparedStatement prepStatement;


        try {
        
            query = ("update tbl_userDetails set isOnline = '0' where username=?");
            prepStatement = con.prepareStatement(query);

            
            prepStatement.setString(1, username);
            
            
            new Thread(new Query(prepStatement)).start();
            
            //prepStatement.executeUpdate();

        }
        
        catch (Exception e) {
        
            e.printStackTrace();
        }
        
    }
    
    

    
    /**
     * Add a new friend and group to the specified users account.
     * @param username The user to register the friend with.
     * @param friendUsername The user to add as a friend.
     * @param group The group name to add the friend under.
     */
    public void addFriend(String username, String friendUsername, String group) {
        
        PreparedStatement prepStatement;
        
        Date currDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        String sql = "INSERT INTO tbl_friends (username, friendName, groupName, dateAdded) VALUES(?, ?, ?, ?);";

        try {
            prepStatement = con.prepareStatement(sql);
            
            prepStatement.setString(1, username);
            prepStatement.setString(2, friendUsername);
            prepStatement.setString(3, group);
            prepStatement.setString(4, dateFormat.format(currDate));
            
            new Thread(new Query(prepStatement)).start();
            
        } 
        
        catch (SQLException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    
    /**
     * Deletes a friend from the database.
     * @param friendUsername The friend to delete.
     */
    public void deleteFriend(String friendUsername) {
        
        PreparedStatement prepStatement;
        
        String sql = "DELETE FROM tbl_friends WHERE friendName = ?;";

        try {
            prepStatement = con.prepareStatement(sql);
            
            prepStatement.setString(1, friendUsername);
            
            new Thread(new Query(prepStatement)).start();
            
        } 
        
        catch (SQLException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    
    /**
     * Adds the specified user to the specified chatroom when they click on the chatroom name.
     * @param username The user to add to the chatroom.
     * @param chatroomToJoin The chatroom to join.
     */
    public void addUserToChatroom(String username, String chatroomToJoin) {
        
        PreparedStatement prepStatement;
        
        
        
        String sql = "INSERT INTO tbl_usersinchatroom (username, roomName) VALUES(?, ?);";
        
        try {
            prepStatement = con.prepareStatement(sql);
            
            prepStatement.setString(1, username);
            prepStatement.setString(2, chatroomToJoin);
            
            new Thread(new Query(prepStatement)).start();
            
        } 
        
        catch (SQLException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    
    /**
     * Removes the specified user from the specified chatroom.
     * @param username The user to remove.
     * @param chatroomToLeave The chatroom to leave.
     */
    public void removeUserFromChatroom(String username, String chatroomToLeave) {
        
        PreparedStatement prepStatement;
        
        
        
        String sql = "DELETE FROM tbl_usersinchatroom  WHERE username = ? AND roomName = ?;";
        
        try {
            prepStatement = con.prepareStatement(sql);
            
            prepStatement.setString(1, username);
            prepStatement.setString(2, chatroomToLeave);
            
            new Thread(new Query(prepStatement)).start();
            
        } 
        
        catch (SQLException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    
    /**
     * Obtains the full friends list of the specified user as a List.
     * @param username The user to retrieve friends list from.
     * @return List of users friends.
     */
    public List<String> getFriendList(String username) {
        
        List<String> friendList = new ArrayList<>();
        
        String sql = "SELECT tbl_friends.friendName, tbl_friends.groupName, tbl_userdetails.displayName FROM tbl_friends JOIN tbl_userdetails ON tbl_friends.friendName=tbl_userdetails.username where tbl_friends.username=?;";
        
        try {
 
            PreparedStatement prepStatement;
            prepStatement = con.prepareStatement(sql);
            
            prepStatement.setString(1, username);
            
            ResultSet rs = prepStatement.executeQuery();


            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {

               friendList.add(rs.getString(2) + " " + rs.getString(1) + " " + rs.getString(3));
            }

        }
        
        catch (SQLException ex) {
           System.err.println(ex + " stack trace: ");
           ex.printStackTrace();
        }
        
        return friendList;
        
    }
    
    
    
    
    /**
     * Retrieves all of the chatrooms from the database as a List.
     * @return List of all the chatrooms.
     */
    public List<String> getChatroomList() {
        
        List<String> chatroomList = new ArrayList<>();
        
        String sql = "SELECT roomName FROM tbl_chatroom;";
        
        try {
 
            PreparedStatement prepStatement;
            prepStatement = con.prepareStatement(sql);
            
            ResultSet rs = prepStatement.executeQuery();


            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {

               chatroomList.add(rs.getString(1));
            }

        }
        
        catch (SQLException ex) {
           System.err.println(ex + " stack trace: ");
           ex.printStackTrace();
        }
        
        return chatroomList;
        
    }
    
    
    
    
    /**
     * Retrieves a complete list of all the chatrooms, including their creator, as a List.
     * @return Full list of all the chatrooms.
     */
    public ResultSet getChatroomListFull() {
        
        ResultSet rs = null;
        
        String sql = "SELECT roomName, creator FROM tbl_chatroom;";
        
        try {
 
            PreparedStatement prepStatement;
            prepStatement = con.prepareStatement(sql);
            
            rs = prepStatement.executeQuery();


            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            /*while (rs.next()) {

               chatroomList.add(rs.getString(1));
            }*/

        }
        
        catch (SQLException ex) {
           System.err.println(ex + " stack trace: ");
           ex.printStackTrace();
        }
        
        return rs;
        
    }
    
    
    

    

    /**
     * @return The database connection.
     */
    public Connection getConnection() {
        return this.con;
    }





    /**
     * Class to manage running update and insert queries on a separate thread to prevent server hanging issues.
     */
    class Query implements Runnable {

        private PreparedStatement prepStatement;
        
        /**
         * Construct a query to be executed by a Thread.
         * @param prepStatement The statement to execute.
         */
        public Query(PreparedStatement prepStatement) {
            this.prepStatement = prepStatement;
        }
        
        
        @Override
        public void run() {

            try {
                this.prepStatement.executeUpdate();
            } 
            
            catch (SQLException ex) {
                Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
    
    
    
 
}
