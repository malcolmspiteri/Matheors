package nit.matheors.model.objects;

import ddf.minim.AudioPlayer;
import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.controls.Controllable;
import nit.matheors.model.TransientVector;
import nit.matheors.model.Vector;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import static processing.core.PApplet.radians;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;

public class Spacecraft extends ComplexQbject implements Controllable, PConstants {

	public Spacecraft(Matheors p, float massKg, Coordinates compos, float _width,
			float _height, Vector initVelocity) {
		super(p, massKg, compos, _width, _height, initVelocity);
		name = "spacecraft";

		setForwardThrust(0f);

		loadImages();
		loadSounds();

		hover.mute();
		hover.loop();
	}

	private final static int FORWARD_THRUSTER  = 10000;
	
	private boolean thrustOn = false;
	private boolean reverseThrustOn = false;
	private boolean steerClockwiseOn = false;
	private boolean steerAntiClockwiseOn = false;

	protected float getForwardThrust() {
		return forces.get(FORWARD_THRUSTER).getMagnitude();
	}
	
	private void setForwardThrust(float thrust) {
		setForce(FORWARD_THRUSTER, new Vector(angle, thrust));
	}

	private void calibrateForwardThrust() {
		setForce(FORWARD_THRUSTER, new Vector(angle, getForwardThrust()));
	}

	@Override
	public void reverseThrust() {
		reverseThrustOn = true;		
	}

	@Override
	public void thrust() {
		thrustOn = true;
	}

	@Override
	public void thrustersOff() {
		thrustOn = false;
		reverseThrustOn = false;		
		zeroForce();
	}

	@Override
	public void rotateClockwise() {
		steerClockwiseOn = true;
	}

	@Override
	public void rotateAntiClockwise() {
		steerAntiClockwiseOn = true;
	}

	@Override
	public void steeringOff() {
		steerClockwiseOn = false;
		steerAntiClockwiseOn = false;
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

	AudioPlayer hover;
	
	private void loadSounds() {
		hover = getParent().getMinim().loadFile("sounds\\spaceship_hover.mp3");
	}
	
	public Qbject fire() {
		Coordinates f = new Coordinates(compos.getX() + cos(PApplet.radians(angle))
				* (height + 2 / 2), compos.getY() - sin(PApplet.radians(angle))
				* (height + 2 / 2));
		return new Shot(getParent(), 10, f, 10, 10, new TransientVector(angle, 20, FPS), 5);

	}
	
	
	private void decForce() {
		setForwardThrust(getForwardThrust() - 5); 
	}

	private void incForce() {
		setForwardThrust(getForwardThrust() + 5);
	}

	private void zeroForce() {
		setForwardThrust(0f);
	}

	public void move() {

		if (steerClockwiseOn) {
			angle -= 10;
			if (angle < 0)
				angle = 350;
			calibrateForwardThrust();
		}
		if (steerAntiClockwiseOn) {
			angle += 10;
			if (angle > 350)
				angle = 0;
			calibrateForwardThrust();
		}

		if (thrustOn) {
			incForce();
		}

		if (reverseThrustOn) {
			decForce();
		}

		super.move();

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
		if (thrust > 0)
			hover.unmute();
		else
			hover.mute();
		
		if (thrust <= 20)
			getParent().image(img0, 0, 0);		
		else if (thrust >= 21 && thrust <= 30)
			getParent().image(img1, 0, 0);
		else if (thrust >= 31 && thrust <= 40)
			getParent().image(img2, 0, 0);
		else 
			getParent().image(img3, 0, 0);
		

		getParent().popMatrix();		

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

		if (DEBUG) {
			getParent().smooth(); 
			getParent().stroke(255); 
			getParent().beginShape(LINES); 
			for (Coordinates v : vertices) { 
				getParent().vertex(v.getX(), v.getY());
				getParent().line(compos.getX(), compos.getY(), v.getX(), v.getY());
			} 
			getParent().endShape();
		}

	}

	@Override
	public CollisionDetectionType collisionDetectionType() {
		return CollisionDetectionType.POLYGONS;
	}

	@Override
	public boolean tidyUp() {
		hover.close();
		return false;
	}
}
