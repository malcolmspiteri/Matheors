package nit.matheors.modes;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ddf.minim.AudioPlayer;

import processing.core.PConstants;
import processing.core.PImage;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;

public class MainMenu extends GameComponent implements MatheorsMode, PConstants {

	public MainMenu(Matheors parent) {
		super(parent);
		loadSounds();
	}

	private PImage mainmenu = null;
	AudioPlayer music;
	
	private void loadSounds() {
		music = getParent().getMinim().loadFile("sounds\\main_menu_music.mp3");
	}
	
	@Override
	public void setup() {
		mainmenu = getParent().loadImage("images\\mainmenu.png");
		music.loop();
		
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
					music.pause();
					getParent().startGame(1);
				}				
				if (e.getKeyChar() == '2') {
					music.pause();
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
		if (!music.isLooping())
			music.loop();
		
		getParent().imageMode(CORNER);
		getParent().image(mainmenu, 0, 0);
		
	}

	@Override
	public void tidyUp() {
		music.close();		
	}
	
	
}
