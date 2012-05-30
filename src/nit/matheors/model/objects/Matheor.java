package nit.matheors.model.objects;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;
import nit.matheors.modes.Game;
import processing.core.PConstants;
import processing.core.PImage;

import static processing.core.PApplet.cos;
import static processing.core.PApplet.radians;
import static processing.core.PApplet.round;
import static processing.core.PApplet.sin;

public class Matheor extends SimpleQbject implements PConstants {

	protected static final String MATHEOR_IMAGE_NAME_SMALL = "0.png";
	protected static final String MATHEOR_IMAGE_NAME_BIG = "1.png";
	
	private Shot killerShot;
	
	@Override
	public boolean collideAndMaybeExplodeWith(Qbject other) throws Exception {
		if (other.getClass() == Matheor.class) { // || other.getClass() == Spacecraft.class) {
			return super.collideWith(other);
		} else {
			float currMass = massKg;
			Vector tmv = getMotionVector();
			Vector omv = other.getMotionVector();
			boolean explode =  super.collideAndMaybeExplodeWith(other);
			
			if (explode && other instanceof Shot)
				killerShot = (Shot) other;
			
			// The following block of code implements the matheor split upon explosion.
			// When hit, the matheor will split into two smaller ones.
			// I'm disregarding the law of conservation of momentum here to make more arcade-ish
			
			if (explode && number > 1 && size == MatheorSize.BIG) {

				float x, y, x1, y1, a;
				int n1, n2;
				n1 = round(getParent().random(1, number - 1));
				n2 = number - n1;
				
				
				x = (MATHEOR_RADIOUS_SMALL + 1);
				y = 0;

				// rotate transform
				a = omv.getDirection() + 90;
				x1 = (x * cos(radians(a))) - (y * sin(radians(a)));
				y1 = (x * sin(radians(a))) + (y * cos(radians(a)));
				
				game.addQbject(new Matheor(getParent(), game, currMass / 2, 
						new Coordinates(compos.getX() + x1, compos.getY() - y1), 
						new Vector(a, tmv.getMagnitude()), MatheorSize.SMALL, color, n1));
				
				// rotate transform
				a = omv.getDirection() - 90;
				x1 = (x * cos(radians(a))) - (y * sin(radians(a)));
				y1 = (x * sin(radians(a))) + (y * cos(radians(a)));

				game.addQbject(new Matheor(getParent(), game, currMass / 2, 
						new Coordinates(compos.getX() + x1, compos.getY() - y1), 
						new Vector(a, tmv.getMagnitude()), MatheorSize.SMALL, color, n2));
			}
			return explode;
		}
	}	
	
	private MatheorSize size;
	
	public Matheor(Matheors p, Game g, float massKg, Coordinates compos, Vector initVelocity, MatheorSize size) {
		this(p, g, massKg, compos, initVelocity, size, -1, -1);
	}

	public Matheor(Matheors p, Game game, float massKg, Coordinates compos, Vector initVelocity, MatheorSize size, int color, int number) {
		super(p, massKg, MATHEAOR_STRENGTH, compos, initVelocity, size == MatheorSize.SMALL ? MATHEOR_RADIOUS_SMALL : MATHEOR_RADIOUS_BIG);
		this.game = game;
		this.size = size;
		if (color == -1)
			this.color = round(getParent().random(3));
		else
			this.color = color;
		
		if (number == -1)
			this.number = round(getParent().random(1, HIGHEST_MATHEOR_NUMBER));
		else
			this.number = number;

		loadImages();
	}

	protected Game game;
	protected int color;
	protected int number;
	
	public int getNumber() {
		return number;
	}

	protected PImage img = null;

	private void loadImages() {
		img = getParent().loadImage("images\\matheors\\" + color + "\\" + (size == MatheorSize.SMALL ? MATHEOR_IMAGE_NAME_SMALL : MATHEOR_IMAGE_NAME_BIG));
	}

	public void paint() {

		getParent().pushMatrix();
		// move the origin to the pivot point
		getParent().translate(compos.getX(), compos.getY());

		if (DEBUG) {
			getParent().stroke(255);		
			getParent().ellipseMode(CENTER);
			getParent().ellipse(0, 0, radious*2, radious*2);
		}
		
		getParent().imageMode(CENTER);
		getParent().smooth();
		getParent().image(img, 0, 0);

		getParent().fill(255);		
		getParent().textAlign(CENTER);
		if (size == MatheorSize.BIG) {
			getParent().textFont(getParent().getDefaultFont(), 35);
			getParent().text(number, 0, 6);
		} else {
			getParent().textFont(getParent().getDefaultFont(), 25);
			getParent().text(number, 0, 6);
		}
		

		getParent().popMatrix();

	}

	private boolean coorValidOnce = false;

	@Override
	public void move() {
		super.move();
		
		if ((compos.getX() - radious > SCREEN_WIDTH)
				|| (compos.getX() + radious < 0)
				|| (compos.getY() - radious > SCREEN_HEIGHT)
				|| (compos.getY() + radious < 0)) {
			if (coorValidOnce) {
				dead = true;
			}
		} else {
			coorValidOnce = true;
		}
		
		if (dead && killerShot != null) {
			game.updateScores(this, killerShot);
		}
		
	}

	@Override
	public boolean canCollideWith(Qbject qbject) {
		if (qbject instanceof Matheor || qbject instanceof Spacecraft || qbject instanceof Shot)
			return true;
		else
			return false;
	}

}
