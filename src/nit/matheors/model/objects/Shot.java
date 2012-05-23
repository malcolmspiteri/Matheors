package nit.matheors.model.objects;

import ddf.minim.AudioSample;
import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;
import processing.core.PConstants;
import processing.core.PImage;

public class Shot extends SimpleQbject implements PConstants {

	@Override
	public void move() {
		super.move();
		if ((compos.getX() - radious > SCREEN_WIDTH)
				|| (compos.getX() + radious < 0)
				|| (compos.getY() - radious > SCREEN_HEIGHT)
				|| (compos.getY() + radious < 0)) {
			dead = true;
		}
	}

	private static AudioSample fire;
	private static PImage additionp1;
	private static PImage subtractionp1;
	private static PImage additionp2;
	private static PImage subtractionp2;
	private boolean fireSoundPlayed = false;
	private float duration = FPS * 2;
	private long ticks = 0;
	private int firedBy = 1;
	private ShotType type = ShotType.ADDITION;
	
	public ShotType getType() {
		return type;
	}

	public int getFiredBy() {
		return firedBy;
	}

	boolean exhausted = false;
	private static boolean resourcesLoaded;

	public boolean isDead() {
		if (super.isDead() || (duration != 0 && (ticks++ >= duration))) {
			return true;
		} else {
			return false;
		}
	}
	
	Shot(Matheors p, Coordinates compos, Vector initVelocity, ShotType type, int firedBy) {
		super(p, SHOT_MASS, SHOT_STRENGTH, compos, initVelocity, SHOT_RADIOUS);
		this.firedBy = firedBy;
		this.type = type;
		if (!resourcesLoaded) {
			loadResources();
			resourcesLoaded = true;
		}
	}

	private void loadImages() {		
		additionp1 = getParent().loadImage("images\\shot\\additionp1.png");
		additionp2 = getParent().loadImage("images\\shot\\additionp2.png");
		subtractionp1 = getParent().loadImage("images\\shot\\subtractionp1.png");
		subtractionp2 = getParent().loadImage("images\\shot\\subtractionp2.png");
	}
	
	private void loadResources() {
		loadImages();
		fire = getParent().getMinim().loadSample("sounds\\player1_shoot.mp3");
	}

	public void paint() {
		
		getParent().pushMatrix();
		// move the origin to the pivot point
		getParent().translate(compos.getX(), compos.getY());

		getParent().imageMode(CENTER);
		getParent().smooth();
		if (firedBy == 1)
			if (type == ShotType.ADDITION)
				getParent().image(additionp1, 0, 0);
			else
				getParent().image(subtractionp1, 0, 0);
		else
			if (type == ShotType.ADDITION)
				getParent().image(additionp2, 0, 0);
			else
				getParent().image(subtractionp2, 0, 0);

		getParent().popMatrix();

		if (!fireSoundPlayed) {
			fire.trigger();
			fireSoundPlayed = true;
		}

	}

	
	@Override
	public boolean collideAndMaybeExplodeWith(float otherMassKg,
			float otherVel0, float otherVel90, Class<? extends Qbject> otherClazz) throws Exception {
		explode();
		return true;
	}

	@Override
	public boolean tidyUp() {
		//fire.close();
		return false;
	}

	@Override
	public boolean canCollideWith(Qbject qbject) {
		if (qbject instanceof Matheor || qbject instanceof Spacecraft)
			return true;
	
		return false;
	}

}
