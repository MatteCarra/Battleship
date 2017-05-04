import listeners.AttackListener;
import listeners.SelectListener;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameLogic implements SelectListener, AttackListener {
	
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

    public void connect(){
	    //TODO ti devi connettere e iniziare ad attendere il pacchetto delle enemyShips
    }
	
	public void startGame() throws InterruptedException {
		gameRunning = true;
		connect();
		
		MainMenu startMenu = new MainMenu(frame);
		startMenu.loadTitleScreen();

		while(startMenu.isImageVisible()){
            //Todo non so se sia opportuno mettere lo sleep e non ho voglia di controllare
            Thread.sleep(100);
        }
		
		p1Ships = initializeShipCreation();

		//TODO devi mandare le tue navi al tuo nemico
        Object[][] myShips = chooseShipPositions(p1Ships);

        p2Ships = null;
        Object[][] enemyShips = null;
        //TODO devi ricevere le navi del nemico e magari mostrare una schermata di attesa se lui non le ha già mandate

        if(enemyShips != null) {
        	//TODO si devono ricevere ShipPiece e le loro posizioni in una sorta di chiave valori
            //TODO esempio: nave 1 (X=5, Y=5) (X=6, y=5)
            p2Ships = null;
            enemyShips = null;
            grid = new Grid(enemyShips, this);
        } else {
            p2Ships = initializeShipCreation();
            grid = new Grid(randomizeGrid(p2Ships), this);
            //TODO Quando avrai implementato i socket devi chiudere il gioco in questo caso
            // e cancellare queste due righe qui sopra aggiunte solo per debuggare
            //System.exit(0);
            //return;
        }

        //TODO Ricevi l'ok dal nemico
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

        betweenTurns = new BetweenTurnsScreen((JPanel) frame.getContentPane(), grid, small, this);

        //TODO ottieni questo valore dal server per sapere se è il tuo turno.
        boolean ilMioturno = false;

        if(!ilMioturno) {
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
	
	private Object[][] chooseShipPositions(Ship[] ships) throws InterruptedException {
		GridCreator creator = new GridCreator(ships, BOARD_SIZE, frame);
		creator.setup();
		frame.getContentPane().add(creator);
		frame.getContentPane().repaint();
		frame.setVisible(true);
		while (!creator.isSetupOver()) {
		    Thread.sleep(100);
        }
		frame.getContentPane().removeAll();
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		
		return creator.getGridArray();
	}

    private Object[][] randomizeGrid(Ship[] ships){
        GridCreator creator = new GridCreator(ships, BOARD_SIZE, frame);
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
        //TODO manda all'avversario la selezione che hai fatto
        checkEnd();

        if (!grid.isTurn() && gameRunning){
            betweenTurns.loadTurnScreen();
        }
    }

    @Override
    public void onAttack() {
        checkEnd();
    }
}