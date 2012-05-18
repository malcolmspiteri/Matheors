package nit.matheors;

final public class Coordinates {
	private float x;
	public float getX() {
		return x;
	}

	public void addX(float a) {
		this.x += a;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void addY(float a) {
		this.y += a;
	}

	public void setY(float y) {
		this.y = y;
	}

	private float y;

	public Coordinates(float d, float e) {
		this.x = d;
		this.y = e;
	}

}
