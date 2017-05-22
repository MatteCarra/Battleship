package com.battleship;

import com.battleship.listeners.AttackListener;
import com.battleship.listeners.PlayListener;
import com.battleship.listeners.SelectListener;
import com.battleship.listeners.SetupListener;

import java.awt.Color;
import java.awt.Dimension;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class GameLogic implements SelectListener, AttackListener, PlayListener, SetupListener {
	
	private final static int BOARD_SIZE = 10;
	
	private final static int BATTLESHIP_SIZE = 4;
	private final static int CRUISER_SIZE = 3;
	private final static int DESTROYER_SIZE = 2;
	private final static int SUBMARINE_SIZE = 1;
	
	private final static int BATTLESHIP_COUNT = 1;
	private final static int CRUISER_COUNT = 2;
	private final static int DESTROYER_COUNT = 3;
	private final static int SUBMARINE_COUNT = 4;
	private JFrame frame;
	private boolean gameRunning;

	private Grid grid;
	private SmallGrid small;
	private BetweenTurnsScreen betweenTurns;
	private Ship[] p1Ships;
	private Ship[] p2Ships;

	private SocketConnector connector;
	private boolean isServer = true;
	
	public void setUpWindow() {
		frame = new JFrame();
		
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(900, 615));
		frame.setMinimumSize(new Dimension(900, 615));
		frame.setResizable(false);
		frame.pack();

        try {
            startGame();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void  mainMenu() {
        MainMenu startMenu = new MainMenu(frame, this);
        startMenu.loadTitleScreen();
    }


	public void startGame() throws InterruptedException {
        betweenTurns = null;
        grid = null;
        small = null;
        p2Ships = null;
        p1Ships = null;

        gameRunning = true;
        mainMenu();

    }

    public void startPlaying(SocketConnector socketConnector) throws InterruptedException {
        this.connector = socketConnector;

        p1Ships = initializeShipCreation();

        chooseShipPositions(p1Ships);
	}

    @Override
    public void onSetupComplete(Object[][] myShips) {
        frame.getContentPane().removeAll();
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();

        connector.writeShips(myShips);

        p2Ships = initializeShipCreation();
        Object[][] enemyShips = connector.readShips();
        grid = new Grid(enemyShips, this);

        small = new SmallGrid(myShips);
        small.setLocation(grid.getWidth()+10, grid.getY());


        int windowWidth = small.getX() + small.getWidth() + 10;
        frame.setPreferredSize(new Dimension(windowWidth, frame.getHeight()));
        frame.setSize(frame.getPreferredSize());
        frame.pack();


        frame.getContentPane().add(grid); // adds the grids to the window
        frame.getContentPane().add(small);
        frame.addMouseListener(grid);
        frame.setVisible(true);

        betweenTurns = new BetweenTurnsScreen((JPanel) frame.getContentPane(), grid, small, this, connector);

        if(!isServer) {
            betweenTurns.loadTurnScreen();
        }
        //gameLoop(p1Ships, grid, small);
    }
	
	private Ship[] initializeShipCreation() {
		Ship[] battleships = createShips(BATTLESHIP_SIZE, BATTLESHIP_COUNT);
		Ship[] cruisers = createShips(CRUISER_SIZE, CRUISER_COUNT);
		Ship[] destroyers = createShips(DESTROYER_SIZE, DESTROYER_COUNT);
		Ship[] submarines = createShips(SUBMARINE_SIZE, SUBMARINE_COUNT);

		Ship[] ships = concatShipArray(battleships, cruisers);
		ships = concatShipArray(ships, destroyers);
		ships = concatShipArray(ships, submarines);

		return ships;
	}

	private Ship[] createShips(int shipSize, int numOfShips) {
		Ship[] listOfShips = new Ship[numOfShips];
		for (int i = 0; i < numOfShips; i++) {
			ShipPiece[] shipArray = new ShipPiece[shipSize];
			for (int j = 0; j < shipSize; j++) {
				ShipPiece p = new ShipPiece();
				shipArray[j] = p;
			}
			listOfShips[i] = new Ship(shipArray);
		}

		return listOfShips;
	}

	private Ship[] concatShipArray(Ship[] a, Ship[] b) {
		int aLen = a.length;
		int bLen = b.length;
		Ship[] c = new Ship[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
	
	private void chooseShipPositions(Ship[] ships) throws InterruptedException {
		GridCreator creator = new GridCreator(ships, BOARD_SIZE, frame, this);
		creator.setup();
		frame.getContentPane().add(creator);
		frame.getContentPane().repaint();
		frame.setVisible(true);
	}

    private Object[][] randomizeGrid(Ship[] ships){
        GridCreator creator = new GridCreator(ships, BOARD_SIZE, frame, this);
        creator.setup();
        creator.randomize();
        return creator.getGridArray();
    }


	public void checkEnd(){
        boolean p1AllShipsDead = true;
        boolean p2AllShipsDead = true;
        for (int i = 0; i < p1Ships.length; i++) {
            if (p1Ships[i].checkIfDead()) {
                for (int j = 0; j < p1Ships[i].getShipPieces().length; j++) {
                    p1Ships[i].getShipPieces()[j].setShipImage("Dead.png");
                }
            } else {
                p1AllShipsDead = false;
            }
        }

        for (int i = 0; i < p2Ships.length; i++) {
            if (p2Ships[i].checkIfDead()) {
                for (int j = 0; j < p2Ships[i].getShipPieces().length; j++)
                    p2Ships[i].getShipPieces()[j].setShipImage("Dead.png");
            } else {
                p2AllShipsDead = false;
            }
        }

        grid.repaint();
        small.repaint();

        if (p1AllShipsDead || p2AllShipsDead) {
            gameRunning = false;
            for (int i = 0; i < grid.getArray().length; i++) {
                for (int j = 0; j < grid.getArray()[i].length; j++) {
                    if ((grid.getArray()[i][j].equals(1))) {
                        grid.getArray()[i][j] = 0;
                    }
                }
            }
            GameOverScreen gameOver = new GameOverScreen(frame, !p1AllShipsDead);
            gameOver.loadEndScreen();
        }
    }

    @Override
    public void onSelection(int x, int y) {
        checkEnd();

        if (!grid.isTurn() && gameRunning){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    connector.writeMove(x, y);
                    betweenTurns.loadTurnScreen();
                }
            }).start();
        }
    }

    @Override
    public void onAttackReceived() {
        checkEnd();
    }

    @Override
    public void onJoinMatchClicked(String ip) {
        try {
            isServer = false;
            startPlaying(new SocketConnector(new Socket(ip, 7878)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateMatchClicked() {
	    new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(7878);
                    Socket s = serverSocket.accept();
                    System.out.println("Socket accepted!");
                    startPlaying(new SocketConnector(s));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }).start();
    }
}