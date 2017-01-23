
package dev.cameron2134.chat;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;


public class Server implements Runnable {

    private final int portNo = 8888;
    
    private int totalClients;
    private boolean acceptClients, 
                    isKicked = false;
    
    private List<HandleClients> connectedClients = new ArrayList<>();
    private List<HandleImages> imgSockets = new ArrayList<>();
    
    private List<String> usernames = new ArrayList<>();
    
    private List<Chatroom> chatroomList = new ArrayList<>();

    private QueryHandler queryHandler;
    
    
    HandleImages imgHandler;
    Socket clientImgSocket, clientSocket;
    
    public Server() {

        this.acceptClients = true;
        this.queryHandler = new QueryHandler();
        
        loadChatrooms();
        startServer();
        
    }
    
    
    
    
    /**
     * Creates a server socket and assigns the port number to it, then listens for any incoming
     * clients connections on that port. When a connection is detected, it creates a new thread,
     * sending the client socket to be handled to the
     */
    private void startServer() {
        
        ServerSocket serverSocket = null, serverImgSocket = null;
        
        
        HandleClients clientHandler;
        
        
        System.out.println(("Starting server on port " + portNo));
        System.out.println(("Now waiting for clients... "));
        
        try {
            serverSocket = new ServerSocket(portNo);
            serverImgSocket = new ServerSocket(portNo+1);
            
            while (acceptClients) {
                
                // Program halts here until a client socket connects to the port
                clientSocket = serverSocket.accept();
                clientImgSocket = serverImgSocket.accept();
                
                // Creating a new thread to handle the new client that has just connected
                new Thread(clientHandler = new HandleClients(clientSocket)).start();
                
                
                connectedClients.add(clientHandler);
                
 
                
                totalClients++;
            }
        } 
        
        catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        finally {
            try {
                serverSocket.close();
            } 
            
            catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
    /**
     * Load all existing chatrooms into a list to allow easy access to what users are in which chatroom for
     * message broadcasting.
     */
    private void loadChatrooms() {
        
        ResultSet rs = this.queryHandler.getChatroomListFull();
        
        try {
            while(rs.next()) {
                chatroomList.add(new Chatroom(rs.getString(1), rs.getString(2)));
                System.out.println("Added " + rs.getString(1) + " created by " + rs.getString(2));
            }
        } 
        
        catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    /**
     * Uses the sendMessage method of each HandleCLient object to send general messages
     * to each connected client.
     * @param username The user that sent the message
     * @param msg The message to be sent
     */
    private void broadcast(String clientUsername, String friendUsername, String msg) {
        
        for (HandleClients client : connectedClients) {
            
            // Ensures that the message is not sent to the user who sent it and is only sent to the person you are chatting with
            if (!client.getUsername().equals(clientUsername) && client.getUsername().equals(friendUsername)) {
                System.out.println("Sending a message to " + clientUsername);
                client.sendMessage(clientUsername, msg);
            }
        }
        
    }
    
    
    /**
     * Broadcasts a message to all members in the specified chatroom
     * @param clientUsername
     * @param chatroomName
     * @param msg 
     */
    private void broadcastChatroom(String clientUsername, String chatroomName, String msg) {
        
        // Also get the array of users in the chatroom, then check them against the list of connected clients to know
        // who to send the message to
        List<String> usersInChatroom = null;
        
        for (Chatroom chatroom : chatroomList) {
            
            if (chatroom.getRoomName().equals(chatroomName)) {
                usersInChatroom = chatroom.getUsersInChatroom();
                System.out.println("Found the correct chatroom. Users in this room: " + usersInChatroom.size());
            }
        }
        
        
        for (HandleClients client : connectedClients) {
            
            if (!client.getUsername().equals(clientUsername)) {
                
                for (int i = 0; i < usersInChatroom.size(); i++) {
                    if (client.getUsername().equals(usersInChatroom.get(i))) {
                        client.sendMessage(clientUsername, msg);
                        System.out.println("Sending the message '" + msg + "' to " + client.getUsername());
                    }
                }
                
                
            }
            
           
        }

        
    }
    
    
    
    /**
     * Broadcasts a server announcement to all users.
     * @param username
     * @param msg 
     */
    private void announce(String username, String msg) {
        
        for (HandleClients client : connectedClients) {
            
            // Ensures that the message is not sent to the user who sent it
            if (!client.getUsername().equals(username))
                client.serverAnnouncement(msg);
        }
        
    }
    

    
    
    
    

    
    
    @Override
    public void run() {
        startServer();
    }
    
    
 
    
    
    /**
     * This class handles all the connected clients, managing their input and output streams, connecting
     * and disconnecting.
     */
    private class HandleClients implements Runnable {

        private String clientUsername = "";
        private boolean disconnect = false,
                        isMuted = false, 
                        isOnline = false;
        
        private PrintWriter output;
        private BufferedReader input;
        
        private Socket client;
        
        
        
        /**
         * Sets up the input/output streams for new clients using the passed client socket,
         * takes the clients name, and adds them to the user pool.
         * @param client The client's socket to be read/written to and from.
         */
        public HandleClients(Socket client) {
            
            this.client = client;
           
            setupStreams();
        }
        
        
        
        /**
         * Initialises the input and output streams on the new client socket.
         * @param client The passed client socket
         */
        private void setupStreams() {
            
            try {
                input = new BufferedReader( new InputStreamReader( client.getInputStream())) ;
                output = new PrintWriter ( client.getOutputStream(),true);

                
                System.out.println("User has connected to the service.");
            } 
            
            catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        
        
        // Formats an array comprises of words with spaces into one word instead
        /**
         * Used to combine multiple words spread across an array into one word.
         * @param startIndex The array index to start at.
         * @param wordsToFormat The array to format.
         * @return The word combined from the array indices.
         */
        private String formatSpacing(int startIndex, String... wordsToFormat) {
            
            String formattedWord = "";
            
            for (int i = startIndex; i < wordsToFormat.length; i++) {
                if (wordsToFormat[i].equals(wordsToFormat[wordsToFormat.length-1]))
                    formattedWord += wordsToFormat[i];

                else
                    formattedWord += wordsToFormat[i] + " ";
            }
            
            return formattedWord;
        }
        
        

        
        @Override
        public void run() {
            
            String message;
            
            while (!this.client.isClosed()) {
                
                try {
                    String[] sepMsg;
                    message = input.readLine();
                    
                    // If readline is null, the client has disconnected
                    if (message == null && disconnect == false) {
                        disconnect();
                    }
                    
                    else {
                        System.out.println("Received command: " + message);
                        sepMsg = message.split(" ");
                        
                        // Decide waht to do with the command from the client
                        switch(sepMsg[0]) {
                            case "CREATE_ACCOUNT": queryHandler.createAccount(sepMsg[1], (sepMsg[2] + " " + sepMsg[3]), sepMsg[4], sepMsg[5]); break;
                            
                            case "CREATE_CHATROOM": String chatRoomName = "";
                                                    for (int i = 1; i < sepMsg.length; i++) {
                                                        if (sepMsg[i].equals(sepMsg[sepMsg.length-2]))
                                                            chatRoomName += sepMsg[i];
                                                        
                                                        else if (!sepMsg[i].equals(sepMsg[sepMsg.length-1]))
                                                            chatRoomName += sepMsg[i] + " ";
                                                    }
                                
                                                    chatroomList.add(new Chatroom(chatRoomName, sepMsg[sepMsg.length-1]));
                                                    queryHandler.createChatroom(chatRoomName, sepMsg[sepMsg.length-1]);
                                                    break;
                                                    
                            case "GET_CHATROOM_CREATOR": output.println(queryHandler.getChatroomOwner(sepMsg[1], sepMsg[2])); break;
                                                    
                            case "DELETE_CHATROOM": queryHandler.deleteChatroom(sepMsg[1]); break;
                                
                            case "JOIN_CHATROOM":   for (Chatroom room : chatroomList) {
                                                        if (room.getRoomName().equals(formatSpacing(2, sepMsg))) {
                                                            room.addUser(sepMsg[1]);
                                                        }
                                                    } 
                                queryHandler.addUserToChatroom(sepMsg[1], formatSpacing(2, sepMsg)); 
                                broadcastChatroom(sepMsg[1], formatSpacing(2, sepMsg), "REFRESH_CHATROOM_LIST");
                                
                                break;
                                                    
                                                    
                                                    
                            case "LEAVE_CHATROOM": for (Chatroom room : chatroomList) {
                                                        if (room.getRoomName().equals(formatSpacing(2, sepMsg))) {
                                                            room.removeUser(sepMsg[1]);
                                                        }
                                                    } 
                                queryHandler.removeUserFromChatroom(sepMsg[1], formatSpacing(2, sepMsg)); 
                                broadcastChatroom(sepMsg[1], formatSpacing(2, sepMsg), "REFRESH_CHATROOM_LIST");
                                break;
                                                     
                            
                            case "CHECK_USERNAME": output.println(queryHandler.authenticateUser(sepMsg[1], sepMsg[2])); break;
                                
                            case "GET_DISPLAYNAME": output.println(queryHandler.retrieveDisplayName(sepMsg[1])); break;
                                
                            case "UPDATE_LOGON": queryHandler.updateLastLogOn(sepMsg[1]); break;
                            
                            case "USER_LOGGED_OFF": queryHandler.userIsOffline(sepMsg[1]); break;
                                
                            case "STORE_AVATAR": BufferedImage img=ImageIO.read(ImageIO.createImageInputStream(this.client.getInputStream()));

                                                    File outputfile = new File("Screen.jpg");
                                                    ImageIO.write(img, "jpg", outputfile);
                                                    queryHandler.storeAvatar(clientUsername, outputfile);
                                                    break;
                                
                            case "RETRIEVE_AVATAR": ImageIO.write(queryHandler.retrieveAvatar(sepMsg[1]), "jpg", client.getOutputStream()); break;
                                
                            case "USERNAME_EXISTS": output.println(queryHandler.usernameExists(sepMsg[1])); break;
                            
                            case "CHATROOM_EXISTS": output.println(queryHandler.chatroomExists(formatSpacing(1, sepMsg))); 
                                                    break;
                                                    
                            case "IS_LOGGED_ON": output.println(queryHandler.userIsLoggedOn(sepMsg[1])); break;
                                
                            case "ADD_FRIEND": queryHandler.addFriend(sepMsg[1], sepMsg[2], sepMsg[3]); break;
                            
                            case "DELETE_FRIEND": queryHandler.deleteFriend(sepMsg[1]); break;
                                
                            case "GET_FRIEND_LIST": List<String> result = queryHandler.getFriendList(sepMsg[1]); 
                                                    for (String x : result) {
                                                        System.out.println("outputting " + x);
                                                        output.println(x);
                                                    }
                                                    output.println("END_WRITE");
                            
                                                    break;
                                                    
                            case "GET_CHATROOM_LIST": List<String> chatrooms = queryHandler.getChatroomList(); 
                                                    for (String x : chatrooms) {
                                                        System.out.println("outputting " + x);
                                                        output.println(x);
                                                    }
                                                    output.println("END_WRITE");

                                                    break;
                                                    
                            case "GET_USERS_IN_CHATROOM": List<String> usersInChatroom = null; 
                            
                                                    for (Chatroom room : chatroomList) {
                                                        if (room.getRoomName().equals(formatSpacing(1, sepMsg))) {
                                                            usersInChatroom = room.getUsersInChatroom();
                                                        }
                                                    }
                            
                            
                                                    for (String x : usersInChatroom) {
                                                        System.out.println("outputting " + x);
                                                        output.println(x);
                                                    }
                                                    output.println("END_WRITE"); 
                                                    
                                                    break;
                                
                            case "REGISTER_USERNAME": usernames.add(sepMsg[1]); this.clientUsername = sepMsg[1]; 
                            new Thread(imgHandler = new HandleImages(clientImgSocket)).start();
                            imgSockets.add(imgHandler);
                            break;

                                
                            case "INITIATE_CHAT":   String msg = "";
                                                    for (int i = 3; i < sepMsg.length; i++)
                                                        msg += sepMsg[i] + " ";
                                                    broadcast(sepMsg[1], sepMsg[2], msg);
                                                    // Will receive the username of the person chatting with, then use a getter in this class to compare
                                                    // on the connectedCLients list so messages are only sent to that client and not all of them 
                                                break;
                                                
                            case "INITIATE_GROUP_CHAT": broadcastChatroom(sepMsg[1], sepMsg[2], formatSpacing(3, sepMsg)); break;
                                
                                
                                
                            case "BREAK_LISTENER": output.println("BREAK_LISTENER"); break; // This is purely to halt the messagelistener so commands can go through
                                
                            default: System.out.println("Unrecognised command!"); break;
                        }
                    }
                    
                    /*else if (!disconnect) {
                        if (!this.isMuted)
                            broadcast(name, message);
                        else
                            output.println("You cannot talk when globally muted.");

                    }*/

                } 
                
                catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
            
            
        }
        
        
        
        /**
         * Disconnect this user from the server.
         */
        public void disconnect() {
            
            if (!imgSockets.isEmpty())
                imgSockets.remove(imgSockets.get(connectedClients.indexOf(this)));
            
            connectedClients.remove(this);
            
            
            usernames.remove(this.clientUsername);




            totalClients--;

            System.out.println("A user has disconnected. Users remaining: " + totalClients);

            // Inform the other clients that this user has disconnected

            announce(clientUsername, clientUsername + " has disconnected from the chat room");
            disconnect = true;
            
            // Inform the user that they have been kicked
            if (isKicked) {
                output.println("You have been kicked from the chat room.");
                isKicked = false;
            }

            
            
            // Let client program know they have been disconnected so their client side socket can be closed
            output.println("dc");
            
            
            
            try {
                client.close();
                
            } 
            
            catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            finally {
                try {
                    input.close();
                    output.close();
                } 
                
                catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        
        
  
        
        
        
        /**
         * Method used to send a message from one client to all the others.
         * @param username The username that the message is originating from
         * @param msg The message to send to all connected clients
         */
        public void sendMessage(String username, String msg) {
            output.println(username + ": " + msg);
        }
        
        
        /**
         * Server announcement to be broadcast to all connected clients.
         * @param msg The message to send to all connected clients
         */
        public void serverAnnouncement(String msg) {
            output.println(msg);
        }
        
        
        
        /**
         * @return This clients username.
         */
        public String getUsername() {
            return this.clientUsername;
        }
        
        /**
         * @return Whether this client is online or not.
         */
        public boolean getOnlineStatus() {
            return this.isOnline;
        }
    }
    
    
    
    
    /**
     * Class to handle the image sockets.
     */
    private class HandleImages implements Runnable {

       
        private InputStream inputStream;
        private DataOutputStream outputStream;
        
        private boolean isInitialising;
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        private Socket imgClient;
        
        
        
        /**
         * Sets up the input/output streams for new clients using the passed client socket,
         * takes the clients name, and adds them to the user pool.
         * @param imgClient The client's socket to be read/written to and from.
         */
        public HandleImages(Socket imgClient) {
            
            this.imgClient = imgClient;
            this.isInitialising = true;
            
            setupStreams();
        }
        
        
        
        /**
         * Initialises the input and output streams on the new client socket.
         * @param client The passed client socket
         */
        private void setupStreams() {
            
            try {
                inputStream = imgClient.getInputStream();
                outputStream = new DataOutputStream(imgClient.getOutputStream());
                //output = new PrintWriter ( client.getOutputStream(),true);

                
                System.out.println("User has connected to the service.");
            } 
            
            catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        
        
   
        
        

        
        @Override
        public void run() {
            

            while (!this.imgClient.isClosed()) {
                
                try {
                    
                    
                    
                    // When a new image thread is set up, load the image for the user from the database (if it exists) and send it
                    
                    // Sending - once client receives it store it locally for the duration of their login
                    //System.out.println(connectedClients.get(0).getUsername());
                    // Load the avatar from the database
                    if (this.isInitialising && queryHandler.avatarExists(usernames.get(imgSockets.indexOf(this)))) {
                        
                        BufferedImage img = queryHandler.retrieveAvatar(usernames.get(imgSockets.indexOf(this)));
            
                        ImageIO.write(img, "jpg", byteArrayOutputStream);

                        /*byte[] size = byteArrayOutputStream.toByteArray();

                        outputStream.write((Integer.toString(size.length)).getBytes());
                        outputStream.write(size,0,size.length);*/
                        
                        this.isInitialising = false;
                    }
                    
                    else {
                        BufferedImage img = ImageIO.read(new File("res/img.jpg"));
                        ImageIO.write(img, "jpg", byteArrayOutputStream);
                        
                    }
                    
                    
                    
                    
                    byte[] size1 = byteArrayOutputStream.toByteArray();

                    outputStream.write((Integer.toString(size1.length)).getBytes());
                    outputStream.write(size1,0,size1.length);
                    
                    
                    
                    // Receiving
                    byte[] sizeAr = new byte[4];
                    inputStream.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                    byte[] imageAr = new byte[size];
                    inputStream.read(imageAr);

                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
                    

                    System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
                    ImageIO.write(image, "jpg", new File("res/temp/img.jpg"));
                    
                    queryHandler.storeAvatar(connectedClients.get(imgSockets.indexOf(this)).clientUsername, new File("res/temp/img.jpg"));
                    System.out.println("running!");
                } 
                
                catch (IOException | NullPointerException ex) {
                    System.out.println("Image operations cancelled - image socket closed.");
                    
                }
                
                finally {
                    try {
                        outputStream.close();
                        inputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
              
                
                
            }
            
 
        }
        

        
        
    }
    
    
}
