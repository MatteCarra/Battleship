package com.battleship;

import java.awt.*;
import javax.swing.ImageIcon;

public class ShipPiece {
	private Image shipPieceAlive;
	private boolean shipIsDead;

	public ShipPiece() {
		shipPieceAlive = new ImageIcon("Player1.png").getImage();
		shipIsDead = false;
	}

	public void setShipImage(String file) {
		shipPieceAlive = new ImageIcon(file).getImage();

	}
	public Image getShipImage() {
		return shipPieceAlive;
	}

	public void destroy() {
		shipIsDead = true;
		setShipImage("Player1Hit.png");
	}

	/*
	 * Returns if the ship piece is destroyed
	 */
	public boolean isDestroy() {
		return shipIsDead;
	}

}
