package nit.matheors.model.objects;

import static nit.matheors.Utils.calcDistance;
import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;

public abstract class SimpleQbject extends Qbject {

	protected float radious;

	public SimpleQbject(Matheors p, float massKg, Coordinates compos,
			float width, float height, Vector initVelocity, float radious) {
		super(p, massKg, compos, width, height, initVelocity);
		this.radious = radious;
	}
	
	public boolean hasCollidedWith(ComplexQbject other) {
		for (Coordinates v : other.vertices) {
			if (calcDistance(v, other.compos) < radious)
				return true;
		}
		return false;
	}
	
	public boolean hasCollidedWith(SimpleQbject other) {
		if (calcDistance(compos, other.compos) < (radious + other.radious))
			return true;
		else
			return false;
	}
	
}
