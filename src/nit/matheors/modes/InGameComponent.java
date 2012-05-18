package nit.matheors.modes;

import nit.matheors.GameComponent;
import nit.matheors.Matheors;

public abstract class InGameComponent extends GameComponent {
	private Game game;

	protected Game getGame() {
		return game;
	}

	protected InGameComponent(Matheors matheors, Game parent) {
		super(matheors);
		this.game = parent;
	}	
	
}