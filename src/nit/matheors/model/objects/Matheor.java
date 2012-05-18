package nit.matheors.model.objects;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;
import processing.core.PConstants;
import processing.core.PImage;

import static processing.core.PApplet.round;

public class Matheor extends SimpleQbject implements PConstants {

	public Matheor(Matheors p, float massKg, Coordinates compos, float width, float height,
			Vector initVelocity) {
		super(p, massKg, compos, width, height, initVelocity, BIG_MATHEOR_RADIOUS);
		name = "matheor";
		randomColor = getParent().random(3);
		number = round(getParent().random(1, HIGHEST_NUMBER));
		loadImages();
	}

	private float randomColor;
	private int number;
	
	private PImage img = null;

	private void loadImages() {
		getParent().random(1f);
		img = getParent().loadImage("images\\matheors\\" + round(randomColor) + "\\1.png");
	}

	public boolean explodeOnCollision() {
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CollisionDetectionType collisionDetectionType() {
		return CollisionDetectionType.CIRCLE;
	}

}
