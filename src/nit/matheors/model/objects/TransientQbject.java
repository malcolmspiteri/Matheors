package nit.matheors.model.objects;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;

public abstract class TransientQbject extends Qbject {

	float duration = FPS * 5;
	long ticks = 0;
	boolean exhausted = false;

	public boolean isExhausted() {
		if (duration != 0 && (ticks++ >= duration)) {
			exhausted = true;
		}
		return exhausted;
	}

	protected TransientQbject(Matheors p, float massKg, Coordinates compos, float _width,
			float _height, Vector initVelocity) {
		super(p, massKg, compos, _width, _height, initVelocity);
	}

}
