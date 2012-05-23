package nit.matheors.model.objects;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;
import processing.core.PConstants;
import processing.core.PImage;

import static processing.core.PApplet.round;

public class Matheor extends SimpleQbject implements PConstants {

	protected static final String MATHEOR_IMAGE_NAME = "1.png";
	
	@Override
	public boolean collideAndMaybeExplodeWith(float otherMassKg,
			float otherVel0, float otherVel90, Class<? extends Qbject> otherClazz) throws Exception {
		// TODO Auto-generated method stub
		if (otherClazz == Matheor.class || otherClazz == Spacecraft.class) {
			return super.collideWith(otherMassKg, otherVel0, otherVel90, otherClazz);
		} else {
			return super.collideAndMaybeExplodeWith(otherMassKg, otherVel0, otherVel90, otherClazz);
		}
	}

	public Matheor(Matheors p, float massKg, Coordinates compos, Vector initVelocity) {
		super(p, massKg, MATHEAOR_STRENGTH, compos, initVelocity, MATHEOR_RADIOUS_BIG);
		color = getParent().random(3);
		number = round(getParent().random(1, HIGHEST_MATHEOR_NUMBER));
		loadImages();
	}

	private float color;
	private int number;
	
	public int getNumber() {
		return number;
	}

	private PImage img = null;

	private void loadImages() {
		getParent().random(1f);
		img = getParent().loadImage("images\\matheors\\" + round(color) + "\\" + MATHEOR_IMAGE_NAME);
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

		/*
		 * smooth(); stroke(100); beginShape(); for (Coordinates v :
		 * vertices) { vertex(v.x, v.y); } endShape();
		 */
		getParent().textFont(getParent().getDefaultFont(), 35);
		getParent().fill(255);		
		getParent().text(number, 0, 6);
		getParent().textAlign(CENTER);

		getParent().popMatrix();

	}

	@Override
	public boolean tidyUp() {
		return false;
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
		
	}

	@Override
	public boolean canCollideWith(Qbject qbject) {
		if (qbject instanceof Matheor || qbject instanceof Spacecraft || qbject instanceof Shot)
			return true;
		else
			return false;
	}

}
