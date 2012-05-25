package nit.matheors.controls;

public interface Controllable {

	void reverseThrust();
	
	void thrust();
	
	void setVelocity(float velocity);
	
	void thrustersOff();
	
	void rotateClockwise();
	
	void rotateAntiClockwise();
	
	void rotateBy(float deg);
	
	void steeringOff();
	
	void startFiring();
	
	void stopFiring();
	
	void fireOnce();
	
	void switchToAdditionGun();
	
	void switchToSubtractionGun();
	
	float getAngle();

}
