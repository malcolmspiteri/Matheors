package nit.matheors.model.objects;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;
import processing.core.PConstants;

import static processing.core.PApplet.radians;

public class Matheor extends Qbject implements PConstants {

	public Matheor(Matheors p, float massKg, Coordinates compos, float _width, float _height,
			Vector initVelocity) {
		super(p, massKg, compos, _width, _height, initVelocity);
		name = "matheor";
	}

	public boolean explodeOnCollision() {
		return false;
	}

	public void paint() {

		getParent().pushMatrix();
		// move the origin to the pivot point
		getParent().translate(compos.getX(), compos.getY());

		// then pivot the grid
		getParent().rotate(radians(45));

		// and draw the square at the origin
		getParent().fill(255);
		getParent().ellipseMode(CENTER);
		getParent().smooth();
		getParent().ellipse(0, 0, width, height);

		/*
		 * smooth(); stroke(100); beginShape(); for (Coordinates v :
		 * vertices) { vertex(v.x, v.y); } endShape();
		 */

		getParent().textFont(getParent().getDefaultFont());
		getParent().fill(150);
		getParent().text("10", 0, 6);
		getParent().textAlign(CENTER);

		getParent().popMatrix();

		vertices.clear();
		vertices.add(new Coordinates(compos.getX(),
				(float) (compos.getY() - (height * 0.5))));
		
		vertices.add(new Coordinates(((float) (compos.getX() + (width * 0.25))), ((float) (compos.getY() - (height * 0.4)))));
		
		vertices.add(new Coordinates(((float) (compos.getX() + (width * 0.5))), compos.getY()));
		vertices.add(new Coordinates(((float) (compos.getX() + (width * 0.25))), ((float) (compos.getY() + (height * 0.4)))));
		vertices.add(new Coordinates(compos.getX(), ((float) (compos.getY() + (height * 0.5)))));
		vertices.add(new Coordinates(((float) (compos.getX() - (width * 0.25))), ((float) (compos.getY()	+ (height * 0.4)))));
		vertices.add(new Coordinates(((float) (compos.getX() - (width * 0.5))), compos.getY()));
		vertices.add(new Coordinates(((float) (compos.getX() - (width * 0.25))), ((float) (compos.getY()	- (height * 0.4)))));

	}

}
