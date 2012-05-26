package nit.matheors.controls;

import static processing.core.PApplet.pow;
import static processing.core.PApplet.sqrt;
import sun.rmi.runtime.NewThreadAction;
import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

import nit.matheors.CanTidyUp;
import nit.matheors.Coordinates;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;
import nit.matheors.MatheorsConstants;
import nit.matheors.modes.Game;

public class TUIOController extends GameComponent implements Controller, CanTidyUp {

	private TuioClient client = new TuioClient();

	public TUIOController(Matheors p, Game g) {		
		super(p);
		client.connect();
	}

	
	@Override
	public void control(final Controllable controllable, Object... params) { 
		
		final int steeringSymbolId = (Integer) params[0];
		final int thrustSymbolId = (Integer) params[1];
		final int switchToAdditionGunSymbolId = (Integer) params[2];
		final int switchToSubractionGunSymbolId = (Integer) params[3];
		
		
		client.addTuioListener(new TuioListener() {
			
			private long lastFire = 0;
			private boolean thrusterOn = false;
			private float steeringAdjust;
			private float thrustAdjust;

			@Override
			public void updateTuioObject(TuioObject tobj) {
				if (tobj.getSymbolID() == steeringSymbolId) {
					controllable.rotateBy(tobj.getAngleDegrees() + steeringAdjust);
					//controllable.rotateBy(tobj.getAngleDegrees());
				}
				if (tobj.getSymbolID() ==  thrustSymbolId) {
					if (tobj.getMotionSpeed() > 0.5f) {
						long now = System.currentTimeMillis();
						if (now > (lastFire + 200)) {
							controllable.fireOnce();
							lastFire = now;
						}
					}

					if (tobj.getAngleDegrees() == 0) {
						controllable.thrustersOff();
					} else {
						controllable.setVelocity(MatheorsConstants.SPACECRAFT_MAX_VELOCITY
								* ((360 - tobj.getAngleDegrees()) / 360));
						controllable.thrust();
					}
				}

			}
			
			@Override
			public void updateTuioCursor(TuioCursor c) {
			}
			
			@Override
			public void removeTuioObject(TuioObject tobj) {
				if (tobj.getSymbolID() ==  thrustSymbolId) {
					controllable.thrustersOff();
				}
				
			}
			
			@Override
			public void removeTuioCursor(TuioCursor c) {
	
			}
			
			@Override
			public void refresh(TuioTime t) {
			}
			
			@Override
			public void addTuioObject(TuioObject tobj) {
				System.out.println("add object "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle());
				if (tobj.getSymbolID() ==  steeringSymbolId) {
					steeringAdjust = controllable.getAngle() - tobj.getAngleDegrees();
				}
				if (tobj.getSymbolID() == switchToAdditionGunSymbolId) {
					controllable.switchToAdditionGun();
				}
				if (tobj.getSymbolID() == switchToSubractionGunSymbolId) {
					controllable.switchToSubtractionGun();
				}
			}
			
			@Override
			public void addTuioCursor(TuioCursor c) {
			}
		});
	}

	@Override
	public void tidyUp() {
		if (client.isConnected())
			client.disconnect();
		
	}

}
