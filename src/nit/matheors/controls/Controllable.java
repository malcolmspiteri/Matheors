package nit.matheors.controls;

import nit.matheors.model.objects.Qbject;

public interface Controllable {

	void reverseThrust();
	
	void thrust();
	
	void thrustersOff();
	
	void rotateClockwise();
	
	void rotateAntiClockwise();
	
	void steeringOff();
	
	Qbject fire();

}
