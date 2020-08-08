package com.company;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.sound.midi.*;
import javax.swing.*;


public class BeatBox {
    JPanel mainPanel;
    JFrame frame;
    ArrayList<JCheckBox> checkBoxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;

    String []InstrumentNames = {"Bass Drumbase", "Closed Hi-Hat box", "Open Hi-Hat", "Acoustic Snare", "Crash CYmbal",
                                   "Hand Ball","High Tom", "Hi Bongo"};
    int []instruments ={35, 42, 46, 38, 49, 39, 50, 60};

    public void buildGUI(){
        frame =new JFrame("Cyber BeatBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkBoxList = new ArrayList<JCheckBox>();

        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton TempoUp = new JButton("Tempo Up");
        stop.addActionListener(new MyTempoUpListener());
        buttonBox.add(TempoUp);

        JButton TempDown = new JButton("Tempo Down");
        stop.addActionListener(new MyTempoDownListener());
        buttonBox.add(TempDown);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i =0; i<8; i++){
            nameBox.add(new Label(InstrumentNames[i]));
        }

        panel.add(BorderLayout.EAST, buttonBox);
        panel.add(BorderLayout.WEST, nameBox);

        frame.getContentPane().add(panel);

        GridLayout gridlayout = new GridLayout(8,8);
        gridlayout.setVgap(1);
        gridlayout.setHgap(2);

        mainPanel = new JPanel(gridlayout);
        panel.add(BorderLayout.CENTER, mainPanel);

        for(int i = 0; i<64; i++){
            JCheckBox c =new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);

            setupMIDI();

            frame.setBounds(50,50,300,300);
            frame.pack();
            frame.setVisible(true);

        }
    }

    public void setupMIDI(){
        try{
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence=new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);

        }
        catch(Exception e){
            e.printStackTrace();

        }
    }

    public void buildTrackandStart(){
        int []tracklist = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for(int i =0; i<8; i++){
            tracklist = new int[8];
            int key = instruments[i];

            for(int j=0; j<8; j++){
                JCheckBox jc =(JCheckBox) (checkBoxList.get(j + (8*i)));
                if(jc.isSelected()){
                    tracklist[j] = key;
                }
                else{
                    tracklist[j] = 0;
                }

            }

            makeTracks(tracklist);
            track.add(makeEvent(176,1, 127,0,16));
        }

        track.add(makeEvent(192,9,1,0,15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);

        }
        catch(Exception e){
            e.printStackTrace();
        }


    }

    public class MyStartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackandStart();
        }
    }

    public class MyStopListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class MyTempoUpListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempofactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempofactor*1.03));

        }
    }

    public class MyTempoDownListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempofactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempofactor*0.97));

        }
    }

    public void makeTracks(int []list){

        for(int i=0;i<8;i++){
            int key =list[i];
            if(key!= 0){
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(128, 9,key, 100,i+1));

            }
        }
    }

    public MidiEvent makeEvent(int comm, int chan, int one, int two, int tick){
        MidiEvent event =null;
        try{
            ShortMessage a = new ShortMessage();
            a.setMessage(comm,chan,one,two);
            event = new MidiEvent(a, tick);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return event;
    }
}
