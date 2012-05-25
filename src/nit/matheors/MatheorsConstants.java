package nit.matheors;

public interface MatheorsConstants {

	public final static float METRES_PER_PIXEL = 10;
	public final static boolean DEBUG = false;
	public final static float SCREEN_WIDTH = 1024;
	public final static float SCREEN_HEIGHT = 768;
	public final static float HALF_WIDTH = SCREEN_WIDTH / 2;
	public final static float HALF_HEIGHT = SCREEN_HEIGHT / 2;
	public final static float QUARTER_WIDTH = SCREEN_WIDTH / 4;
	public final static float THREEQUARTERS_WIDTH = SCREEN_WIDTH * 0.75f;
	public final static float FPS = 25;
	public final static float MILLIS_DELAY_PER_DRAW = 1000 / FPS;
	public final static float SECONDS_PER_TICK = 1 / (float) FPS;
	public final static float HIGHEST_MATHEOR_NUMBER = 10;
	public final static float LOWEST_TARGET_NUMBER = 40;
	public final static float HIGHEST_TARGET_NUMBER = 99;
	public final static float GAME_TIMER_SECONDS = 999;
	public final static float MATHEOR_SPAWN_INTERVAL = FPS * 2;
	
	public final static int PLAYER1_SPACECRAFT_TYPE = 1;
	public final static int PLAYER2_SPACECRAFT_TYPE = 2;
	public final static float SPACECRAFT_MASS = 10;
	public final static float SPACECRAFT_STRENGTH = Float.MAX_VALUE;
	public final static float SPACECRAFT_MAX_VELOCITY = 15;
	public final static float FIRING_RATE_PER_SECOND = 2;

	public final static float MATHEAOR_MASS_LOW = 50;
	public final static float MATHEAOR_MASS_HIGH = 80;
	public final static float MATHEAOR_STRENGTH = 10;
	public final static float MATHEOR_RADIOUS_BIG = 45;
	public final static float MATHEOR_RADIOUS_SMALL = 20;

	public final static float SHOT_MASS = 10;
	public final static float SHOT_STRENGTH = 0;
	public final static float SHOT_RADIOUS = 15;
	
	public final static int FIDUCIAL_0 = 0;
	public final static int FIDUCIAL_1 = 1;

}
