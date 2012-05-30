package nit.matheors.controls;

import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

import nit.matheors.CanTidyUp;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;
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
		final int switchGunSymbolId = (Integer) params[1];		
		
		client.addTuioListener(new TuioListener() {
			
			private long lastFire = 0;
			@Override
			public void updateTuioObject(TuioObject tobj) {
				if (tobj.getSymbolID() == steeringSymbolId) {
					controllable.rotateBy(tobj.getAngleDegrees());
				}
				if (tobj.getSymbolID() == switchGunSymbolId) {
					if (tobj.getMotionSpeed() > 0.5f) {
						long now = System.currentTimeMillis();
						if (now > (lastFire + 200)) {
							controllable.fireOnce();
							lastFire = now;
						}
					}
					float a = tobj.getAngleDegrees();					
					if ((a >= 0 && a <= 90) || (a >= 271 && a <= 360)) {
						controllable.switchToAdditionGun();						
					}
					if (a >= 91 && a <= 270) {
						controllable.switchToSubtractionGun();						
					}
				}

			}
			
			@Override
			public void updateTuioCursor(TuioCursor c) {
			}
			
			@Override
			public void removeTuioObject(TuioObject tobj) {
				
			}
			
			@Override
			public void removeTuioCursor(TuioCursor tobj) {
	
			}
			
			@Override
			public void refresh(TuioTime t) {
			}
			
			@Override
			public void addTuioObject(TuioObject tobj) {
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
