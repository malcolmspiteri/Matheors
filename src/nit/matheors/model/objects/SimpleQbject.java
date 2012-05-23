package nit.matheors.model.objects;

import static nit.matheors.Utils.calcDistance;
import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import nit.matheors.model.Vector;

public abstract class SimpleQbject extends Qbject {

	protected float radious;

	public SimpleQbject(Matheors p, float massKg, float strength, Coordinates compos,
			Vector initVelocity, float radious) {
		super(p, massKg, strength, compos, initVelocity);
		this.radious = radious;
	}
	
	public boolean determineIfCollisionOccurredWith(Qbject other) {
		if (other instanceof ComplexQbject) {
			ComplexQbject cother = (ComplexQbject) other;
			for (Coordinates v : cother.vertices) {
				if (calcDistance(v, compos) < radious)
					return true;
			}
			return false;
		} else {
			SimpleQbject sother = (SimpleQbject) other;
			if (calcDistance(compos, other.compos) < (radious + sother.radious))
				return true;
			else
				return false;
		}
	}
	
}
