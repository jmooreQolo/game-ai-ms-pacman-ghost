package game.controllers;

import game.core.Game;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public final class Human extends KeyAdapter implements PacManController
{
    private int key;

    public int getAction(Game game,long dueTime)
    {
    	if (key == KeyEvent.VK_UP)
            return 0;
    	if (key == KeyEvent.VK_RIGHT)
            return 1;    	
        if (key == KeyEvent.VK_DOWN)
            return 2;
        if (key == KeyEvent.VK_LEFT)
            return 3;
        if (key == KeyEvent.VK_1){
        	return 5;
        }
        if (key == KeyEvent.VK_2){
        	return 6;
        }
        

        return 4;
    }

    public void keyPressed(KeyEvent e) 
    {
        key=e.getKeyCode();
        
    }
}