package nit.matheors.model;

public class Vector {
	protected float direction;
	protected float magnitude;
	
	public Vector() {
		this.direction = 0;
		this.magnitude = 0;		
	}
	
	public Vector(float direction, float magnitude) {
		super();
		this.direction = direction;
		this.magnitude = magnitude;
	}
	
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
