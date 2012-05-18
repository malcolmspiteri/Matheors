package nit.matheors;

public abstract class GameComponent {
	private Matheors parent;

	protected Matheors getParent() {
		return parent;
	}

	protected GameComponent(Matheors parent) {
		super();
		this.parent = parent;
	}	
	
}
