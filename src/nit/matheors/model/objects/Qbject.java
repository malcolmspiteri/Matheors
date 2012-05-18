package nit.matheors.model.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nit.matheors.Coordinates;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;
import nit.matheors.MatheorsSettings;
import nit.matheors.model.TransientVector;
import nit.matheors.model.Vector;
import processing.core.PApplet;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.radians;

public abstract class Qbject extends GameComponent implements MatheorsSettings {

	protected Coordinates compos;
	protected float massKg;
	protected float width;
	protected float height;
	protected float angle;	
	protected String name;

	protected Qbject(Matheors p, float massKg, Coordinates compos, float width, float height,
			Vector initVelocity) {
		super(p);
		this.massKg = massKg;
		this.compos = compos;
		this.width = width;
		this.height = height;		
		this.angle = initVelocity.getDirection();
		
		// Distribute the initial velocity over the 4 directions
		
		float[] velocities = new float[] { 0f, 0f, 0f, 0f }; // RIGHT, UP, LEFT, DOWN
		
		float norDeg = initVelocity.getDirection() % 90;
		int quadrant = (int) initVelocity.getDirection() / 90; // 0: 0-89, 1: 90-179, 2:
												// 180-269, 3: 270-359
		float riser = sin(radians(norDeg)) * initVelocity.getMagnitude();
		float runner = cos(radians(norDeg)) * initVelocity.getMagnitude();
		velocities[quadrant % 4] = runner;
		velocities[(quadrant + 1) % 4] = riser;
		velocity0 = velocities[0];
		velocity90 = velocities[1];
		velocity180 = velocities[2];
		velocity270 = velocities[3];
		
	}

	protected float velocity0 = 0f;
	protected float velocity90 = 0f;
	protected float velocity180 = 0f;
	protected float velocity270 = 0f;

	protected List<Coordinates> vertices = new ArrayList<Coordinates>();

	public abstract boolean explodeOnCollision();

	protected float calculateAcceleration(float newtons) {
		return newtons / massKg;
	}
	
	protected float calculateDisplacement(float velocity, float acceleration, float newtons) {
		float displacement = (float) ((velocity * Matheors.SECONDS_PER_TICK) + (0.5 * acceleration * PApplet.pow(
				Matheors.SECONDS_PER_TICK, 2)));
		if (displacement < 0)
			return 0;
		else
			return displacement;
	}

	protected float calculateMomentum(float velocity) {
		return velocity * massKg;
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
		if (this.name.equals("shot") && (other.name.equals("shot")))
			return false;
		if (compos.getX() < 0 || compos.getX() > SCREEN_WIDTH || compos.getY() < 0
				|| compos.getY() > SCREEN_HEIGHT || other.compos.getX() < 0
				|| other.compos.getX() > SCREEN_WIDTH || other.compos.getY() < 0
				|| other.compos.getY() > SCREEN_HEIGHT)
			return false;
		Coordinates cm = this.compos;
		Coordinates cc = null;
		for (Coordinates ct : this.vertices) {
			if (cc == null)
				cc = this.vertices.get(this.vertices.size() - 1);
			// Find the area of the triangle cc, ct, cm
			// We'll use the cross product for this, which is
			// a1((b2*c3)-(c2*b3))-a2((b1*c3)-(c1*b3))+a3((b1*c2)-(c1*b2))
			float art = calcArea(cc, cm, ct);
			for (Coordinates co : other.vertices) {
				if (abs(this.compos.getX() - co.getX()) < abs(this.compos.getX() - cm.getX())
						|| abs(this.compos.getX() - cc.getY()) < abs(this.compos.getX()
								- cc.getX()))
					continue;
				if (abs(this.compos.getY() - co.getY()) < abs(this.compos.getY() - cm.getY())
						|| abs(this.compos.getY() - cc.getY()) < abs(this.compos.getY()
								- cc.getY()))
					continue;
				/*
				 * println("The area is of 1 of " + other.name + " is " +
				 * (calcArea(cc, ct,co))); println("The area is of 2 of " +
				 * other.name + " is " + (calcArea(ct, cm,co)));
				 * println("The area is of 3 of " + other.name + " is " +
				 * (calcArea(cm, cc,co))); println("The area is of sum of "
				 * + other.name + " is " + (calcArea(co, cc,ct)+calcArea(co,
				 * ct,cm)+calcArea(co, cm,cc)));
				 */
				if (abs(abs(art)
						- (abs(calcArea(co, cc, ct))
								+ abs(calcArea(co, ct, cm)) + abs(calcArea(
									co, cm, cc)))) <= 0.1f) {
					// this.moveToPreviousPosition();
					// other.moveToPreviousPosition();
					/*
					 * this.compos.getX() = this.pcompos.getX(); this.compos.getY() =
					 * this.pcompos.getY(); other.compos.getX() = other.pcompos.getX();
					 * other.compos.getY() = other.pcompos.getY();
					 */
					return true;
				}
			}
			cc = ct;
		}
		return false;
	}

	public void collideWith(Qbject other) {
		doCollide0(other);
		doCollide90(other);
	}

	void doCollide90(Qbject other) {
		// Find the velocity of the 2-Qbject system.
		float sysv = (calculateMomentum(velocity90) + 
				other.calculateMomentum(other.velocity90))
				/ (this.massKg + other.massKg);
		// Next we find the velocity of each Qbject in the coordinate system
		// (frame) that is moving along with the center of mass.
		float vcmt = this.velocity90 - sysv;
		float vcmo = other.velocity90 - sysv;
		// Next we reflect (reverse) each velocity in this center of mass
		// frame, and translate back to the stationary coordinate system.
		float vft = (vcmt * -1) + sysv;
		float vfo = (vcmo * -1) + sysv;
		this.velocity90 = vft;
		this.velocity270 = vft * -1;
		other.velocity90 = vfo;
		other.velocity270 = vfo * -1;
	}

	void doCollide0(Qbject other) {
		// Find the velocity of the 2-Qbject system.
		float sysv = (calculateMomentum(velocity0) + 
				other.calculateMomentum(other.velocity0))
				/ (this.massKg + other.massKg);
		// Next we find the velocity of each Qbject in the coordinate system
		// (frame) that is moving along with the center of mass.
		float vcmt = this.velocity0 - sysv;
		float vcmo = other.velocity0 - sysv;
		// Next we reflect (reverse) each velocity in this center of mass
		// frame, and translate back to the stationary coordinate system.
		float vft = (vcmt * -1) + sysv;
		float vfo = (vcmo * -1) + sysv;
		this.velocity0 = vft;
		this.velocity180 = vft * -1;
		other.velocity0 = vfo;
		other.velocity180 = vfo * -1;
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
			int quadrant = (int) f.getDirection() / 90; // 0: 0-89, 1: 90-179, 2:
													// 180-269, 3: 270-359
			// Use Pythagorean Theorem to calculate the runner and riser
			// forces
			float riserForce = sin(radians(norDeg)) * f.getMagnitude();
			float runnerForce = cos(radians(norDeg)) * f.getMagnitude();
			netForce[quadrant % 4] += runnerForce;
			netForce[(quadrant + 1) % 4] += riserForce;
		}
		/*
		 * if (DEBUG) { println("Net Force RIGHT: " + netForce[0]);
		 * println("Net Force UP: " + netForce[1]);
		 * println("Net Force LEFT: " + netForce[2]);
		 * println("Net Force DOWN: " + netForce[3]); }
		 */

		// Calculate the displacement to move for each vector
		float accel = 0;
		float disp = 0;
		float newtons = 0;
		
		// Velocity rightwards
		newtons = netForce[0] - netForce[2];
		accel = calculateAcceleration(newtons);
		disp = calculateDisplacement(velocity0, accel, newtons)
				* METRES_PER_PIXEL;
		dispXY.addX(cos(radians(0f)) * disp);
		dispXY.addY(sin(radians(0f)) * disp * -1);
		velocity0 += accel * Matheors.SECONDS_PER_TICK;

		// Velocity upwards
		newtons = netForce[1] - netForce[3];
		accel = calculateAcceleration(newtons);
		disp = calculateDisplacement(velocity90, massKg, newtons)
				* METRES_PER_PIXEL;
		dispXY.addX(cos(radians(90f)) * disp);
		dispXY.addY(sin(radians(90f)) * disp * -1);
		velocity90 += accel * Matheors.SECONDS_PER_TICK;

		// Velocity leftwards
		newtons = netForce[2] - netForce[0];
		accel = calculateAcceleration(newtons);
		disp = calculateDisplacement(velocity180, massKg, newtons)
				* METRES_PER_PIXEL;
		dispXY.addX(cos(radians(180f)) * disp);
		dispXY.addY(sin(radians(180f)) * disp * -1);
		velocity180 += accel * Matheors.SECONDS_PER_TICK;

		// Velocity downwards
		newtons = netForce[3] - netForce[1];
		accel = calculateAcceleration(newtons);
		disp = calculateDisplacement(velocity270, massKg, newtons)
				* METRES_PER_PIXEL;
		dispXY.addX(cos(radians(270f)) * disp);
		dispXY.addY(sin(radians(270f)) * disp * -1);
		velocity270 += accel * Matheors.SECONDS_PER_TICK;

		compos.addX(dispXY.getX());
		compos.addY(dispXY.getY());

		// Wrap to the opposite side of the screen
		// if the object's position exceeds it
		if (compos.getX() > SCREEN_WIDTH + width)
			compos.setX(width * -1);
		if (compos.getX() < 0 - width)
			compos.setX(SCREEN_WIDTH + width);
		if (compos.getY() > SCREEN_HEIGHT + height)
			compos.setY(height * -1);
		if (compos.getY() < 0 - height)
			compos.setY(SCREEN_HEIGHT + height);
	}

	public void paintStats() {
		/*velocity0.showStats(SCREEN_WIDTH - 170, HALF_HEIGHT + 15);
		velocity90.showStats(HALF_WIDTH + 15, 15);
		velocity180.showStats(25, HALF_HEIGHT + 15);
		velocity270.showStats(HALF_WIDTH + 20, SCREEN_HEIGHT - 40);*/
	}

	public abstract void paint();

}
