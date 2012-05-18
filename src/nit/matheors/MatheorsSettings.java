package nit.matheors;

public interface MatheorsSettings {

	public final static float METRES_PER_PIXEL = 10;
	public final static boolean DEBUG = true;
	public final static float SCREEN_WIDTH = 1024;
	public final static float SCREEN_HEIGHT = 768;
	public final static float HALF_WIDTH = SCREEN_WIDTH / 2;
	public final static float HALF_HEIGHT = SCREEN_HEIGHT / 2;
	public final static float FPS = 25;
	public final static float MILLIS_DELAY_PER_DRAW = 1000 / FPS;
	public final static float SECONDS_PER_TICK = 1 / (float) FPS;

}
