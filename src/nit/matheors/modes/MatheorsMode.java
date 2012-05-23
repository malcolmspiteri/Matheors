package nit.matheors.modes;

public interface MatheorsMode {

	void setup();
	
	void draw() throws Exception;
	
	void tidyUp();
	
}
