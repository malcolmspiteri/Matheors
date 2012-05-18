package nit.matheors.model.objects;

import static processing.core.PApplet.abs;

import java.util.ArrayList;
import java.util.List;

import nit.matheors.Coordinates;
import nit.matheors.Matheors;
import static nit.matheors.Utils.calcDistance;
import nit.matheors.model.Vector;

public abstract class ComplexQbject extends Qbject {

	protected List<Coordinates> vertices = new ArrayList<Coordinates>();

	public ComplexQbject(Matheors p, float massKg, Coordinates compos,
			float width, float height, Vector initVelocity) {
		super(p, massKg, compos, width, height, initVelocity);
	}

	public boolean hasCollidedWith(SimpleQbject other) {
		for (Coordinates v : this.vertices) {
			if (calcDistance(v, other.compos) < other.radious)
				return true;
		}
		return false;
	}
	
	public boolean hasCollidedWith(ComplexQbject other) {
		if (this.name.equals("shot") && (other.name.equals("shot")))
			return false;	
		
		Coordinates cm = this.compos;
		Coordinates cc = null;
		for (Coordinates ct : this.vertices) {
			if (cc == null)
				cc = this.vertices.get(this.vertices.size() - 1);
			// Find the area of the triangle cc, ct, cm
			// We'll use the cross product for this, which is
			// a1((b2*c3)-(c2*b3))-a2((b1*c3)-(c1*b3))+a3((b1*c2)-(c1*b2))
			float art = calcArea(cc, cm, ct);
			for (Coordinates co : other.vertices) {
				if (abs(this.compos.getX() - co.getX()) < abs(this.compos.getX() - cm.getX())
						|| abs(this.compos.getX() - cc.getY()) < abs(this.compos.getX()
								- cc.getX()))
					continue;
				if (abs(this.compos.getY() - co.getY()) < abs(this.compos.getY() - cm.getY())
						|| abs(this.compos.getY() - cc.getY()) < abs(this.compos.getY()
								- cc.getY()))
					continue;
				/*
				 * println("The area is of 1 of " + other.name + " is " +
				 * (calcArea(cc, ct,co))); println("The area is of 2 of " +
				 * other.name + " is " + (calcArea(ct, cm,co)));
				 * println("The area is of 3 of " + other.name + " is " +
				 * (calcArea(cm, cc,co))); println("The area is of sum of "
				 * + other.name + " is " + (calcArea(co, cc,ct)+calcArea(co,
				 * ct,cm)+calcArea(co, cm,cc)));
				 */
				if (abs(abs(art)
						- (abs(calcArea(co, cc, ct))
								+ abs(calcArea(co, ct, cm)) + abs(calcArea(
									co, cm, cc)))) <= 0.1f) {
					// this.moveToPreviousPosition();
					// other.moveToPreviousPosition();
					/*
					 * this.compos.getX() = this.pcompos.getX(); this.compos.getY() =
					 * this.pcompos.getY(); other.compos.getX() = other.pcompos.getX();
					 * other.compos.getY() = other.pcompos.getY();
					 */
					return true;
				}
			}
			cc = ct;
		}
		return false;
	}
	
}
