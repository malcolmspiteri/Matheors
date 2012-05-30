package nit.matheors.controls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import nit.matheors.CanTidyUp;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;

public class KeyboardController extends GameComponent implements Controller, CanTidyUp {

	private int thrustKey;
	private int reverseThrustKey;
	private int steerClockwiseKey;
	private int steerAntiClockwiseKey;
	private int fireKey;
	private int switchToAdditionGunKey;
	private int switchToSubtractionGunKey;
	
	
	public KeyboardController(Matheors p, int thrustKey, int reverseThrustKey,
			int steerClockwiseKey, int steerAntiClockwiseKey, int fireKey, int switchToAdditionGunKey, int switchToSubtractionGunKey) {
		super(p);
		this.thrustKey = thrustKey;
		this.reverseThrustKey = reverseThrustKey;
		this.steerClockwiseKey = steerClockwiseKey;
		this.steerAntiClockwiseKey = steerAntiClockwiseKey;
		this.fireKey = fireKey;
		this.switchToAdditionGunKey = switchToAdditionGunKey;
		this.switchToSubtractionGunKey = switchToSubtractionGunKey;
	}


	private KeyListener listener;
	@Override
	public void control(final Controllable controllable, Object... params) {
		listener = new KeyListener() {
			
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
				if (e.getKeyCode() == fireKey) {
					controllable.stopFiring();
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
				if (e.getKeyCode() == fireKey) {
					controllable.startFiring();
				}				
				if (e.getKeyCode() == switchToAdditionGunKey) {
					controllable.switchToAdditionGun();
				}
				if (e.getKeyCode() == switchToSubtractionGunKey) {
					controllable.switchToSubtractionGun();
				}
			}
		};
		getParent().addKeyListener(listener);
		
	}


	@Override
	public void tidyUp() {
		getParent().removeKeyListener(listener);		
	}


}
