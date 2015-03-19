
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mb1994
 */
public class MusicThread {

    Map locationMapper;
    JTable target;
    int row;
    Thread songThread;

    public MusicThread(Map locationMapper, JTable target, int row) {
        this.locationMapper = locationMapper;
        this.target = target;
        this.row = row;
    }

    public void playMusic() throws InterruptedException {

        songThread = new Thread() {

            String location;
            FileInputStream fis;
            Player playMP3;

            @Override
            public void run() {
                try {

                    for (;; row++) {
                        
                        if (row == StartGUI.songs) {
                            row = 0;
                        }
                        
                        location = locationMapper.get(target.getValueAt(row, 0)).toString();
                        
                        fis = new FileInputStream(location);
                        playMP3 = new Player(fis);
                        playMP3.play();
                        
                        //highlight the corresponding row.
                        
                        playMP3.close();
                    }
                } catch (FileNotFoundException | JavaLayerException ex) {
                    Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        songThread.start();
        songThread.join();
    }
    
    public void killThread() {
        songThread.stop();
    }
    
    public void suspendThread() {
        songThread.suspend();
    }
    
    public void resumeThread() {
        songThread.resume();
    }
}