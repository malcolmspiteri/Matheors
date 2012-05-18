package nit.matheors.model.objects;

import ddf.minim.AudioSample;
import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;
import processing.core.PConstants;

public class Shot extends SimpleQbject implements TransientQbject {

	private AudioSample fire;
	private boolean fireSoundPlayed = false;
	float duration = FPS * 5;
	long ticks = 0;
	boolean exhausted = false;

	public boolean isExhausted() {
		if (duration != 0 && (ticks++ >= duration)) {
			exhausted = true;
		}
		return exhausted;
	}
	
	Shot(Matheors p, float massKg, Coordinates compos, float _width, float _height,
			Vector initVelocity, float radious) {
		super(p, massKg, compos, _width, _height, initVelocity, radious);
		name = "shot";
		fire = getParent().getMinim().loadSample("sounds\\player1_shoot.mp3");
	}

	public boolean explodeOnCollision() {
		return true;
	}

	public void paint() {
		
		getParent().fill(255);
		getParent().ellipseMode(PConstants.CENTER);
		getParent().ellipse(compos.getX(), compos.getY(), width, height);

		if (!fireSoundPlayed) {
			fire.trigger();
			fireSoundPlayed = true;
		}

	}

	@Override
	public boolean tidyUp() {
		fire.close();
		return false;
	}

	@Override
	public CollisionDetectionType collisionDetectionType() {
		return CollisionDetectionType.CIRCLE;
	}
	

}
