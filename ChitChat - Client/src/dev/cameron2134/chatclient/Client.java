
package dev.cameron2134.chatclient;

import dev.cameron2134.chatclient.display.GUI_Chat;
import dev.cameron2134.chatclient.display.GUI_ChatRoom;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class Client {

    private final String host = "localhost"; 
                         
    private final int port = 8888;
    
    private BufferedReader textInput;
    private PrintWriter textOutput;
    
    private DataOutputStream imgOutStream;
    private InputStream imgInputStream;
    
    private Socket clientSocket, imgSocket;
    
    private GUI_Chat chatWindow;
    private GUI_ChatRoom groupWindow;
    
    private volatile boolean isSendingCommand, chatWindowOpen, messengerModeActive;


    /**
     * List of commands to send to the server so it knows what to do with any data sent.
     */
    public enum Command {
        
        CHECK_USERNAME,
        GET_DISPLAYNAME,
        STORE_AVATAR,
        RETRIEVE_AVATAR,
        UPDATE_LOGON,
        USER_LOGGED_OFF,
        USERNAME_EXISTS,
        CREATE_ACCOUNT,
        GET_USERID,
        ADD_FRIEND,
        EDIT_FRIEND,
        DELETE_FRIEND,
        GET_FRIEND_LIST,
        GET_CHATROOM_LIST,
        GET_USERS_IN_CHATROOM,
        REGISTER_USERNAME,
        INITIATE_CHAT,
        INITIATE_GROUP_CHAT,
        BREAK_LISTENER,
        CREATE_CHATROOM,
        DELETE_CHATROOM,
        CHATROOM_EXISTS,
        GET_CHATROOM_CREATOR,
        JOIN_CHATROOM,
        LEAVE_CHATROOM,
        IS_LOGGED_ON;

    }
    
    
    
    /**
     * Initialise the connection with the server, default all boolean values to false.
     */
    public Client() {
        
        connectToServer();
        
        this.isSendingCommand = false;
        this.chatWindowOpen = false;
        this.messengerModeActive = false;
        
    }
    
    
    /**
     * Establish the initial connection to the server
     */
    private void connectToServer() {
        
        try {
            clientSocket = new Socket(host, port);
            imgSocket = new Socket(host, port + 1);
            
            
            
            System.out.println(("Connection successful. Now authenticatin details..."));
            
            textInput = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()) ) ;
            textOutput = new PrintWriter(clientSocket.getOutputStream(),true); 
            
            
            
        } 
        
        catch (IOException ex) {
            System.err.println("Connection failed - the server is down or the connection is blocked: " + ex);
        } 
        
        
    }
    
    
    
    /**
     * Establish a connection to the service using account credentials. Authenticate details and grant
     * access if successful.
     * @param username The account username.
     * @param password The password for the account.
     * @return Whether or not the login attempt was successful.
     */
    public boolean connectToService(String username, String password) {
        String msg;
        boolean success = false;
        System.out.println("Attempting to connect to the ChitChat service...");
        
        
        try {           
            textOutput.println(Command.REGISTER_USERNAME + " " + username);
            
            
            imgInputStream = imgSocket.getInputStream();
            
            // Read image here at logon?
            byte[] sizeAr = new byte[4];
            imgInputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            imgInputStream.read(imageAr);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageAr));
            
      
            ImageIO.write(img, "jpg", new File("res/temp/temp.png"));
            
            imgOutStream = new DataOutputStream(this.imgSocket.getOutputStream());
            
            // Authenticase this user with the server
            textOutput.println(Command.CHECK_USERNAME + " " + username + " " + password);
            
            msg = textInput.readLine();
                
            
            
            if (msg.equals("false"))
                success = false;
            
            else {
                System.out.println("Authentication successful!");
                System.out.println("STATUS: " + this.isSendingCommand);
                new Thread(new MessageListener()).start();

                success = true;
            }
        } 
        
        catch (IOException ex) {
            System.err.println("Connection failed - the server is down or the connection is blocked: " + ex);
        } 
 
        return success;
    }
    
    
    
    // Send a command to the server to request data from the database, receive results from a query
    /**
     * Request data from the database through the server.
     * @param cmd The command to issue so the server knows what to do.
     * @param params Information needed to retrieve the data, e.g. username.
     * @return The data that was retrieved.
     */
    public String requestData(Command cmd, String... params) {
        
        this.isSendingCommand = true;
        
        this.disableMessengerMode();
        
        String result = "";
        
        
        String finalCmd = cmd.toString() + " ";
        
        for (String x : params) {
            if (!x.equals(params[params.length-1]))
                finalCmd += x + " ";
            else
                finalCmd += x;
        }
        
        
        try {
            
            textOutput.println(finalCmd);
            System.out.println("Sending command " + finalCmd);
            
            
            result = textInput.readLine();


            
        } 
        
        catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } 

        
        this.isSendingCommand = false;

        return result;
    }
    
    
    
    
    /**
     * Request data from the database through the server as a List rather than a String.
     * @param cmd The command to issue so the server knows what to do.
     * @param params Information needed to retrieve the data, e.g. username.
     * @return The list of data that was retrieved.
     */
    public List<String> requestDataAsArray(Command cmd, String... params) {
        
        this.isSendingCommand = true;
        
        this.disableMessengerMode();
        
        List<String> result = new ArrayList<>();
        
        
        String finalCmd = cmd.toString() + " ";
        
        for (String x : params) {
            if (!x.equals(params[params.length-1]))
                finalCmd += x + " ";
            else
                finalCmd += x;
        }
        
        
        try {
            
            textOutput.println(finalCmd);
            System.out.println("Sending command " + finalCmd);
            
            String line;
            while (!(line = textInput.readLine()).equals("END_WRITE")) {
                result.add(line);
            }
            
        } 
        
        catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        this.isSendingCommand = false;   
        return result;
    }
    
    
    
    
    /**
     * Send an image to the server to store in the database.
     * @param cmd The command to issue.
     * @param username The username for the account where the avatar will be stored.
     * @param file The path to the image on the users file system.
     */
    public void storeImg(Command cmd, String username, File file) {
        
        BufferedImage img;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try {
            
            img = (ImageIO.read(file));
            
            ImageIO.write(img, "jpg", byteArrayOutputStream);
            
            byte[] size = byteArrayOutputStream.toByteArray();
            
            imgOutStream.write((Integer.toString(size.length)).getBytes());
            imgOutStream.write(size,0,size.length);
            
    
        } 
        
        catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
    
    
    
   
    

    
    /**
     * Send data to the server to process.
     * @param cmd The command to issue.
     * @param pararms Data to pass to the server to be processed.
     */
    public void sendData(Command cmd, String... pararms) {

        String finalCmd = cmd.toString() + " ";
        
        for (String x : pararms) {
            if (!x.equals(pararms[pararms.length-1]))
                finalCmd += x + " ";
            else
                finalCmd += x;
        }
        
        textOutput.println(finalCmd);
    }
    
    
    
    
    /**
     * If a client is chatting in a private room or a chat room, resume normal command listening
     * operations to process commands.
     */
    public void disableMessengerMode() {
        
        // Break the readline in the messagelistener
        if (this.messengerModeActive) {
            this.messengerModeActive = false;
            textOutput.println(Command.BREAK_LISTENER);}
        
        
    }
    
    
    
    
    /**
     * Whether or not the user is currently chatting with other users.
     * @param status Whether or not this is  true or false.
     */
    public void setChatStatus(boolean status) {
        this.chatWindowOpen = status;
        System.out.println("Chat status: " + status);
    }
    
    /**
     * Assign a GUI_Chat object to this class.
     * @param chatWindow The object to pass.
     */
    public void setChatWindow(GUI_Chat chatWindow) {
        this.chatWindow = chatWindow;
    }
    
    
    /**
     * Assign a GUI_ChatRoom object to this class.
     * @param groupWindow The GUI_ChatRoom object to assign.
     */
    public void setGroupChatWindow(GUI_ChatRoom groupWindow) {
        this.groupWindow = groupWindow;
    }
    
    
    
    
    
    /**
     * Check whether or not the user is chatting.
     * @return True or false.
     */
    public boolean isChatting() {
        return this.chatWindowOpen;
    }
    
    
    /**
     * Get the client socket object.
     * @return Client socket.
     */
    public Socket getConnection() {
        
        return this.clientSocket;
    }
    
    
    /**
     * Get the image socket object.
     * @return The image socket.
     */
    public Socket getImgSocket() {
        
        return this.imgSocket;
    }
    
    
    
  
    
    
    
    
    /**
     * New thread to run whenever an invidiual chat is running when commands aren't being sent.
     */
    private class MessageListener implements Runnable {
        
     
        @Override
        public void run() {

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }

                if ((chatWindowOpen && !isSendingCommand) && !clientSocket.isClosed()) {
                    try {
                        
                        messengerModeActive = true;


                        String msg = textInput.readLine();

                        
                        
                        if (msg.contains("REFRESH_CHATROOM_LIST")) {
                            groupWindow.loadUserList();
                            System.out.println("refreshing chat");
                        }

                        else {
                            if (chatWindow != null && !msg.equals(Command.BREAK_LISTENER.toString()))
                                chatWindow.out(msg);

                            if (groupWindow != null && !msg.equals(Command.BREAK_LISTENER.toString()))
                                groupWindow.out(msg);
                        }
                        
                        

                        System.out.println(msg);
                    } 

                    catch (IOException ex) {
                        System.out.println("Stopped listening for messages - connection terminated.");
                    } 
            
                }

                
                

            }   
               
        }
        
        
    }
    
    
    
}
