
package dev.cameron2134.chatclient;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;


public class TTS implements Runnable {


    private String msg;
    
    
    /**
     * Create a new TTS to read the specified message.
     * @param msg The message to read.
     */
    public TTS(String msg) {
        this.msg = msg;
    }
    
    
    @Override
    public void run() {
        
        VoiceManager vm = VoiceManager.getInstance();
        Voice voice = vm.getVoice("kevin16");

        voice.allocate();


        voice.speak(this.msg);
        voice.deallocate();
       
    }
    
    
    
    
    
}
