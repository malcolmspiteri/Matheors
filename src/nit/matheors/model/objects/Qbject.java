package nit.matheors.model.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ddf.minim.AudioSample;
import nit.matheors.Coordinates;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;
import nit.matheors.MatheorsConstants;
import nit.matheors.Utils;
import nit.matheors.model.TransientVector;
import nit.matheors.model.Vector;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.radians;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.pow;
import static processing.core.PApplet.sqrt;
import static processing.core.PApplet.degrees;
import static processing.core.PApplet.floor;

public abstract class Qbject extends GameComponent implements Cloneable, MatheorsConstants, PConstants {

	protected Coordinates pcompos;
	protected Coordinates compos;
	protected float massKg;
	protected float angle;
	public float getAngle() {
		return angle;
	}

	protected float strength;	
	
	protected boolean dead;
	
	public boolean isDead() {
		return dead;
	}
	
	public float getMassKg() {
		return massKg;
	}

	public float getVelocity0() {
		return velocity0;
	}

	public float getVelocity90() {
		return velocity90;
	}

	public float getVelocity180() {
		return velocity180;
	}

	public float getVelocity270() {
		return velocity270;
	}

	private boolean exploding;
	private boolean explosionSoundPlayed = false;
	float explosionDuration = FPS * 3;
	long explosionTicks = 0;
	private static AudioSample explosionAudioSample;
	private static List<PImage> explosionFrames;
	private static boolean resourcesLoaded;

	public Qbject createClone() {
		try {
			return (Qbject) this.clone();
		} catch (CloneNotSupportedException e) {
			// I hate Java's check exceptions
			throw new RuntimeException("Error while cloneing a Qbject instance", e);
		}
	}
	
	public Object clone() throws CloneNotSupportedException {

	    return super.clone();

	 }
	
	private void loadResources() {
		loadFrames();
		explosionAudioSample = getParent().getMinim().loadSample("sounds\\explosion.mp3");
	}
	
	private void loadFrames() {
		explosionFrames = new ArrayList<PImage>(16);
		for (int i = 0; i <= 15; i++) {
			explosionFrames.add(getParent().loadImage("images\\explosion\\" + i + ".png"));
		}
	}

	public boolean isExploding() {
		return exploding;
	}

	protected void explode() {
		massKg = 0;
		exploding = true;
	}

	public abstract boolean canCollideWith(Qbject qbject);
	
	private static final int RIGHT_Q = 0;
	private static final int UP_Q = 1;
	private static final int LEFT_Q = 2;
	private static final int DOWN_Q = 3;
	
	private float maxVelocity;
	
	protected Qbject(Matheors p, float massKg, float strength, Coordinates compos, 	Vector initVelocity) {
		this(p, massKg, strength, compos, initVelocity, Float.MAX_VALUE);
	}
	
	protected Qbject(Matheors p, float massKg, float strength, Coordinates compos, 	Vector initVelocity, float maxVelocity) {
		super(p);
		this.massKg = massKg;
		this.strength = strength;
		this.compos = compos;
		this.angle = initVelocity.getDirection();
		this.maxVelocity = maxVelocity;
		
		pcompos = compos.createClone();

		// Distribute the initial velocity over the 4 directions
		
		float[] velocities = new float[] { 0f, 0f, 0f, 0f }; // RIGHT, UP, LEFT, DOWN
		
		float norDeg = initVelocity.getDirection() % 90;
		int quadrant = (int) initVelocity.getDirection() / 90; // 0: 0-89, 1: 90-179, 2:
												// 180-269, 3: 270-359
		float riser = sin(radians(norDeg)) * initVelocity.getMagnitude();
		float runner = cos(radians(norDeg)) * initVelocity.getMagnitude();
		velocities[quadrant % 4] = runner;
		velocities[(quadrant + 1) % 4] = riser;
		velocity0 = velocities[RIGHT_Q] == 0 ? velocities[LEFT_Q] * -1 : velocities[RIGHT_Q];
		velocity90 = velocities[UP_Q] == 0 ? velocities[DOWN_Q] * -1 : velocities[UP_Q];
		velocity180 = velocities[LEFT_Q] == 0 ? velocities[RIGHT_Q] * -1 : velocities[LEFT_Q];
		velocity270 = velocities[DOWN_Q] == 0 ? velocities[UP_Q] * -1 : velocities[DOWN_Q];
		
		if (!resourcesLoaded) {
			loadResources();
			resourcesLoaded = true;
		}
		
	}

	protected float velocity0 = 0f;
	protected float velocity90 = 0f;
	protected float velocity180 = 0f;
	protected float velocity270 = 0f;

	protected float calculateAcceleration(float newtons) {
		if (massKg > 0)
			return newtons / massKg;
		else
			return 0;
	}
	
	protected float calculateDisplacement(float velocity, float acceleration, float newtons) {
		float displacement = (float) ((velocity * Matheors.SECONDS_PER_TICK) + (0.5 * acceleration * pow(
				Matheors.SECONDS_PER_TICK, 2)));
		if (displacement < 0)
			return 0;
		else
			return displacement;
	}

	protected Map<Integer, Vector> forces = new HashMap<Integer, Vector>();
	
	private int forceNumber = 0;

	void addForce(Vector f) {
		setForce(++forceNumber, f);
	}

	protected void setForce(int id, Vector f) {
		forces.put(id, f);
	}

	float calcArea(Coordinates a, Coordinates b, Coordinates c) {
		return calcArea(a.getX(), a.getY(), 
				b.getX(), b.getY(), 
				c.getX(), c.getY());
	}

	float calcArea(float a1, float a2, float b1, float b2, float c1,
			float c2) {
		return calcArea(a1, a2, 1, b1, b2, 1, c1, c2, 1);
	}

	float calcArea(float a1, float a2, float a3, float b1, float b2,
			float b3, float c1, float c2, float c3) {
		// Find the area of the triangle
		// We'll use the cross product for this, which is
		// a1((b2*c3)-(c2*b3))-a2((b1*c3)-(c1*b3))+a3((b1*c2)-(c1*b2))
		return ((a1 * ((b2 * c3) - (c2 * b3)))
				- (a2 * ((b1 * c3) - (c1 * b3))) + (a3 * ((b1 * c2) - (c1 * b2)))) / 2;
	}

	public boolean hasCollidedWith(Qbject other) {
		if (!canCollideWith(other))
			return false;
		return determineIfCollisionOccurredWith(other);
	}
	
	public abstract boolean determineIfCollisionOccurredWith(Qbject other);

	public boolean collideWith(Qbject other) throws Exception {
		collide0(other.getMassKg(), other.getVelocity0());
		collide90(other.getMassKg(), other.getVelocity90());
		moveToPreviousPosition();
		return false;
	}

	public boolean collideAndMaybeExplodeWith(Qbject other) throws Exception {
		float impulse = 0;
		impulse += collide0(other.getMassKg(), other.getVelocity0());
		impulse += collide90(other.getMassKg(), other.getVelocity90());
		if (impulse > strength) {
			explode();
			return true;
		} else {
			moveToPreviousPosition();
			return false;
		}
	}

	public Vector getMotionVector() {
		float a = angle;
		if (velocity0 > 0)
			a = 0;
		if (velocity90 > 0)
			a = 90;
		if (velocity180 > 0)
			a = 180;
		if (velocity270 > 0)
			a = 270;		
		
		if (velocity0 > 0 && velocity90 > 0) {
			a = degrees(PApplet.atan(velocity90 / velocity0));
		}
		if (velocity180 > 0 && velocity90 > 0) {
			a = 180 + degrees(PApplet.atan(velocity90 / velocity0));
		}
		if (velocity180 > 0 && velocity270 > 0) {
			a = 180 + degrees(PApplet.atan(velocity90 / velocity0));
		}
		if (velocity0 > 0 && velocity270 > 0) {
			a = 360 + degrees(PApplet.atan(velocity90 / velocity0));
		}
		
		float m = sqrt(pow(velocity0,2) + pow(velocity90,2));
		//System.out.println(m);
		return new Vector(a, m);
	}
	
	private float collide90(float otherMassKg, float otherVel90) {
		// Find the velocity of the 2-Qbject system.
		float sysv = (Utils.calculateMomentum(velocity90, massKg) + 
				Utils.calculateMomentum(otherVel90, otherMassKg))
				/ (this.massKg + otherMassKg);
		float vcmt = this.velocity90 - sysv;
		float vft = (vcmt * -1) + sysv;
		float impulse = ((vft - velocity90) * massKg) / 1f;
		this.velocity90 = vft;
		this.velocity270 = vft * -1;
		
		// Return the impulse the object received		
		return abs(impulse);
	}
	
	private float collide0(float otherMassKg, float otherVel0) {
		// Find the velocity of the 2-Qbject system.
		float sysv = (Utils.calculateMomentum(velocity0, massKg) + 
				Utils.calculateMomentum(otherVel0, otherMassKg))
				/ (this.massKg + otherMassKg);
		float vcmt = this.velocity0 - sysv;
		float vft = (vcmt * -1) + sysv;
		float impulse = ((vft - velocity0) * massKg) / 1f;
		this.velocity0 = vft;
		this.velocity180 = vft * -1;

		// Return the impulse the object received		
		return abs(impulse);

	}


	
	public void move() {
		Coordinates dispXY = new Coordinates(0, 0);
		
		// Calculate the net force on the object for all directions
		float[] netForce = new float[] { 0f, 0f, 0f, 0f }; // RIGHT, UP,
															// LEFT, DOWN
		for (Vector f : forces.values()) {

			if (f instanceof TransientVector && 
					((TransientVector)f).isExhausted())
				continue;

			float norDeg = f.getDirection() % 90;
			int quadrant = floor(f.getDirection() / 90); // 0: 0-89, 1: 90-179, 2:
													     // 180-269, 3: 270-359
			// Use Pythagorean Theorem to calculate the runner and riser
			// forces
			float riserForce = sin(radians(norDeg)) * f.getMagnitude();
			float runnerForce = cos(radians(norDeg)) * f.getMagnitude();
			netForce[quadrant % 4] += runnerForce;
			netForce[(quadrant + 1) % 4] += riserForce;
		}
		
		// Calculate the displacement, velocity and move the object
		
		float accel = 0;
		float disp = 0;
		float newtons = 0;
		
		// Velocity rightwards
		newtons = netForce[RIGHT_Q] - netForce[LEFT_Q];
		accel = calculateAcceleration(newtons);
		disp = calculateDisplacement(velocity0, accel, newtons)
				* METRES_PER_PIXEL;
		dispXY.addX(cos(radians(0f)) * disp);
		dispXY.addY(sin(radians(0f)) * disp * -1);
		velocity0 += (accel * Matheors.SECONDS_PER_TICK);

		// Velocity upwards
		newtons = netForce[UP_Q] - netForce[DOWN_Q];
		accel = calculateAcceleration(newtons);
		disp = calculateDisplacement(velocity90, massKg, newtons)
				* METRES_PER_PIXEL;
		dispXY.addX(cos(radians(90f)) * disp);
		dispXY.addY(sin(radians(90f)) * disp * -1);
		velocity90 += (accel * Matheors.SECONDS_PER_TICK);

		// Velocity leftwards
		newtons = netForce[LEFT_Q] - netForce[RIGHT_Q];
		accel = calculateAcceleration(newtons);
		disp = calculateDisplacement(velocity180, massKg, newtons)
				* METRES_PER_PIXEL;
		dispXY.addX(cos(radians(180f)) * disp);
		dispXY.addY(sin(radians(180f)) * disp * -1);
		velocity180 += (accel * Matheors.SECONDS_PER_TICK);

		// Velocity downwards
		newtons = netForce[DOWN_Q] - netForce[UP_Q];
		accel = calculateAcceleration(newtons);
		disp = calculateDisplacement(velocity270, massKg, newtons)
				* METRES_PER_PIXEL;
		dispXY.addX(cos(radians(270f)) * disp);
		dispXY.addY(sin(radians(270f)) * disp * -1);
		velocity270 += (accel * Matheors.SECONDS_PER_TICK);

		// Record previous position
		
		pcompos = compos.createClone();
		
		// And, finally, set the new coordinates
		
		compos.addX(dispXY.getX());
		compos.addY(dispXY.getY());

	}

	private void moveToPreviousPosition() {
		compos = pcompos.createClone();
	}

	public void draw() {

		if (exploding) {
			getParent().pushMatrix();
			getParent().translate(compos.getX(), compos.getY());
	
			getParent().imageMode(CENTER);
			getParent().smooth();
			getParent().image(explosionFrames.get(explosionTicks > 15 ? 15 : PApplet.floor(explosionTicks)), 0, 0);
	
			getParent().popMatrix();
			
	
			if (!explosionSoundPlayed) {
				explosionAudioSample.trigger();
				explosionSoundPlayed = true;
			}
			
			if (explosionDuration != 0 && (explosionTicks++ >= explosionDuration)) {
				dead = true;
			}
			
		} else {
			paint();
		}

	}

	public abstract void paint();

}
