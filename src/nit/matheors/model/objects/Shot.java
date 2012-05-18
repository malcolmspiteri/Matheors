package nit.matheors.model.objects;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;
import processing.core.PConstants;

public class Shot extends TransientQbject {

	Shot(Matheors p, float massKg, Coordinates compos, float _width, float _height,
			Vector initVelocity) {
		super(p, massKg, compos, _width, _height, initVelocity);
		name = "shot";
	}

	public boolean explodeOnCollision() {
		return true;
	}

	public void paint() {
		
		getParent().fill(255);
		getParent().ellipseMode(PConstants.CENTER);
		getParent().ellipse(compos.getX(), compos.getY(), width, height);

		vertices.clear();
		vertices.add(new Coordinates(compos.getX(), compos.getY()));
		/*
		 * vertices.add(new Coordinates( compos.getX(), compos.getY() - (_height / 2)
		 * )); vertices.add(new Coordinates( compos.getX() + (width / 2),
		 * compos.getY() )); vertices.add(new Coordinates( compos.getX(), compos.getY() +
		 * (_height / 2) )); vertices.add(new Coordinates( compos.getX() - (width
		 * / 2), compos.getY() ));
		 */
	}

}
