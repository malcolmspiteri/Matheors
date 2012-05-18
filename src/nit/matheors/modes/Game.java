package nit.matheors.modes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import processing.core.PConstants;
import processing.core.PImage;
import nit.matheors.Coordinates;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;
import nit.matheors.MatheorsSettings;
import nit.matheors.controls.Controller;
import nit.matheors.controls.KeyboardController;
import nit.matheors.model.Vector;
import nit.matheors.model.objects.Matheor;
import nit.matheors.model.objects.Qbject;
import nit.matheors.model.objects.Spacecraft;
import nit.matheors.model.objects.TransientQbject;

import static processing.core.PApplet.round;

public class Game extends GameComponent implements MatheorsSettings, PConstants, MatheorsMode {

	public Game(Matheors parent) {
		super(parent);
	}

	Spacecraft player1;
	Spacecraft player2;
	
	private List<Qbject> qbjects = new CopyOnWriteArrayList<Qbject>();
	private List<TransientQbject> ltqbjects = new CopyOnWriteArrayList<TransientQbject>();

	public void addQbject(Qbject o) {
		synchronized (Matheors.MUTEX) {
			qbjects.add(o);
			if (o instanceof TransientQbject)
				ltqbjects.add((TransientQbject) o);
		}
	}
	
	private PImage background = null;

	public void setup() {
		getParent().textFont(getParent().getDefaultFont(), 12);
		//getParent().fill(255);
		background = getParent().loadImage("images\\background.jpg");
		getParent().imageMode(CORNER);
		getParent().image(background, 0, 0);

		player1 = new Spacecraft(getParent(), 10, new Coordinates(HALF_WIDTH, HALF_HEIGHT), 30,
				60, new Vector(0, 0));
		Controller controllerPlayer1 = new KeyboardController(getParent(), this, UP, DOWN, RIGHT, LEFT, 1);
		controllerPlayer1.control(player1);
		
		qbjects.add(player1);
		
		qbjects.add(new Matheor(getParent(), 30, new Coordinates(100f, 100f), 100, 50,
				new Vector(300, 1)));
		qbjects.add(new Matheor(getParent(), 20, new Coordinates(500f, 200f), 75, 50,
				new Vector(40, 1)));
		qbjects.add(new Matheor(getParent(), 20, new Coordinates(200f, 500f), 75, 50,
				new Vector(40, 1)));
		qbjects.add(new Matheor(getParent(), 40, new Coordinates(600f, 700f), 125, 75,
				new Vector(40, 1)));
				
	}

	private void checkForCollisions() {
		Map<Qbject, List<Qbject>> collisions = new HashMap<Qbject, List<Qbject>>();
		for (Qbject o : qbjects) {
			if (!collisions.containsKey(o)) {
				collisions.put(o, new ArrayList<Qbject>());
			}
			for (Qbject o2 : qbjects) {
				if (!collisions.containsKey(o2)) {
					collisions.put(o2, new ArrayList<Qbject>());
				}
				if (o != o2) {
					// println("Checking " + o.name + " with " + o2.name);
					if (collisions.get(o).contains(o2)) {
						// println(o.name +
						// " has already been checked for collisions with " +
						// o2.name);
						continue;
					}
					if (o.hasCollidedWith(o2)) {
						// println("Adding " + o.name + " to list of " +
						// o2.name);
						o.collideWith(o2);
					}
					collisions.get(o2).add(o);
				}
			}
		}
	}

	private List<TransientQbject> removeTransientQbjects() {
		List<TransientQbject> nltqbjects = new ArrayList<TransientQbject>();
		for (TransientQbject o : ltqbjects) {
			if (o.isExhausted()) {
				qbjects.remove(o);
				((Qbject) o).tidyUp();
			} else {
				nltqbjects.add(o);
			}
		}
		return nltqbjects;
	}

	public void draw() {
		getParent().imageMode(CORNER);
		getParent().image(background, 0, 0);
		//getParent().background(20);
		if (DEBUG) {
			getParent().line(HALF_WIDTH, 0, HALF_WIDTH, SCREEN_HEIGHT);
			getParent().triangle(HALF_WIDTH, 0, HALF_WIDTH - 10, 20, HALF_WIDTH + 10, 20);
			getParent().triangle(HALF_WIDTH, SCREEN_HEIGHT, HALF_WIDTH - 10, SCREEN_HEIGHT - 20,
					HALF_WIDTH + 10, SCREEN_HEIGHT - 20);
			getParent().line(0, HALF_HEIGHT, SCREEN_WIDTH, HALF_HEIGHT);
			getParent().triangle(0, HALF_HEIGHT, 20, HALF_HEIGHT - 10, 20, HALF_HEIGHT + 10);
			getParent().triangle(SCREEN_WIDTH, HALF_HEIGHT, SCREEN_WIDTH - 20, HALF_HEIGHT - 10,
					SCREEN_WIDTH - 20, HALF_HEIGHT + 10);
		}
		if (DEBUG)
			player1.paintStats();
		
		for (Qbject o : qbjects) {
			o.move();
			o.paint();
		}
		ltqbjects = removeTransientQbjects();
		checkForCollisions();
		getParent().delay(round(MILLIS_DELAY_PER_DRAW));
	}

	@Override
	public void tidyUp() {
		// TODO Auto-generated method stub
		
	}
	
}
