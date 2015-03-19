
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
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
public class StartGUI extends javax.swing.JFrame {

    /**
     * Creates new form StartGUI
     */
    private String[] validExtensions = {".mp3", ".wav"};
    public static HashMap locationMapper = new HashMap();
    public static int songs = 0;
    private double totalSize = 0.0d;
    public static Thread songThread;
    private boolean paused = false;
    private Player playMP3;
    private FileInputStream fis;
    private MusicThread musicThread;

    private String getDuration(Map properties) throws UnsupportedAudioFileException, IOException {

        Long duration = (Long) properties.get("duration");

        String minutes = Long.toString(TimeUnit.MICROSECONDS.toMinutes(duration));
        String seconds = Long.toString(TimeUnit.MICROSECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MICROSECONDS.toMinutes(duration)));

        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        return String.format(minutes + ":" + seconds);
    }

    private boolean isValidExtension(String extension) {

        for (String ext : validExtensions) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }
        }

        return false;
    }

    private String getExtension(File file) {
        int lastDot = file.getName().lastIndexOf(".");

        if (lastDot == -1) {
            return null;
        }

        return file.getName().substring(lastDot);
    }

    private void addMusicToTable(File audioFile) throws UnsupportedAudioFileException, IOException {

        /*
         This function adds music file to the table
         and also stores the file details in another folder.
         This folder will be needed when we restart the player.
         */
        AudioFileFormat baseFileFormat = null;
        Map properties = null;

        try {
            baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(audioFile);
            properties = baseFileFormat.properties();

        } catch (Exception ex) {
            System.out.println("error occured for file:" + audioFile.getName());
            return;
        }

        String[] data = new String[8];

        //setting the Title.
        data[0] = audioFile.getName().replace(getExtension(audioFile), "");
        //"Title", "Duration", "Bit Rate", "Artist", "Album", "Rating", "Genre", "Year"
        //data[0] = (String) properties.get("title");doesnt work properly.
        data[1] = getDuration(properties);

        try {
            data[2] = Integer.toString((int) properties.get("mp3.bitrate.nominal.bps") / 1000);
        } catch (Exception e) {
            data[2] = "";
            System.out.println(e.toString());
        }

        data[3] = (String) properties.get("author");
        data[4] = (String) properties.get("album");
        data[6] = (String) properties.get("mp3.id3tag.genre");
        data[7] = (String) properties.get("date");

        ((DefaultTableModel) jTable1.getModel()).addRow(data);

        //add it to current hashmap..
        locationMapper.put(data[0], audioFile.getAbsolutePath());
        songs++;
        totalSize += (int) properties.get("mp3.length.bytes") / (1024.0 * 1024.0);
        String size = String.format("%.2f", totalSize);

        jLabel1.setText(Integer.toString(songs) + " songs added. size: " + size + " MB");

        //also add it to some permanent storage..
    }

    private class MyCustomFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".mp3");
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "mp3 files (*.mp3), wav files (.wav)";
        }
    }

    class MyCustomFilter2 extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory();
        }

        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "Directories";
        }
    }

    public StartGUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jFileChooser2 = new javax.swing.JFileChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();

        jFileChooser1.setFileFilter(new MyCustomFilter());

        jFileChooser2.setDialogTitle("Choose a folder to import..");
        jFileChooser2.setFileFilter(new MyCustomFilter2());
        jFileChooser2.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BMPlayer");

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            null,
            new String [] {
                "Title", "Duration", "Bit Rate", "Artist", "Album", "Rating", "Genre", "Year"
            }
        ));
        jTable1.setCellEditor(null);
        jTable1.setColumnSelectionAllowed(true);
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                doubleClickHandler(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("0 songs added. size : 0.0 MB");

        jToggleButton1.setText("PLAY");
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(226, 226, 226)
                .addComponent(jToggleButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jToggleButton1)
                .addContainerGap())
        );

        jMenu1.setText("File");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenu3.setText("Import..");

        jMenuItem3.setText("File");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuItem2.setText("Folder");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenu1.add(jMenu3);

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu4.setText("About");
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        int returnVal = jFileChooser1.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File music = jFileChooser1.getSelectedFile();
            try {
                addMusicToTable(music);
            } catch (UnsupportedAudioFileException | IOException ex) {
                Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:

        //first store the hashmap
        File file = new File("SongsData.ser");

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("/home/mb1994/address.ser");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fout);
        } catch (IOException ex) {
            Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            oos.writeObject(locationMapper);
        } catch (IOException ex) {
            Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        //AudioFileFormat aff

        int returnVal = jFileChooser2.showOpenDialog(this);
        jFileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            //get all audio files in the directory recursively..
            ArrayList<File> dirs = new ArrayList<>();

            dirs.add(jFileChooser2.getSelectedFile());
            System.out.println(dirs.get(0).getAbsolutePath());

            while (!dirs.isEmpty()) {

                System.out.println("Current dir=" + dirs.get(0).getAbsolutePath());

                //continue;
                //go thru all its files..
                File[] files = dirs.get(0).listFiles();

                if (files == null) {
                    dirs.remove(0);
                    continue;
                }

                for (File file : files) {

                    if (file.isHidden() || file.getName().startsWith(".")) {
                        continue;
                    } else if (file.isDirectory()) {
                        dirs.add(file);
                    } else {
                        //if its a valid extension..then add it to the table.
                        if (isValidExtension(getExtension(file))) {
                            try {
                                addMusicToTable(file);
                            } catch (UnsupportedAudioFileException ex) {
                                Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }

                dirs.remove(0);
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void doubleClickHandler(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_doubleClickHandler
        // TODO add your handling code here:

        if (evt.getClickCount() == 2) {
            JTable target = (JTable) evt.getSource();
            int row = target.getSelectedRow();

            System.out.println("mouse clicked twice!");

            //access the location of file from map..
            String location = locationMapper.get(target.getValueAt(row, 0)).toString();

            //now play the music..
            try {
                FileInputStream fis = new FileInputStream(location);
                Player playMP3 = new Player(fis);
                playMP3.play();
            } catch (Exception ex) {
                Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_doubleClickHandler

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed

        //if ENTER was pressed.
        if (evt.getKeyCode() == 10) {

            //Check if a song is already being played..
            if (songThread != null) {

                System.out.println("Already running!");
                musicThread.killThread();
                songThread.stop();
            }

            //another alternative..
            //if(playMP3.) something in this..
            JTable target = (JTable) evt.getSource();
            int row = target.getSelectedRow();
            jToggleButton1.setSelected(true);
            jToggleButton1.setText("PAUSE");
            
            songThread = new Thread() {

                public void run() {
                    musicThread = new MusicThread(locationMapper, target, row);
                    try {
                        musicThread.playMusic();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            songThread.start();

        } //if SPACE was pressed.
        else if (evt.getKeyCode() == 32) {
            //This means i have to pause the song.

            System.out.println("Space was pressed!");

            //if a song was started..
            if (songThread.isAlive()) {

                System.out.println("thread is alive!");
                //if it's paused..
                if (paused) {
                    //then resume it..
                    System.out.println("was already paused. resuming now..");
                    
                    jToggleButton1.setSelected(true);
                    jToggleButton1.setText("PAUSE");
                    
                    musicThread.resumeThread();
                    songThread.resume();
                    paused = false;
                } else {
                    System.out.println("thread was not paused! pausing now..");
                    
                    jToggleButton1.setSelected(false);
                    jToggleButton1.setText("PLAY");
                    
                    paused = true;
                    musicThread.suspendThread();
                    songThread.suspend();
                }
            }
        }
        //add other key functionality.
    }//GEN-LAST:event_jTable1KeyPressed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
        if (jToggleButton1.getText().equals("PLAY")) {

            //means it was paused before..or no song is playing now..
            jToggleButton1.setSelected(true);
            jToggleButton1.setText("PAUSE");

            if (songThread.isAlive()) {
                musicThread.resumeThread();
                songThread.resume();
            } else {

                songThread = new Thread() {
                    public void run() {
                        musicThread = new MusicThread(locationMapper, jTable1, 0);
                        try {
                            musicThread.playMusic();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                songThread.start();
            }
        } else {
            //means it was playing..this means i have to pause it now..
            jToggleButton1.setSelected(false);
            jToggleButton1.setText("PLAY");

            if (songThread != null) {
                musicThread.suspendThread();
                songThread.suspend();
            }
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void populateSongs() throws UnsupportedAudioFileException, IOException {

        Collection<String> paths;
        paths = locationMapper.values();
        for (String path : paths) {
            addMusicToTable(new File(path));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StartGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StartGUI().setVisible(true);

                /*   //read from serialized file and get back the details..
                 File file = new File("temp");
                 FileInputStream f = null;
                 try {
                 f = new FileInputStream(file);
                 } catch (FileNotFoundException ex) {
                 Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 ObjectInputStream s = null;
                 try {
                 s = new ObjectInputStream(f);
                 } catch (IOException ex) {
                 Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 try {
                 HashMap<String, Object> fileObj2 = (HashMap<String, Object>) s.readObject();
                 } catch (IOException ex) {
                 Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (ClassNotFoundException ex) {
                 Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 try {
                 s.close();
                 } catch (IOException ex) {
                 Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                 }
                
                 try {
                 populateSongs();
                 } catch (UnsupportedAudioFileException ex) {
                 Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException ex) {
                 Logger.getLogger(StartGUI.class.getName()).log(Level.SEVERE, null, ex);
                 }*/
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
