
package dev.cameron2134.chatclient.display;

import dev.cameron2134.chatclient.Client;
import java.io.IOException;


public abstract class GUI extends javax.swing.JFrame {

    /**
     * The Client object used to communicate with the server.
     */
    protected Client client;
    
    /**
     * Username of the user opening the UI.
     */
    protected String username;
    
    /**
     * Whether or not text to speech is enabled for chat windows.
     */
    protected boolean ttsEnabled;
    
    
    
    
    /**
     * Set the look and feel of the application.
     */
    protected void feel() {

        try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(GUI_ChatRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(GUI_ChatRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(GUI_ChatRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(GUI_ChatRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

    }

    
    

    
    
    /**
     * Abstract method for UI components that require listeners.
     */
    public abstract void createListeners();
    

}
