package nit.matheors.modes;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ddf.minim.AudioSample;

import static processing.core.PApplet.abs;
import processing.core.PConstants;
import processing.core.PImage;
import nit.matheors.GameComponent;
import nit.matheors.GameMode;
import nit.matheors.Matheors;
import nit.matheors.MatheorsConstants;

public class GameEnding extends GameComponent implements MatheorsMode, PConstants, MatheorsConstants {

	private Game game;
	
	public GameEnding(Matheors parent, Game currentGame) {
		super(parent);
		this.game = currentGame;
	}

	private PImage bg = null;
	private boolean rendered = false;
	private AudioSample winAudioSample;
	private AudioSample loseAudioSample;
	
	private void loadSounds() {
		winAudioSample = getParent().getMinim().loadSample("sounds\\player_wins.mp3");
		loseAudioSample = getParent().getMinim().loadSample("sounds\\player_loses.mp3");
	}
	
	@Override
	public void setup() {
		loadSounds();
		bg = getParent().loadImage("images\\endingbg.jpg");
		
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
				if (getParent().getMode() == GameMode.ENDING) {
					if (e.getKeyCode() == ENTER) {
						tidyUp();
						getParent().mainMenu();
					}				
				}
			}
		});
		
	}

	@Override
	public void draw() {
		if (!rendered) {
			AudioSample appropriateSample = null;
			String message = "";
			if (game.getNoOfPlayers() == 1) {
				if (game.getP1Score() == game.getTargetNumber()) {
					message = "Player 1, you won!";
					appropriateSample = winAudioSample;
				} else {
					message = "Player 1, you lost!";
					appropriateSample = loseAudioSample;					
				}
			} else {
				// Determine who of the players was the closest
				int p1gap = abs(game.getTargetNumber() - game.getP1Score());
				int p2gap = abs(game.getTargetNumber() - game.getP2Score());
				int winningGap = 0;
				if (p1gap == p2gap) {
					message = "It's a tie!";
					appropriateSample = winAudioSample;
					winningGap = p1gap;
				} else if (p1gap > p2gap) {
					message = "Player 2, you won!";					
					appropriateSample = winAudioSample;
					winningGap = p2gap;
				} else if (p1gap < p2gap) {
					message = "Player 1, you won!";					
					appropriateSample = winAudioSample;
					winningGap = p1gap;
				}
				if (winningGap > (game.getTargetNumber() * 0.2)) {
					message = "You are both way off the mark!";					
					appropriateSample = loseAudioSample;					
				}
				
			}
			
			getParent().imageMode(CORNERS);
			getParent().tint(255,127);
			getParent().image(bg, 0, 0);
			getParent().tint(255,255);
			rendered = true;

			getParent().textMode(CENTER);
			getParent().text("Game Over\n" + message + "\nPress ENTER to go back", HALF_WIDTH, HALF_HEIGHT - 50);
			
			appropriateSample.trigger();
		}	
		
	}

	@Override
	public void tidyUp() {
		winAudioSample.close();
		loseAudioSample.close();
	}	
	
}
