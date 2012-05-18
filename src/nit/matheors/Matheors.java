package nit.matheors;

import nit.matheors.modes.Game;
import nit.matheors.modes.MatheorsMode;
import nit.matheors.modes.MainMenu;
import processing.core.PApplet;
import processing.core.PFont;

public class Matheors extends PApplet implements MatheorsSettings {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static transient final Object MUTEX = new Object();
	
	private enum Mode { MAIN_MENU, GAME, ENDING }
	
	private Mode mode = Mode.MAIN_MENU;
	
	private PFont defaultFont;	

	public PFont getDefaultFont() {
		return defaultFont;
	}
	
	private MatheorsMode mainMenu;
	private MatheorsMode currentGame;
	private MatheorsMode currentEnding;
	
	public void startGame(int noOfPlayers) {
		currentGame = new Game(this);
		currentGame.setup();
		mode = Mode.GAME;
	}
	
	public void setup() {
		if (DEBUG) {
			System.out.println("FPS: " + FPS);
			System.out.println("MILLIS_DELAY_PER_DRAW: " + MILLIS_DELAY_PER_DRAW);
			System.out.println("SECONDS_PER_TICK: " + SECONDS_PER_TICK);
		}
		size(round(SCREEN_WIDTH), round(SCREEN_HEIGHT));		
		defaultFont = loadFont("CourierNewPSMT-22.vlw");
		
		mainMenu = new MainMenu(this);
		mainMenu.setup();
	}
	
	public void draw() {
		switch (mode) {
			case MAIN_MENU:
				mainMenu.draw();
				break;
			case GAME:
				currentGame.draw();
				break;
			case ENDING:
				currentEnding.draw();
				break;
		}
		
	}

}
