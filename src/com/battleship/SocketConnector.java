package com.battleship;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by matteo on 22/05/17.
 */
public class SocketConnector {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    public SocketConnector(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public Object[][] readShips() {
        try {
            Object[][] enemyShips = new Object[10][10];
            for(int i = 0; i < enemyShips.length; i++) {
                for(int j = 0; j < enemyShips[i].length; j++) {
                    int t = inputStream.readInt();
                    if(t == 0) {
                        enemyShips[i][j] = new ShipPiece();
                    } else {
                        enemyShips[i][j] = 1;
                    }

                }
            }
            return enemyShips;
        } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    public Point readMove() {
        try {
            return new Point(inputStream.readInt(), inputStream.readInt());
        } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    public void writeShips(Object[][] ships) {
        try {
            System.out.println(ships.length);
            System.out.println(ships[0].length);
            for(int i = 0; i < ships.length; i++) {
                for(int j = 0; j < ships[i].length; j++) {
                    Object o = ships[i][j];
                    if(o instanceof ShipPiece) {
                        outputStream.writeInt(0);
                    } else {
                        outputStream.writeInt(1);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void close() throws IOException {
        this.socket.close();
    }

    public void writeMove(int x, int y) {
        try {
            outputStream.writeInt(x);
            outputStream.writeInt(y);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
}
