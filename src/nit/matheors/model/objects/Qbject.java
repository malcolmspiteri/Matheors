package nit.matheors.model.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import nit.matheors.Matheors;
import nit.matheors.model.TemporaryVector;
import nit.matheors.model.MotionVector;
import processing.core.PApplet;
import static processing.core.PApplet.abs;
import static processing.core.PApplet.sin;
import static processing.core.PApplet.cos;

public abstract class Qbject {

	protected PApplet parent;
	
	protected Stack<Coordinates> pcs = new Stack<Coordinates>();
	protected Coordinates compos;
	protected float massKg;
	protected float _width;
	protected float _height;
	protected String name;

	protected Qbject(PApplet p, float massKg, Coordinates compos, float _width, float _height,
			TemporaryVector initForce) {
		this.parent = p;
		this.massKg = massKg;
		this.compos = compos;
		this._width = _width;
		this._height = _height;
		addForce(0, initForce);
		
		rightMotionVector = new MotionVector(parent, 0);
		upMotionVector = new MotionVector(parent, 90);
		leftMotionVector = new MotionVector(parent, 180);
		downMotionVector = new MotionVector(parent, 270);
	}

	protected MotionVector rightMotionVector;
	protected MotionVector upMotionVector;
	protected MotionVector leftMotionVector;
	protected MotionVector downMotionVector;

	protected List<Coordinates> vertices = new ArrayList<Coordinates>();

	public abstract boolean explodeOnCollision();

	float getLeftmostPoint() {
		float res = Matheors.WIDTH + 1000;
		for (Coordinates v : vertices)
			if (v.x < res)
				res = v.x;
		return res;
	}

	float getRightmostPoint() {
		float res = -1000;
		for (Coordinates v : vertices)
			if (v.x > res)
				res = v.x;
		return res;
	}

	float getCentrePointX() {
		return this.getRightmostPoint()
				- ((this.getRightmostPoint() - this.getLeftmostPoint()) / 2);
	}

	float getCentrePointY() {
		return this.getDownmostPoint()
				- ((this.getDownmostPoint() - this.getUpmostPoint()) / 2);
	}

	float getUpmostPoint() {
		float res = Matheors.HEIGHT + 1000;
		for (Coordinates v : vertices)
			if (v.y < res)
				res = v.y;
		return res;
	}

	float getDownmostPoint() {
		float res = -1000;
		for (Coordinates v : vertices)
			if (v.y > res)
				res = v.y;
		return res;
	}

	protected Map<Integer, TemporaryVector> forces = new HashMap<Integer, TemporaryVector>();
	private int forceNumber = 0;

	void addForce(TemporaryVector f) {
		addForce(++forceNumber, f);
	}

	protected void addForce(int id, TemporaryVector f) {
		forces.put(id, f);
	}

	float calcArea(Coordinates a, Coordinates b, Coordinates c) {
		return calcArea(a.x, a.y, b.x, b.y, c.x, c.y);
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
		if (compos.x < 0 || compos.x > Matheors.WIDTH || compos.y < 0
				|| compos.y > Matheors.HEIGHT || other.compos.x < 0
				|| other.compos.x > Matheors.WIDTH || other.compos.y < 0
				|| other.compos.y > Matheors.HEIGHT)
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
				if (abs(this.compos.x - co.x) < abs(this.compos.x - cm.x)
						|| abs(this.compos.x - cc.x) < abs(this.compos.x
								- cc.x))
					continue;
				if (abs(this.compos.y - co.y) < abs(this.compos.y - cm.y)
						|| abs(this.compos.y - cc.y) < abs(this.compos.y
								- cc.y))
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
					 * this.compos.x = this.pcompos.x; this.compos.y =
					 * this.pcompos.y; other.compos.x = other.pcompos.x;
					 * other.compos.y = other.pcompos.y;
					 */
					return true;
				}
			}
			cc = ct;
		}
		return false;
	}

	Coordinates getPreviousPosition() {
		return pcs.pop();
	}

	void moveToPreviousPosition() {
		if (!pcs.empty()) {
			Coordinates pcompos = getPreviousPosition();
			compos.x = pcompos.x;
			compos.y = pcompos.y;
		}
	}

	public void collideWith(Qbject other) {
		doCollide0(other);
		doCollide90(other);
	}

	void doCollide90(Qbject other) {
		// Find the velocity of the 2-Qbject system.
		float sysv = (this.upMotionVector.calculateMomentum(massKg) + other.upMotionVector
				.calculateMomentum(other.massKg))
				/ (this.massKg + other.massKg);
		// println("Initial velcoity 1: " + this.upMotionVector.velocity);
		// println("Initial velcoity 2: " + other.upMotionVector.velocity);
		// println("System Velcoity: " + sysv);
		// Next we find the velocity of each Qbject in the coordinate system
		// (frame) that is moving along with the center of mass.
		float vcmt = this.upMotionVector.getVelocity() - sysv;
		float vcmo = other.upMotionVector.getVelocity() - sysv;
		// Next we reflect (reverse) each velocity in this center of mass
		// frame, and translate back to the stationary coordinate system.
		float vft = (vcmt * -1) + sysv;
		float vfo = (vcmo * -1) + sysv;
		this.upMotionVector.setVelocity(vft);
		this.downMotionVector.setVelocity(vft * -1);
		other.upMotionVector.setVelocity(vfo);
		other.downMotionVector.setVelocity(vfo * -1);

		// println("Final velcoity 1: " + vft);
		// println("Final velcoity 2: " + vfo);
		// Now we know the change in velocity for each object so we can
		// calculate the impluse
		// float impt = ((vft - this.upMotionVector.velocity) * this.massKg)
		// / SECONDS_PER_TICK;
		// println("Impulse 1: " + impt);
		// this.addForce(new Force(90, impt, 1));
		// float impo = ((vfo - other.upMotionVector.velocity) *
		// other.massKg) / SECONDS_PER_TICK;
		// println("Impulse 2: " + impo);
		// other.addForce(new Force(90, impo, 1));

	}

	void doCollide0(Qbject other) {
		// Find the velocity of the 2-Qbject system.
		float sysv = (this.rightMotionVector.calculateMomentum(massKg) + other.rightMotionVector
				.calculateMomentum(other.massKg))
				/ (this.massKg + other.massKg);
		// println("Initial velcoity 1: " +
		// this.rightMotionVector.velocity);
		// println("Initial velcoity 2: " +
		// other.rightMotionVector.velocity);
		// println("System Velcoity: " + sysv);
		// Next we find the velocity of each Qbject in the coordinate system
		// (frame) that is moving along with the center of mass.
		float vcmt = this.rightMotionVector.getVelocity() - sysv;
		float vcmo = other.rightMotionVector.getVelocity() - sysv;
		// Next we reflect (reverse) each velocity in this center of mass
		// frame, and translate back to the stationary coordinate system.
		float vft = (vcmt * -1) + sysv;
		float vfo = (vcmo * -1) + sysv;
		this.rightMotionVector.setVelocity(vft);
		this.leftMotionVector.setVelocity(vft * -1);
		other.rightMotionVector.setVelocity(vfo);
		other.leftMotionVector.setVelocity(vfo * -1);

		// println("Final velcoity 1: " + vft);
		// println("Final velcoity 2: " + vfo);
		// Now we know the change in velocity for each object so we can
		// calculate the impluse
		// float impt = ((vft - this.rightMotionVector.velocity) *
		// this.massKg) / SECONDS_PER_TICK;
		// println("Impulse 1: " + impt);
		// this.addForce(new Force(0, impt, 1));
		// float impo = ((vfo - other.rightMotionVector.velocity) *
		// other.massKg) / SECONDS_PER_TICK;
		// println("Impulse 2: " + impo);
		// other.addForce(new Force(0, impo, 1));

	}

	public void move() {
		Coordinates dispXY = new Coordinates(0, 0);

		// Calculate the net force on the object for all directions
		float[] netForce = new float[] { 0f, 0f, 0f, 0f }; // RIGHT, UP,
															// LEFT, DOWN
		for (TemporaryVector f : forces.values()) {

			if (f.isExhausted())
				continue;

			float norDeg = f.getDirection() % 90;
			int quadrant = (int) f.getDirection() / 90; // 0: 0-89, 1: 90-179, 2:
													// 180-269, 3: 270-359
			// Use Pythagorean Theorem to calculate the runner and riser
			// forces
			float riserForce = PApplet.sin(PApplet.radians(norDeg)) * f.getMagnitude();
			float runnerForce = PApplet.cos(PApplet.radians(norDeg)) * f.getMagnitude();
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

		float disp = 0;
		disp = rightMotionVector.calculateDisplacement(massKg, netForce[0]
				- netForce[2])
				* Matheors.METRES_PER_PIXEL;
		dispXY.x += (cos(PApplet.radians(rightMotionVector.getDirection())) * disp);
		dispXY.y += (sin(PApplet.radians(rightMotionVector.getDirection())) * disp * -1);

		disp = upMotionVector.calculateDisplacement(massKg, netForce[1]
				- netForce[3])
				* Matheors.METRES_PER_PIXEL;
		dispXY.x += (cos(PApplet.radians(upMotionVector.getDirection())) * disp);
		dispXY.y += (sin(PApplet.radians(upMotionVector.getDirection())) * disp * -1);

		disp = leftMotionVector.calculateDisplacement(massKg, netForce[2]
				- netForce[0])
				* Matheors.METRES_PER_PIXEL;
		dispXY.x += (cos(PApplet.radians(leftMotionVector.getDirection())) * disp);
		dispXY.y += (sin(PApplet.radians(leftMotionVector.getDirection())) * disp * -1);

		disp = downMotionVector.calculateDisplacement(massKg, netForce[3]
				- netForce[1])
				* Matheors.METRES_PER_PIXEL;
		dispXY.x += (cos(PApplet.radians(downMotionVector.getDirection())) * disp);
		dispXY.y += (sin(PApplet.radians(downMotionVector.getDirection())) * disp * -1);

		// Set the new compos of the object,
		// rounding to the nearest pixel
		if (!pcs.empty()) {
			Coordinates pcompos = pcs.peek();
			if (pcompos.x != compos.x || pcompos.y != compos.y) {
				pcs.push(new Coordinates(compos.x, compos.y));
			}
		} else {
			pcs.push(new Coordinates(compos.x, compos.y));
		}

		compos.x += dispXY.x;
		compos.y += dispXY.y;

		// Wrap to the opposite side of the screen
		// if the object's position exceeds it
		if (compos.x > Matheors.WIDTH + _width)
			compos.x = _width * -1;
		if (compos.x < 0 - _width)
			compos.x = Matheors.WIDTH + _width;
		if (compos.y > Matheors.HEIGHT + _height)
			compos.y = _height * -1;
		if (compos.y < 0 - _height)
			compos.y = Matheors.HEIGHT + _height;
	}

	public void paintStats() {
		rightMotionVector.showStats(Matheors.WIDTH - 170, Matheors.HALF_HEIGHT + 15);
		upMotionVector.showStats(Matheors.HALF_WIDTH + 15, 15);
		leftMotionVector.showStats(25, Matheors.HALF_HEIGHT + 15);
		downMotionVector.showStats(Matheors.HALF_WIDTH + 20, Matheors.HEIGHT - 40);
	}

	public abstract void paint();

}
