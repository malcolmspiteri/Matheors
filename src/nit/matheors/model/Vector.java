package nit.matheors.model;

public abstract class Vector {
	protected float direction;
	protected float magnitude;
	
	public float getMagnitude() {
		return magnitude;
	}
	public void setMagnitude(float magnitude) {
		this.magnitude = magnitude;
	}
	
	public float getDirection() {
		return direction;
	}
	public void setDirection(float direction) {
		this.direction = direction;
	}
	
	

}
