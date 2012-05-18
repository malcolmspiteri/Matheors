package nit.matheors.controls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import nit.matheors.GameComponent;
import nit.matheors.Matheors;
import nit.matheors.modes.Game;
import nit.matheors.modes.InGameComponent;

public class KeyboardController extends InGameComponent implements Controller {

	private int thrustKey;
	private int reverseThrustKey;
	private int steerClockwiseKey;
	private int steerAntiClockwiseKey;
	private int fireKey;
	
	
	public KeyboardController(Matheors p, Game g, int thrustKey, int reverseThrustKey,
			int steerClockwiseKey, int steerAntiClockwiseKey, int fireKey) {
		super(p, g);
		this.thrustKey = thrustKey;
		this.reverseThrustKey = reverseThrustKey;
		this.steerClockwiseKey = steerClockwiseKey;
		this.steerAntiClockwiseKey = steerAntiClockwiseKey;
	}

	@Override
	public void control(final Controllable controllable) {
		getParent().addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// nothing to do here				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == thrustKey || e.getKeyCode() == reverseThrustKey) {
					controllable.thrustersOff();
				}
				if (e.getKeyCode() == steerClockwiseKey || e.getKeyCode() == steerAntiClockwiseKey) {
					controllable.steeringOff();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == thrustKey) {
					controllable.thrust();
				}
				if (e.getKeyCode() == reverseThrustKey) {
					controllable.reverseThrust();
				}
				if (e.getKeyCode() == steerClockwiseKey) {
					controllable.rotateClockwise();
				}
				if (e.getKeyCode() == steerAntiClockwiseKey) {
					controllable.rotateAntiClockwise();
				}
				if (e.getKeyChar() == 'c') {
					getGame().addQbject(controllable.fire());
				}				
			}
		});
		
	}

}
