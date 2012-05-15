package nit.matheors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nit.matheors.model.TemporaryVector;
import nit.matheors.model.objects.Coordinates;
import nit.matheors.model.objects.Qbject;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Matheors extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final static float METRES_PER_PIXEL = 10;
	public final static boolean DEBUG = true;
	public final static float WIDTH = 800;
	public final static float HEIGHT = 600;
	public final static float HALF_WIDTH = WIDTH / 2;
	public final static float HALF_HEIGHT = HEIGHT / 2;
	public final static float FPS = 25;
	public final static float MILLIS_DELAY_PER_DRAW = 1000 / FPS;
	public final static float SECONDS_PER_TICK = 1 / (float) FPS;
	
	long ticker = 0; // Will be incremented every 40 milliseconds, 25 times per
						// second

	Spacecraft o1;
	List<Qbject> qbjects = new ArrayList<Qbject>();
	List<LifetimeQbject> ltqbjects = new ArrayList<LifetimeQbject>();

	PFont f;

	public void setup() {
		if (DEBUG) {
			println("FPS: " + FPS);
			println("MILLIS_DELAY_PER_DRAW: " + MILLIS_DELAY_PER_DRAW);
			println("SECONDS_PER_TICK: " + SECONDS_PER_TICK);
		}
		size(round(WIDTH), round(HEIGHT));		
		f = loadFont("CourierNewPSMT-22.vlw");
		textFont(f, 12);
		fill(255);
		o1 = new Spacecraft(this, 10, new Coordinates(HALF_WIDTH, HALF_HEIGHT), 30,
				60, new TemporaryVector(30, 0));
		qbjects.add(o1);
		qbjects.add(new Matheor(this, 30, new Coordinates(100f, 100f), 100, 50,
				new TemporaryVector(300, 50, FPS * 3)));
		qbjects.add(new Matheor(this, 20, new Coordinates(500f, 200f), 75, 50,
				new TemporaryVector(40, 50, FPS * 3)));
		qbjects.add(new Matheor(this, 20, new Coordinates(200f, 500f), 75, 50,
				new TemporaryVector(40, 50, FPS * 3)));
		qbjects.add(new Matheor(this, 40, new Coordinates(600f, 700f), 125, 75,
				new TemporaryVector(40, 80, FPS * 3)));
	}

	void checkForCollisions() {
		Map<Qbject, List<Qbject>> collisions = new HashMap<Qbject, List<Qbject>>();
		for (Qbject o : qbjects) {
			if (!collisions.containsKey(o)) {
				collisions.put(o, new ArrayList<Qbject>());
			}
			for (Qbject o2 : qbjects) {
				if (o != o2) {
					// println("Checking " + o.name + " with " + o2.name);
					if (collisions.get(o).contains(o2)) {
						;
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
					if (!collisions.containsKey(o2)) {
						collisions.put(o2, new ArrayList<Qbject>());
					}
					collisions.get(o2).add(o);
				}
			}
		}
		// if (nc > 0)
		// println("No collisions: " + nc);
	}

	List<LifetimeQbject> killLiftimeQbjects() {
		List<LifetimeQbject> nltqbjects = new ArrayList<LifetimeQbject>();
		for (LifetimeQbject o : ltqbjects) {
			if (o.isExhausted()) {
				// ltqbjects.remove(o);
				qbjects.remove(o);
			} else {
				nltqbjects.add(o);
			}
		}
		return nltqbjects;
	}

	public void draw() {
		background(20);
		if (DEBUG) {
			line(HALF_WIDTH, 0, HALF_WIDTH, height);
			triangle(HALF_WIDTH, 0, HALF_WIDTH - 10, 20, HALF_WIDTH + 10, 20);
			triangle(HALF_WIDTH, height, HALF_WIDTH - 10, height - 20,
					HALF_WIDTH + 10, height - 20);
			line(0, HALF_HEIGHT, width, HALF_HEIGHT);
			triangle(0, HALF_HEIGHT, 20, HALF_HEIGHT - 10, 20, HALF_HEIGHT + 10);
			triangle(width, HALF_HEIGHT, width - 20, HALF_HEIGHT - 10,
					width - 20, HALF_HEIGHT + 10);
		}
		if (DEBUG)
			o1.paintStats();
		for (Qbject o : qbjects) {
			o.move();
			o.paint();
		}
		ltqbjects = killLiftimeQbjects();
		checkForCollisions();
		delay(round(MILLIS_DELAY_PER_DRAW));
	}

	public void keyPressed() {
		if (keyCode == UP) {
			o1.incForceOn = true;
		}
		if (keyCode == DOWN) {
			o1.decForceOn = true;
		}
		if (keyCode == RIGHT) {
			o1.rotateRight = true;
		}
		if (keyCode == LEFT) {
			o1.rotateLeft = true;
		}
		if (key == 'c') {
			// o1.collideWith(o2);
			// checkForCollisions();
			Shot s = (Shot) o1.fire();
			qbjects.add(s);
			ltqbjects.add(s);
		}

	}

	public void keyReleased() {
		if (keyCode == UP) {
			o1.incForceOn = false;
			o1.zeroForce();
		}
		if (keyCode == DOWN) {
			o1.decForceOn = false;
			o1.zeroForce();
		}
		if (keyCode == RIGHT) {
			o1.rotateRight = false;
		}
		if (keyCode == LEFT) {
			o1.rotateLeft = false;
		}
	}

	abstract class DriveableQbject extends Qbject {

		boolean incForceOn = false;
		boolean decForceOn = false;
		boolean rotateRight = false;
		boolean rotateLeft = false;

		DriveableQbject(PApplet p, float massKg, Coordinates compos, float _width,
				float _height, TemporaryVector initForce) {
			super(p, massKg, compos, _width, _height, initForce);
		}

		void decForce() {
			if (forces.size() > 0)
				forces.get(0).setMagnitude(forces.get(0).getMagnitude() - 5) ;
		}

		void incForce() {
			if (forces.size() > 0)
				forces.get(0).setMagnitude(forces.get(0).getMagnitude() + 5) ;
		}

		void zeroForce() {
			if (forces.size() > 0)
				forces.get(0).setMagnitude(0) ;
		}

		public void move() {

			if (rotateRight) {
				float na = forces.get(0).getDirection() - 10;
				if (na < 0)
					na = 350;
				addForce(0, new TemporaryVector(na, forces.get(0).getMagnitude()));
			}
			if (rotateLeft) {
				float na = forces.get(0).getDirection() + 10;
				if (na > 350)
					na = 0;
				addForce(0, new TemporaryVector(na, forces.get(0).getMagnitude()));
			}

			if (incForceOn) {
				incForce();
			}

			if (decForceOn) {
				decForce();
			}

			super.move();

		}

	}

	abstract class LifetimeQbject extends Qbject {

		float lifetime = FPS * 5;
		long ticks = 0;
		boolean exhausted = false;

		boolean isExhausted() {
			if (lifetime != 0 && (ticks++ >= lifetime)) {
				exhausted = true;
			}
			return exhausted;
		}

		LifetimeQbject(PApplet p, float massKg, Coordinates compos, float _width,
				float _height, TemporaryVector initForce) {
			super(p, massKg, compos, _width, _height, initForce);
		}

	}

	class Shot extends LifetimeQbject {

		Shot(PApplet p, float massKg, Coordinates compos, float _width, float _height,
				TemporaryVector initForce) {
			super(p, massKg, compos, _width, _height, initForce);
			name = "shot";
		}

		public boolean explodeOnCollision() {
			return true;
		}

		public void paint() {
			fill(255);

			ellipseMode(CENTER);
			ellipse(compos.x, compos.y, _width, _height);

			vertices.clear();
			vertices.add(new Coordinates(compos.x, compos.y));
			/*
			 * vertices.add(new Coordinates( compos.x, compos.y - (_height / 2)
			 * )); vertices.add(new Coordinates( compos.x + (width / 2),
			 * compos.y )); vertices.add(new Coordinates( compos.x, compos.y +
			 * (_height / 2) )); vertices.add(new Coordinates( compos.x - (width
			 * / 2), compos.y ));
			 */
		}

	}

	class Spacecraft extends DriveableQbject {

		Spacecraft(PApplet p, float massKg, Coordinates compos, float _width,
				float _height, TemporaryVector initForce) {
			super(p, massKg, compos, _width, _height, initForce);
			name = "spacecraft";
			loadImages();
		}

		public boolean explodeOnCollision() {
			return false;
		}

		private Map<Integer, PImage> imgs = new HashMap<Integer, PImage>();

		void loadImages() {
			for (int i = 0; i <= 350; i += 10) {
				PImage img = loadImage("images\\spacecraft\\0thrust\\" + i
						+ ".png");
				imgs.put(i, img);
			}
		}

		Qbject fire() {
			float angle = forces.size() > 0 ? forces.get(0).getDirection() : 0;
			Coordinates f = new Coordinates(compos.x + cos(radians(angle))
					* (_height + 2 / 2), compos.y - sin(radians(angle))
					* (_height + 2 / 2));
			return new Shot(parent, 1, f, 10, 10, new TemporaryVector(angle, 80, FPS));

		}

		public void paint() {
			fill(0);
			/*
			 * float angle = forces.size() > 0 ? forces.get(0).direction : 0;
			 * PShape sc = loadShape("shuttle.svg"); smooth();
			 * sc.rotate(radians(90 - angle)); sc.scale(0.2); shapeMode(CENTER);
			 * shape(sc, compos.x, compos.y, sc.width * 0.2, sc.height * 0.2);
			 */

			float angle = forces.size() > 0 ? forces.get(0).getDirection() : 0;
			Coordinates f = new Coordinates(compos.x + cos(radians(angle))
					* (_height / 2), compos.y - sin(radians(angle))
					* (_height / 2));
			Coordinates bm = new Coordinates(compos.x - cos(radians(angle))
					* (_height / 2), compos.y + sin(radians(angle))
					* (_height / 2));
			Coordinates bl = new Coordinates(bm.x - sin(radians(angle))
					* (_width / 2), bm.y - cos(radians(angle)) * (_width / 2));
			Coordinates br = new Coordinates(bm.x + sin(radians(angle))
					* (_width / 2), bm.y + cos(radians(angle)) * (_width / 2));
			vertices.clear();
			vertices.add(f);
			vertices.add(bl);
			vertices.add(br);

			/*
			 * smooth(); beginShape(TRIANGLES); vertex(f.x, f.y); vertex(bl.x,
			 * bl.y); vertex(br.x, br.y); endShape();
			 * 
			 * smooth(); stroke(100); beginShape(LINES); for (Coordinates v :
			 * vertices) { vertex(v.x, v.y); } endShape();
			 */
			imageMode(CENTER);
			image(imgs.get(floor(angle)), compos.x, compos.y);

		}

	}

	class Matheor extends Qbject {

		Matheor(PApplet p, float massKg, Coordinates compos, float _width, float _height,
				TemporaryVector initForce) {
			super(p, massKg, compos, _width, _height, initForce);
			name = "matheor";
		}

		public boolean explodeOnCollision() {
			return false;
		}

		public void paint() {

			pushMatrix();
			// move the origin to the pivot point
			translate(compos.x, compos.y);

			// then pivot the grid
			rotate(radians(45));

			// and draw the square at the origin
			fill(255);
			ellipseMode(CENTER);
			smooth();
			ellipse(0, 0, _width, _height);

			/*
			 * smooth(); stroke(100); beginShape(); for (Coordinates v :
			 * vertices) { vertex(v.x, v.y); } endShape();
			 */

			textFont(f);
			fill(150);
			text("10", 0, 6);
			textAlign(CENTER);

			popMatrix();

			vertices.clear();
			vertices.add(new Coordinates(compos.x,
					(float) (compos.y - (_height * 0.5))));
			
			vertices.add(new Coordinates(((float) (compos.x + (_width * 0.25))), ((float) (compos.y - (_height * 0.4)))));
			
			vertices.add(new Coordinates(((float) (compos.x + (_width * 0.5))), compos.y));
			vertices.add(new Coordinates(((float) (compos.x + (_width * 0.25))), ((float) (compos.y + (_height * 0.4)))));
			vertices.add(new Coordinates(compos.x, ((float) (compos.y + (_height * 0.5)))));
			vertices.add(new Coordinates(((float) (compos.x - (_width * 0.25))), ((float) (compos.y	+ (_height * 0.4)))));
			vertices.add(new Coordinates(((float) (compos.x - (_width * 0.5))), compos.y));
			vertices.add(new Coordinates(((float) (compos.x - (_width * 0.25))), ((float) (compos.y	- (_height * 0.4)))));

		}

	}

}
