package nit.matheors.model.objects;

import ddf.minim.AudioPlayer;
import nit.matheors.CanTidyUp;
import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.controls.Controllable;
import nit.matheors.model.TransientVector;
import nit.matheors.model.Vector;
import nit.matheors.modes.Game;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import static processing.core.PApplet.radians;
import static processing.core.PApplet.degrees;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sqrt;
import static processing.core.PApplet.pow;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.floor;

public class Spacecraft extends ComplexQbject implements Controllable, PConstants, CanTidyUp {

	public Spacecraft(Matheors p, Game g, int type, Coordinates compos, Vector initVelocity, float maxVelocity) {
		super(p, SPACECRAFT_MASS, SPACECRAFT_STRENGTH, compos, initVelocity, maxVelocity);

		this.type = type;
		this.game = g;
		
		setForwardThrust(0f);
		setReverseThrust(0f);

		loadImages();
		loadSounds();

		hover.mute();
		hover.loop();
	}

	private int type;
	
	private final static int FORWARD_THRUSTER  = 10000;
	private final static int REVERSE_THRUSTER  = 10001;
	
	private Game game;
	private boolean thrustOn = false;
	private boolean reverseThrustOn = false;
	private boolean steerClockwiseOn = false;
	private boolean steerAntiClockwiseOn = false;
	private float maxVelocity = SPACECRAFT_MAX_VELOCITY;

	protected float getForwardThrust() {
		return forces.get(FORWARD_THRUSTER).getMagnitude();
	}
	
	protected float getReverseThrust() {
		return forces.get(REVERSE_THRUSTER).getMagnitude();
	}

	protected float getForwardThrustDirection() {
		return forces.get(FORWARD_THRUSTER).getDirection();
	}

	private void setForwardThrust(float thrust) {
		setForce(FORWARD_THRUSTER, new Vector(angle, thrust));
	}

	private void setReverseThrust(float thrust) {
		setForce(REVERSE_THRUSTER, new Vector((angle + 180) % 360, thrust));
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
		calibrateForwardThrust();
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
	
	private PImage img0 = null;
	private PImage img1 = null;
	private PImage img2 = null;
	private PImage img3 = null;

	private void loadImages() {
		img0 = getParent().loadImage("images\\spacecraft" + type + "\\0.png");
		img1 = getParent().loadImage("images\\spacecraft" + type + "\\1.png");
		img2 = getParent().loadImage("images\\spacecraft" + type + "\\2.png");
		img3 = getParent().loadImage("images\\spacecraft" + type + "\\3.png");
	}

	AudioPlayer hover;
	
	private ShotType gun = ShotType.ADDITION;
	
	public ShotType getGun() {
		return gun;
	}

	private void loadSounds() {
		hover = getParent().getMinim().loadFile("sounds\\spaceship_hover.mp3");
	}
	
	public Qbject fire() {
		float x = 80;
		float y = 0;
		float x1 = (x * cos(radians(angle))) - (y * sin(radians(angle)));
		float y1 = (x * sin(radians(angle))) + (y * cos(radians(angle)));
		Coordinates c = new Coordinates(compos.getX() + x1, compos.getY() - y1);
		
		Shot s = new Shot(getParent(), c, new Vector(angle, 20), gun, type);
		
		// To make the firing more realistic, we'll add impluses derived from the positive velocities 
		// of the spacecraft
		
		if (velocity0 > 0) s.addForce(new TransientVector(0, (velocity0 * SHOT_MASS) / SECONDS_PER_TICK, 1));
		if (velocity90 > 0) s.addForce(new TransientVector(90, (velocity90 * SHOT_MASS) / SECONDS_PER_TICK, 1));
		if (velocity180 > 0) s.addForce(new TransientVector(180, (velocity180 * SHOT_MASS) / SECONDS_PER_TICK, 1));
		if (velocity270 > 0) s.addForce(new TransientVector(270, (velocity270 * SHOT_MASS) / SECONDS_PER_TICK, 1));
		
		return s;

	}
	
	private void decForce() {
		setReverseThrust(getReverseThrust() + 50); 
	}

	private void incForce() {
		setForwardThrust(getForwardThrust() + 50);
	}

	private void zeroForce() {
		setForwardThrust(0f);
		setReverseThrust(0f);
	}

	private boolean maxVelocityReached(float angle) {
		float na = angle % 90;
		float riser = sin(radians(na)) * SPACECRAFT_MAX_VELOCITY;
		float runner = cos(radians(na)) * SPACECRAFT_MAX_VELOCITY;
		int qa = floor(angle / 90);
		switch (qa) {
			case 0 :
				if (velocity0 >= runner && velocity90 >= riser)
					return true;
				break;
			case 1 :
				if (velocity90 >= runner && velocity180 >= riser)
					return true;
				break;
			case 2 :
				if (velocity180 >= runner && velocity270 >= riser)
					return true;
				break;
			case 3 :
				if (velocity270 >= runner && velocity0 >= riser)
					return true;
				break;
			
		}
		return false;
	}

	public void move() {

		float abs = angle;
		
		if (steerClockwiseOn) {
			angle -= 10;
			if (angle < 0)
				angle = 350;
			if (thrustOn)
				calibrateForwardThrust();
		}
		if (steerAntiClockwiseOn) {
			angle += 10;
			if (angle > 350)
				angle = 0;
			if (thrustOn)
				calibrateForwardThrust();
		}
		
		if (!maxVelocityReached(abs)) {
			if (thrustOn) {
				incForce();
			}
	
			if (reverseThrustOn) {
				decForce();
			}
		} else {
			thrustersOff();
		}
		
		if ((firingOn && 
				firingTicker++ % PApplet.round(FPS / FIRING_RATE_PER_SECOND) == 0) 
			|| fireOnce) {
			game.addQbject(fire());
			fireOnce = false;
		}
		
		super.move();

		// Limit the velocity
		/*
		float v = getMotionVector().getMagnitude();
		float d = v - maxVelocity;
		
		if (d > 0) {
			velocity0 -= (d * (velocity0 / v));
			velocity90 -= (d * (velocity90 / v));
			velocity180 -= (d * (velocity180 / v));
			velocity270 -= (d * (velocity270 / v));
		}
		*/

		// Wrap to the opposite side of the screen
		// if the object's position exceeds it
		if (compos.getX() > SCREEN_WIDTH)
			compos.setX(0);
		
		if (compos.getX() < 0)
			compos.setX(SCREEN_WIDTH);
		
		if (compos.getY() > SCREEN_HEIGHT)
			compos.setY(0);
		
		if (compos.getY() < 0)
			compos.setY(SCREEN_HEIGHT);
		
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
		
		if (thrustOn) {
			float v = getMotionVector().getMagnitude();
			float v25p = SPACECRAFT_MAX_VELOCITY * 0.25f;
			float v50p = SPACECRAFT_MAX_VELOCITY * 0.5f;
			float v75p = SPACECRAFT_MAX_VELOCITY * 0.75f;
			if (v <= v25p)
				getParent().image(img0, 0, 0);		
			else if (v > v25p && v <= v50p)
				getParent().image(img1, 0, 0);
			else if (v > v50p && v <= v75p)
				getParent().image(img2, 0, 0);
			else 
				getParent().image(img3, 0, 0);
		} else {
			getParent().image(img0, 0, 0);
		}

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

	public void tidyUp() {
		hover.close();
	}

	@Override
	public boolean canCollideWith(Qbject qbject) {
		if (qbject instanceof Matheor || qbject instanceof Spacecraft || qbject instanceof Shot)
			return true;
		else
			return false;
	}

	@Override
	public void switchToAdditionGun() {
		this.gun = ShotType.ADDITION;
		
	}

	@Override
	public void switchToSubtractionGun() {
		this.gun = ShotType.SUBTRACTION;
		
	}

	@Override
	public void rotateBy(float deg) {
		angle = deg;
		if (angle < 0)
			angle = 360 + angle;
		if (angle > 359)
			angle = 0 + (angle - 360);
		if (thrustOn)
			calibrateForwardThrust();
	}

	private boolean firingOn = false;
	private long firingTicker;
	private boolean fireOnce = false;
	

	@Override
	public void startFiring() {
		if (!firingOn) {
			firingOn = true;
			fireOnce = true;
			firingTicker = 0;
		}
	}

	@Override
	public void stopFiring() {
		firingOn = false;		
	}

	@Override
	public void setVelocity(float velocity) {
		this.maxVelocity = velocity;
		
	}

	@Override
	public void fireOnce() {
		fireOnce = true;
		
	}

}
