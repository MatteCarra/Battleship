package com.battleship;

import java.awt.*;
import javax.swing.ImageIcon;

public class ShipPiece {
	private int id;
	private Image shipPieceAlive;
	private boolean shipIsDead;

	public ShipPiece(int id) {
		this.id = id;
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
		System.out.println("Destroying!");
	}

	/*
	 * Returns if the ship piece is destroyed
	 */
	public boolean isDestroy() {
		return shipIsDead;
	}

	public int getId() {
		return id;
	}
}
