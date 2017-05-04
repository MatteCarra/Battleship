import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BetweenTurnsScreen {

	private JPanel window;
	private ImageIcon backgroundImageIcon;
	private JLabel bkgImageContainer;
	private volatile boolean isImageVisible;
	private Grid grid;
	private SmallGrid small;
	
	public BetweenTurnsScreen(JPanel theWindow, Grid grid, SmallGrid small){
		window = theWindow;
		backgroundImageIcon = new ImageIcon("WaitForOpponentsMove.png");
		Image bkgImage = backgroundImageIcon.getImage();
		Image scaledBkgImage = bkgImage.getScaledInstance(window.getWidth(),
				window.getHeight(), BufferedImage.SCALE_FAST);
		ImageIcon scaledBkgImageIcon = new ImageIcon(scaledBkgImage);
		bkgImageContainer = new JLabel(scaledBkgImageIcon);
		bkgImageContainer.setSize(window.getWidth(), 
				window.getHeight());
		bkgImageContainer.setLocation(0, 0); 
		isImageVisible = true;
		this.grid = grid;
		this.small = small;

		//TODO Ricevi la mossa dell'avversario e invoca il metodo onInfoReceived.
	}
	
	public void loadTurnScreen() {
		window.add(bkgImageContainer);
		window.setVisible(true);
		window.repaint();
	}

	public boolean isImageVisible(){
		return isImageVisible;
	}

	public void onInfoReceived(int x, int y) {
		boolean colpito = small.attack(x, y);
		//TODO opzionale manda colpito al client
		// (perchè il client sa già se è stato colpito perchè all'inizio del gioco si sono condivisi le informazioni)

		window.remove(bkgImageContainer);
		window.revalidate();
		window.repaint();
		grid.setTurn(true);
		grid.setVisible(true);
		small.setVisible(true);
	}
}
