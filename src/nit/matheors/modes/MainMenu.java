package nit.matheors.modes;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ddf.minim.AudioPlayer;

import processing.core.PConstants;
import processing.core.PImage;
import nit.matheors.GameComponent;
import nit.matheors.GameMode;
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
				if (getParent().getMode() == GameMode.MAIN_MENU) {
					if (e.getKeyChar() == '1') {
						getParent().startGame(1);
						music.pause();
					}				
					if (e.getKeyChar() == '2') {
						getParent().startGame(2);
						music.pause();
					}				
					if (e.getKeyChar() == 'e') {
						getParent().exitMatheors();
					}
				}
			}
		});
		
	}

	@Override
	public void draw() {
		getParent().background(mainmenu);
		/*getParent().imageMode(CORNERS);
		getParent().image(mainmenu, 0, 0);*/

		if (!music.isPlaying())
			music.loop();
		
	}

	@Override
	public void tidyUp() {
		music.close();		
	}
	
	
}
