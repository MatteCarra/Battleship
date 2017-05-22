package com.battleship;

import com.battleship.listeners.PlayListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MainMenu implements ActionListener {

	private final PlayListener listener;
	private JFrame window;
	private JButton joinMatch;
	private JButton createMatch;
	private volatile boolean isImageVisible;
	
	public MainMenu(JFrame theWindow, PlayListener listener){
		window = theWindow;
		joinMatch = new JButton("Join match");
		createMatch = new JButton("Create match");
		isImageVisible = true;
		this.listener = listener;
	}
	
	public void loadTitleScreen() {
		joinMatch.setSize(window.getContentPane().getWidth(), window.getContentPane().getHeight() / 2);
		joinMatch.addActionListener(this);
		joinMatch.setLocation(0, 0);
        createMatch.setSize(window.getContentPane().getWidth(), window.getContentPane().getHeight() / 2);
        createMatch.addActionListener(this);
        createMatch.setLocation(0, window.getContentPane().getHeight()/2);
        window.getContentPane().add(joinMatch);
        window.getContentPane().add(createMatch);
		joinMatch.setVisible(true);
		createMatch.setVisible(true);
		window.setVisible(true);
		window.getContentPane().revalidate();
		window.getContentPane().repaint();
	}

	public boolean isImageVisible(){
		return isImageVisible;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		window.getContentPane().remove(joinMatch);
        window.getContentPane().remove(createMatch);
        joinMatch.removeActionListener(this);
        createMatch.removeActionListener(this);
		window.getContentPane().revalidate();
		window.getContentPane().repaint();
		isImageVisible = false;

        if(e.getSource() == joinMatch) {
            String ip = (String) JOptionPane.showInputDialog("Insert the ip address:");
            listener.onJoinMatchClicked(ip);
        } else {
            listener.onCreateMatchClicked();
        }

	}
}
