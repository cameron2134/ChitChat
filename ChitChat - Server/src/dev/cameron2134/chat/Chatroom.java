
package dev.cameron2134.chat;

import java.util.ArrayList;
import java.util.List;


public class Chatroom {
    
    
    // Have a Chatroom class, and a Chatroom list to hold all of the currently active chatrooms, then iterate through themto get the
    // user list for each room, then iterate through that to send messages to all members
    
    
    private String roomName, creatorUsername;
    private List<String> usersInChatroom = new ArrayList<String>();
    
    
    /**
     * Create a new chatroom object with the specified room name and creator name.
     * @param roomName The name for the chatroom.
     * @param creatorUsername The user who created the chatroom.
     */
    public Chatroom(String roomName, String creatorUsername) {
        
        this.roomName = roomName;
        this.creatorUsername = creatorUsername;
        
    }
    
    
    
    /**
     * Adds a user to the list of users in this chatroom.
     * @param username The user to add.
     */
    public void addUser(String username) {
        this.usersInChatroom.add(username);
    }
    
    
    /**
     * Removes a user from the list of users in this chatroom.
     * @param username The user to remove.
     */
    public void removeUser(String username) {
        this.usersInChatroom.remove(username);
    }
    
    
    
    /**
     * Get the name of this room.
     * @return The name of this room.
     */
    public String getRoomName() {
        return this.roomName;
    }
    
    /**
     * Get the username of the user who created this chatroom.
     * @return The username of the creator.
     */
    public String getCreatorUsername() {
        return this.creatorUsername;
    }
    
    /**
     * Get the list of all users in this chatroom.
     * @return The list of users.
     */
    public List<String> getUsersInChatroom() {
        return this.usersInChatroom;
    }
    
    
}
