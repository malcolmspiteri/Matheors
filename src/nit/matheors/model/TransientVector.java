package nit.matheors.model;

public class TransientVector extends Vector {
	
	private float duration = 0;
	private long ticks = 0;
	private boolean exhausted = false;

	public boolean isExhausted() {
		if (duration != 0 && (ticks++ >= duration)) {
			exhausted = true;
		}
		return exhausted;
	}

	public TransientVector(float a, float n) {
		direction = a;
		magnitude = n;
	}

	public TransientVector(float a, float n, float l) {
		this(a, n);
		duration = l;
	}

}