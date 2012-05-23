package nit.matheors.controls;

import static processing.core.PApplet.pow;
import static processing.core.PApplet.sqrt;
import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

import nit.matheors.CanTidyUp;
import nit.matheors.Coordinates;
import nit.matheors.GameComponent;
import nit.matheors.Matheors;
import nit.matheors.modes.Game;

public class TUIOController extends GameComponent implements Controller, CanTidyUp {

	private TuioClient client = new TuioClient();
	private int steeringSymbolId = 0;
	private float steeringAdjust = 0;
	private int thrustSymbolId = 0;
	private Coordinates thrustRefCoor;

	public TUIOController(Matheors p, Game g) {
		super(p);
	}

	private boolean thrusterOn = false;
	
	@Override
	public void control(final Controllable controllable) {
		client.connect();
		client.addTuioListener(new TuioListener() {
			
			@Override
			public void updateTuioObject(TuioObject tobj) {
				System.out.println("update object "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle()
			          +" "+tobj.getMotionSpeed()+" "+tobj.getRotationSpeed()+" "+tobj.getMotionAccel()+" "+tobj.getRotationAccel()+" "+tobj.getAngleDegrees());

				if (tobj.getSymbolID() == steeringSymbolId) {
					controllable.rotateBy(tobj.getAngleDegrees() + steeringAdjust);
				}
				if (tobj.getSymbolID() ==  thrustSymbolId) {
					Coordinates newThrustCoor = new Coordinates(tobj.getX(), tobj.getY());
					float distX  = newThrustCoor.getX() - thrustRefCoor.getX();
					float distY  = newThrustCoor.getY() - thrustRefCoor.getY();
					float dist = sqrt(pow(distX,2) + pow(distY,2));					
					System.out.println("distance is "+dist);
					if (dist > 0.1) {
						if (thrusterOn) {
							controllable.thrustersOff();
							thrustRefCoor = new Coordinates(tobj.getX(), tobj.getY());
							thrusterOn = false;
						} else {
							controllable.thrust();
							thrustRefCoor = new Coordinates(tobj.getX(), tobj.getY());
							thrusterOn = true;
						}
					}
					
				}

			}
			
			@Override
			public void updateTuioCursor(TuioCursor c) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeTuioObject(TuioObject tobj) {
				if (tobj.getSymbolID() ==  thrustSymbolId) {
					controllable.thrustersOff();
				}
				
			}
			
			@Override
			public void removeTuioCursor(TuioCursor c) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void refresh(TuioTime t) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addTuioObject(TuioObject tobj) {
				System.out.println("add object "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle());
				if (tobj.getSessionID() ==  steeringSymbolId) {
					steeringAdjust = controllable.getAngle() - tobj.getAngleDegrees();
				}
				if (tobj.getSymbolID() ==  thrustSymbolId) {
					thrustRefCoor = new Coordinates(tobj.getX(), tobj.getY());
				}				
			}
			
			@Override
			public void addTuioCursor(TuioCursor c) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void tidyUp() {
		if (client.isConnected())
			client.disconnect();
		
	}

}
