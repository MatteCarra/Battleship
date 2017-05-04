import listeners.PlayListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class MainMenu implements ActionListener {

	private final PlayListener listener;
	private JFrame window;
	private JButton play;
	private volatile boolean isImageVisible;
	
	public MainMenu(JFrame theWindow, PlayListener listener){
		window = theWindow;
		play = new JButton("Play");
		isImageVisible = true;
		this.listener = listener;
	}
	
	public void loadTitleScreen() {
		play.setSize(window.getContentPane().getWidth(), window.getContentPane().getHeight());
		play.addActionListener(this);
		play.setLocation(0, 0);
		window.getContentPane().add(play);
		play.setVisible(true);
		window.setVisible(true);
		window.getContentPane().revalidate();
		window.getContentPane().repaint();
	}

	public boolean isImageVisible(){
		return isImageVisible;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		window.getContentPane().remove(play);
		play.removeActionListener(this);
		window.getContentPane().revalidate();
		window.getContentPane().repaint();
		isImageVisible = false;
		listener.onPlayClicked();
	}
}
