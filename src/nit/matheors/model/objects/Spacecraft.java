package nit.matheors.model.objects;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.TransientVector;
import nit.matheors.model.Vector;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import static processing.core.PApplet.radians;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;

public class Spacecraft extends ControllableQbject implements PConstants {

	public Spacecraft(Matheors p, float massKg, Coordinates compos, float _width,
			float _height, Vector initVelocity) {
		super(p, massKg, compos, _width, _height, initVelocity);
		name = "spacecraft";
		loadImages();
	}

	public boolean explodeOnCollision() {
		return false;
	}

	private PImage img0 = null;
	private PImage img1 = null;
	private PImage img2 = null;
	private PImage img3 = null;

	private void loadImages() {
		img0 = getParent().loadImage("images\\spacecraft1\\0.png");
		img1 = getParent().loadImage("images\\spacecraft1\\1.png");
		img2 = getParent().loadImage("images\\spacecraft1\\2.png");
		img3 = getParent().loadImage("images\\spacecraft1\\3.png");
	}

	public Qbject fire() {
		Coordinates f = new Coordinates(compos.getX() + cos(PApplet.radians(angle))
				* (height + 2 / 2), compos.getY() - sin(PApplet.radians(angle))
				* (height + 2 / 2));
		return new Shot(getParent(), 1, f, 10, 10, new TransientVector(angle, 20, FPS));

	}

	public void paint() {

		getParent().pushMatrix();
		// move the origin to the pivot point
		getParent().translate(compos.getX(), compos.getY());

		// then pivot the grid
		getParent().rotate(radians(angle * -1));

		// and draw the spacecraft at the origin
		getParent().imageMode(CENTER);
		getParent().smooth();
		float thrust = getForwardThrust();
		if (thrust <= 20)
			getParent().image(img0, 0, 0);
		else if (thrust >= 21 && thrust <= 30)
			getParent().image(img1, 0, 0);
		else if (thrust >= 31 && thrust <= 40)
			getParent().image(img2, 0, 0);
		else 
			getParent().image(img3, 0, 0);


		/*
		 * smooth(); stroke(100); beginShape(); for (Coordinates v :
		 * vertices) { vertex(v.x, v.y); } endShape();
		 */

		getParent().popMatrix();
		
		
		/*
		 * float angle = forces.size() > 0 ? forces.get(0).direction : 0;
		 * PShape sc = loadShape("shuttle.svg"); smooth();
		 * sc.rotate(radians(90 - angle)); sc.scale(0.2); shapeMode(CENTER);
		 * shape(sc, compos.getX(), compos.getY(), sc.width * 0.2, sc.height * 0.2);
		 */
/*
		Coordinates f = new Coordinates(compos.getX() + cos(radians(angle))
				* (height / 2), compos.getY() - sin(radians(angle))
				* (height / 2));
		Coordinates bm = new Coordinates(compos.getX() - cos(radians(angle))
				* (height / 2), compos.getY() + sin(radians(angle))
				* (height / 2));
		Coordinates bl = new Coordinates(bm.getX() - sin(radians(angle))
				* (width / 2), bm.getY() - cos(radians(angle)) * (width / 2));
		Coordinates br = new Coordinates(bm.getX() + sin(radians(angle))
				* (width / 2), bm.getY() + cos(radians(angle)) * (width / 2));
*/
		float x = 0;
		float y = 0;
		float x1 = 0;
		float y1 = 0;
		
		vertices.clear();

		x = 30;
		y = 0;
		x1 = (x * cos(radians(angle))) - (y * sin(radians(angle)));
		y1 = (x * sin(radians(angle))) + (y * cos(radians(angle)));
		vertices.add(new Coordinates(compos.getX() + x1, compos.getY() - y1));
		
		x = 10;
		y = -20;
		x1 = (x * cos(radians(angle))) - (y * sin(radians(angle)));
		y1 = (x * sin(radians(angle))) + (y * cos(radians(angle)));
		vertices.add(new Coordinates(compos.getX() + x1, compos.getY() - y1));
	 
		x = -10;
		y = -20;
		x1 = (x * cos(radians(angle))) - (y * sin(radians(angle)));
		y1 = (x * sin(radians(angle))) + (y * cos(radians(angle)));
		vertices.add(new Coordinates(compos.getX() + x1, compos.getY() - y1));

		x = -25;
		y = 0;
		x1 = (x * cos(radians(angle))) - (y * sin(radians(angle)));
		y1 = (x * sin(radians(angle))) + (y * cos(radians(angle)));
		vertices.add(new Coordinates(compos.getX() + x1, compos.getY() - y1));

		x = -10;
		y = 20;
		x1 = (x * cos(radians(angle))) - (y * sin(radians(angle)));
		y1 = (x * sin(radians(angle))) + (y * cos(radians(angle)));
		vertices.add(new Coordinates(compos.getX() + x1, compos.getY() - y1));

		x = 10;
		y = 20;
		x1 = (x * cos(radians(angle))) - (y * sin(radians(angle)));
		y1 = (x * sin(radians(angle))) + (y * cos(radians(angle)));
		vertices.add(new Coordinates(compos.getX() + x1, compos.getY() - y1));

		getParent().smooth(); 
		getParent().stroke(255); 
		getParent().beginShape(LINES); 
		for (Coordinates v : vertices) { 
			getParent().vertex(v.getX(), v.getY()); 
		} 
		getParent().endShape();
		

	}
}
