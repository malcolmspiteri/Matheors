package nit.matheors.controls;

public interface Controllable {

	void reverseThrust();
	
	void thrust();
	
	void thrustersOff();
	
	void rotateClockwise();
	
	void rotateAntiClockwise();
	
	void rotateBy(float deg);
	
	void steeringOff();
	
	void startFiring();
	
	void stopFiring();
	
	void switchToAdditionGun();
	
	void switchToSubtractionGun();
	
	float getAngle();

}
