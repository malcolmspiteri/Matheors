package nit.matheors.model;

import nit.matheors.Matheors;
import processing.core.PApplet;

public class MotionVector extends Vector {

	private PApplet parent;
	private float force = 0;
	private float velocity; // The velocity, i.e., displacement over time
	
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public float getVelocity() {
		return velocity;
	}

	private float acceleration; // The current acceleration of the vector

	public MotionVector(PApplet p, float a) {
		parent = p;
		direction = a;
	}

	public float calculateDisplacement(float massKg, float newtons) {
		force = newtons;
		acceleration = newtons / massKg;
		float displacement = (float) ((velocity * Matheors.SECONDS_PER_TICK) + (0.5 * acceleration * PApplet.pow(
				Matheors.SECONDS_PER_TICK, 2)));
		velocity += acceleration * Matheors.SECONDS_PER_TICK;
		if (displacement < 0)
			return 0;
		else
			return displacement;
	}

	public float calculateMomentum(float massKg) {
		return velocity * massKg;
	}

	public void showStats(float x, float y) {
		parent.fill(0);
		parent.textAlign(Matheors.LEFT);
		parent.text("Force: " + PApplet.round(force), x, y);
		parent.text("Acceleration: " + acceleration + " m/s²", x, y + 15);
		parent.text("Velocity: " + velocity + " m/s", x, y + 30);
	}

}
