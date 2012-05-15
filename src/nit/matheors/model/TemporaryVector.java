package nit.matheors.model;

public class TemporaryVector extends Vector {
	
	private float lifetime = 0;
	private long ticks = 0;
	private boolean exhausted = false;

	public boolean isExhausted() {
		if (lifetime != 0 && (ticks++ >= lifetime)) {
			exhausted = true;
		}
		return exhausted;
	}

	public TemporaryVector(float a, float n) {
		direction = a;
		magnitude = n;
	}

	public TemporaryVector(float a, float n, float l) {
		this(a, n);
		lifetime = l;
	}

}