package nit.matheors.modes;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import processing.core.PConstants;
import processing.core.PImage;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;

public class MainMenu extends GameComponent implements MatheorsMode, PConstants {

	public MainMenu(Matheors parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	private PImage mainmenu = null;
	
	@Override
	public void setup() {
		mainmenu = getParent().loadImage("images\\mainmenu.png");
		
		getParent().addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// nothing to do here				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// nothing to do here				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {				
				if (e.getKeyChar() == '1') {
					getParent().startGame(1);
				}				
				if (e.getKeyChar() == '2') {
					getParent().startGame(2);
				}				
				if (e.getKeyChar() == 'e') {
					getParent().startGame(1);
				}				
			}
		});
		
	}

	@Override
	public void draw() {
		getParent().imageMode(CORNER);
		getParent().image(mainmenu, 0, 0);
		
	}
	
	
}
