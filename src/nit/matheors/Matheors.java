package nit.matheors;

import ddf.minim.Minim;
import nit.matheors.modes.Game;
import nit.matheors.modes.GameEnding;
import nit.matheors.modes.MatheorsMode;
import nit.matheors.modes.MainMenu;
import processing.core.PApplet;
import processing.core.PFont;

public class Matheors extends PApplet implements MatheorsConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static transient final Object MUTEX = new Object();
	
	private GameMode mode = GameMode.MAIN_MENU;
	
	private PFont defaultFont;	

	public PFont getDefaultFont() {
		return defaultFont;
	}
	
	private MatheorsMode mainMenu;
	private MatheorsMode currentGame;
	private MatheorsMode currentEnding;
	
	private Minim minim;
	
	public void startGame(int noOfPlayers) {
		currentGame = new Game(this, noOfPlayers);
		currentGame.setup();
		mode = GameMode.GAME;
	}
	
	public void endCurrentGame() {
		currentGame.tidyUp();
		currentEnding = new GameEnding(this, (Game) currentGame);
		currentEnding.setup();
		mode = GameMode.ENDING;
	}
	
	public void mainMenu() {
		mode = GameMode.MAIN_MENU;
	}

	public GameMode getMode() {
		return mode;
	}

	public void setup() {
		if (DEBUG) {
			System.out.println("FPS: " + FPS);
			System.out.println("MILLIS_DELAY_PER_DRAW: " + MILLIS_DELAY_PER_DRAW);
			System.out.println("SECONDS_PER_TICK: " + SECONDS_PER_TICK);
		}
		size(round(SCREEN_WIDTH), round(SCREEN_HEIGHT));		
		defaultFont = loadFont("Arial-BoldMT-48.vlw");
		minim = new Minim(this);
		
		mainMenu = new MainMenu(this);
		mainMenu.setup();
	}
	
	public void draw() {
		frameRate(FPS);
		
		try {
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
		} catch (Exception e) {
			throw new RuntimeException("An error occurred during the game", e);
		}
		
	}

	public Minim getMinim() {
		return minim;
	}

}
