package nit.matheors.model.objects;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.controls.Controllable;
import nit.matheors.model.TransientVector;
import nit.matheors.model.Vector;

public abstract class ControllableQbject extends Qbject implements Controllable {

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

	protected ControllableQbject(Matheors p, float massKg, Coordinates compos, float _width,
			float _height, Vector initVelocity) {
		super(p, massKg, compos, _width, _height, initVelocity);
		setForwardThrust(0f);
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
	
}
