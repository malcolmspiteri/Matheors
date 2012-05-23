package nit.matheors.model.objects;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;

public class SmallMatheor extends Matheor {

	protected static final String MATHEOR_IMAGE_NAME = "0.png";

	public SmallMatheor(Matheors p, float massKg, Coordinates compos,
			Vector initVelocity, float color, int number) {
		super(p, massKg, compos, initVelocity);
	}

}
