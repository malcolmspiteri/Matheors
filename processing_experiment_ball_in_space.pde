import java.util.Collections;

final float METRES_PER_PIXEL = 10;
final boolean DEBUG = true;
final int WIDTH = 800;
final int HEIGHT = 600;
final int HALF_WIDTH = WIDTH / 2;
final int HALF_HEIGHT = HEIGHT / 2;
final int FPS = 25;
final int MILLIS_DELAY_PER_DRAW = 1000 / FPS;
final float SECONDS_PER_TICK = 1 / (float)FPS;
long ticker = 0; // Will be incremented every 40 milliseconds, 25 times per second


DriveableQbject o1;
List<Qbject> qbjects = new ArrayList();

PFont f;

void setup() {
  if (DEBUG) {
    println("FPS: " + FPS);
    println("MILLIS_DELAY_PER_DRAW: " + MILLIS_DELAY_PER_DRAW);
    println("SECONDS_PER_TICK: " + SECONDS_PER_TICK);
  }
  size(WIDTH,HEIGHT);
  f = loadFont("CourierNewPSMT-22.vlw");
  textFont(f,12);
  fill(0);
  o1 = new Spacecraft(10, new Coordinates(HALF_WIDTH, HALF_HEIGHT), 30, 60, new Force(30, 0));  
  qbjects.add(o1);
  qbjects.add(new Matheor(30, new Coordinates(100, 100), 100, 50, new Force(300, 50, FPS * 3)));
  qbjects.add(new Matheor(20, new Coordinates(500, 200), 75, 50, new Force(40, 50, FPS * 3)));
  qbjects.add(new Matheor(20, new Coordinates(200, 500), 75, 50, new Force(40, 50, FPS * 3)));
  qbjects.add(new Matheor(40, new Coordinates(600, 700), 125, 75, new Force(40, 80, FPS * 3)));
}

void checkForCollisions() {
  int nc = 0;
  Map<Qbject, List<Qbject>> collisions = new HashMap();
  for (Qbject o : qbjects) {
    if (!collisions.containsKey(o)) {
      collisions.put(o, new ArrayList());
    }
    for (Qbject o2 : qbjects) {
      if (o != o2) {
        //println("Checking " + o.name + " with " + o2.name);          
        if (collisions.get(o).contains(o2)) {;
            //println(o.name + " has already been checked for collisions with " + o2.name);          
            continue;
        }
        if (o.hasCollidedWith(o2)) {
          //println("Adding " + o.name + " to list of " + o2.name);          
          o.collideWith(o2);
          //println(o.name + " has collided with " + o2.name);          
          nc++;
        }
        if (!collisions.containsKey(o2)) {
          collisions.put(o2, new ArrayList());
        }
        collisions.get(o2).add(o);
      }
    }    
  }
  if (nc > 0)
  println("No collisions: " + nc);
}

void draw() {
  background(255);
  if (DEBUG) {
    line(HALF_WIDTH, 0, HALF_WIDTH, height);
    triangle(HALF_WIDTH, 0, HALF_WIDTH-10, 20, HALF_WIDTH+10, 20);
    triangle(HALF_WIDTH, height, HALF_WIDTH-10, height-20, HALF_WIDTH+10, height-20);
    line(0, HALF_HEIGHT, width, HALF_HEIGHT);
    triangle(0, HALF_HEIGHT, 20, HALF_HEIGHT-10, 20, HALF_HEIGHT+10);
    triangle(width, HALF_HEIGHT, width-20, HALF_HEIGHT-10, width-20, HALF_HEIGHT+10);
  }
  if (DEBUG)
    o1.paintStats();
  checkForCollisions();
  for (Qbject o : qbjects) {
    o.move();
    o.paint();
  }
  delay(MILLIS_DELAY_PER_DRAW);  
}

void keyPressed() {
  if (keyCode == UP) {
    o1.incForceOn = true;
  }
  if (keyCode == DOWN) {
    o1.decForceOn = true;
  }
  if (keyCode == RIGHT) {
    o1.rotateRight = true;
  }
  if (keyCode == LEFT) {
    o1.rotateLeft = true;
  }
  if (key == 'c') {
    //o1.collideWith(o2);
    checkForCollisions();
  }
}

void keyReleased() {
  if (keyCode == UP) {
    o1.incForceOn = false;
    o1.zeroForce();
  }
  if (keyCode == DOWN) {
    o1.decForceOn = false;
    o1.zeroForce();
  }
  if (keyCode == RIGHT) {
    o1.rotateRight = false;
  }
  if (keyCode == LEFT) {
    o1.rotateLeft = false;
  }
}

abstract class Vector {
  float direction;
  float magnitude;
}

class MotionVector extends Vector {
  private float force = 0;
  private float velocity; // The velocity, i.e., displacement over time
  private float acceleration; // The current acceleration of the vector

  MotionVector(float a) {
    direction = a;
  }
      
  float calculateDisplacement(float massKg, float newtons) {    
    force = newtons;
    acceleration = newtons / massKg;
    float displacement = (velocity * SECONDS_PER_TICK) + (0.5 * acceleration * pow(SECONDS_PER_TICK,2));
    velocity += acceleration * SECONDS_PER_TICK;
    if (displacement < 0)
      return 0;
    else
      return displacement;
  }
  
  float calculateMomentum(float massKg) {   
      return velocity * massKg;
  }
  
  void showStats(int x, int y) {
    fill(0);
    textAlign(LEFT);
    text("Force: " + round(force),x,y);
    text("Acceleration: " + acceleration + " m/s²", x, y+15);
    text("Velocity: " + velocity + " m/s", x, y+30);
  }
  
}

class Force extends Vector {
  float lifetime = 0;
  long ticks = 0;
  boolean exhausted = false;
  
  Force(float a, float n) {
    direction = a;
    magnitude = n;
  } 

  Force(float a, float n, long l) {
    this(a, n);
    lifetime = l;    
  } 
  
  boolean isExhausted() {
    if (lifetime != 0 && (ticks++ >= lifetime)) {
      exhausted = true;
    }
    return exhausted;
  }

}

class Coordinates {
  float x;
  float y;
  
  Coordinates(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
}

abstract class DriveableQbject extends Qbject {

  boolean incForceOn = false;
  boolean decForceOn = false;
  boolean rotateRight = false;
  boolean rotateLeft = false;

  DriveableQbject(float massKg, Coordinates compos, int _width, int _height, Force initForce) {
    super(massKg, compos, _width, _height, initForce);
  }
  
  void decForce() {
    if (forces.size() > 0)
    forces.get(0).magnitude-=5;
  }    

  void incForce() {
    if (forces.size() > 0)
    forces.get(0).magnitude += 5;
  }    

  void zeroForce() {
    if (forces.size() > 0)
    forces.get(0).magnitude = 0;
  }    

  void move() {
   
    if (rotateRight) {
      float na = forces.get(0).direction - 10;
      if (na < 0) 
        na = 360;
      addForce(0, new Force(na, forces.get(0).magnitude));
    }
    if (rotateLeft) {
      float na = forces.get(0).direction + 10;
      if (na > 360) 
        na = 0;
      addForce(0, new Force(na, forces.get(0).magnitude));
    }    
      
    if (incForceOn) {
      incForce();
    }
    
    if (decForceOn) {
      decForce();
    }
    
    super.move();

  }

}

abstract class Qbject {

  Coordinates pcompos = new Coordinates(0, 0);
  Coordinates compos;
  float massKg;
  float _width;
  float _height;
  String name;
  
  Qbject(float massKg, Coordinates compos, int _width, int _height, Force initForce) {
    this.massKg = massKg;
    this.compos = compos;
    this._width = _width;
    this._height = _height;
    addForce(0, initForce);
  }
  
  MotionVector rightMotionVector = new MotionVector(0);
  MotionVector upMotionVector = new MotionVector(90);
  MotionVector leftMotionVector = new MotionVector(180);
  MotionVector downMotionVector = new MotionVector(270);
  
  List<Coordinates> vertices = new ArrayList();
  
  float getLeftmostPoint() {
    float res = WIDTH + 1000;
    for (Coordinates v : vertices)
      if (v.x < res)
        res = v.x;
    return res;
  }

  float getRightmostPoint() {
    float res = -1000;
    for (Coordinates v : vertices)
      if (v.x > res)
        res = v.x;
    return res;
  }
  
  float getCentrePointX() {
    return this.getRightmostPoint() - ((this.getRightmostPoint() - this.getLeftmostPoint()) / 2);
  }

  float getCentrePointY() {
    return this.getDownmostPoint() - ((this.getDownmostPoint() - this.getUpmostPoint()) / 2);
  }

  float getUpmostPoint() {
    float res = HEIGHT + 1000;
    for (Coordinates v : vertices)
      if (v.y < res)
        res = v.y;
    return res;
  }

  float getDownmostPoint() {
    float res = -1000;
    for (Coordinates v : vertices)
      if (v.y > res)
        res = v.y;
    return res;
  }

  protected Map<Integer, Force> forces = new HashMap();
  private int forceNumber = 0;
  
  void addForce(Force f) {
    addForce(++forceNumber, f);
  }
  
  void addForce(int id, Force f) {
    forces.put(id, f);
  }

  boolean hasCollidedWith(Qbject other) {
    float distX = 0;
    float distY = 0;
    distX = abs(this.getCentrePointX() - other.getCentrePointX());
    distY = abs(this.getCentrePointY() - other.getCentrePointY());
    if (distX < ((this.getRightmostPoint() - this.getLeftmostPoint()) + (other.getRightmostPoint() - other.getLeftmostPoint())) / 2 &&
        distY < ((this.getDownmostPoint() - this.getUpmostPoint()) + (other.getDownmostPoint() - other.getUpmostPoint())) / 2) {
      // Separeate objects from each other
      this.moveToPreviousPosition();
      other.moveToPreviousPosition();
    println("distX: " + distX);
    println("distY: " + distY);
    println("width this: " + (this.getRightmostPoint() - this.getLeftmostPoint()));
    println("width that: " + (other.getRightmostPoint() - other.getLeftmostPoint()));
      println("height this: " + (this.getDownmostPoint() - this.getUpmostPoint()));
      println("height that: " + (other.getDownmostPoint() - other.getUpmostPoint()));
      println("moving " + this.name + " to prev pos: " + this.pcompos.x + " - " + this.pcompos.y);
      println("moving " + other.name + " to prev pos: " + other.pcompos.x + " - " + other.pcompos.y);
      println(this.name + " pos: " + this.compos.x + " - " + this.compos.y);
      println(other.name + " pos: " + other.compos.x + " - " + other.compos.y);
      return true;
    } else {
      return false;    
    }
  }
  
  void moveToPreviousPosition() {
    compos.x = pcompos.x;
    compos.y = pcompos.y;
  }

  void collideWith(Qbject other) {
    doCollide0(other);
    doCollide90(other);
  }

  void doCollide90(Qbject other) {
    // Find the velocity of the 2-Qbject system. 
    float sysv = (this.upMotionVector.calculateMomentum(massKg) + other.upMotionVector.calculateMomentum(other.massKg)) / (this.massKg + other.massKg);
    //println("Initial velcoity 1: " + this.upMotionVector.velocity);
    //println("Initial velcoity 2: " + other.upMotionVector.velocity);
    //println("System Velcoity: " + sysv);
    // Next we find the velocity of each Qbject in the coordinate system (frame) that is moving along with the center of mass.
    float vcmt = this.upMotionVector.velocity - sysv;
    float vcmo = other.upMotionVector.velocity - sysv;
    // Next we reflect (reverse) each velocity in this center of mass frame, and translate back to the stationary coordinate system. 
    float vft = (vcmt * -1) + sysv;
    float vfo = (vcmo * -1) + sysv;
    //println("Final velcoity 1: " + vft);
    //println("Final velcoity 2: " + vfo);
    // Now we know the change in velocity for each object so we can calculate the impluse
    float impt = ((vft - this.upMotionVector.velocity) * this.massKg) / SECONDS_PER_TICK;
    //println("Impulse 1: " + impt);
    this.addForce(new Force(90, impt, 1));    
    float impo = ((vfo - other.upMotionVector.velocity) * other.massKg) / SECONDS_PER_TICK;
    //println("Impulse 2: " + impo);
    other.addForce(new Force(90, impo, 1));

  }
  
  void doCollide0(Qbject other) {
    // Find the velocity of the 2-Qbject system. 
    float sysv = (this.rightMotionVector.calculateMomentum(massKg) + other.rightMotionVector.calculateMomentum(other.massKg)) / (this.massKg + other.massKg);
    println("Initial velcoity 1: " + this.rightMotionVector.velocity);
    println("Initial velcoity 2: " + other.rightMotionVector.velocity);
    println("System Velcoity: " + sysv);
    // Next we find the velocity of each Qbject in the coordinate system (frame) that is moving along with the center of mass.
    float vcmt = this.rightMotionVector.velocity - sysv;
    float vcmo = other.rightMotionVector.velocity - sysv;
    // Next we reflect (reverse) each velocity in this center of mass frame, and translate back to the stationary coordinate system. 
    float vft = (vcmt * -1) + sysv;
    float vfo = (vcmo * -1) + sysv;
    println("Final velcoity 1: " + vft);
    println("Final velcoity 2: " + vfo);
    // Now we know the change in velocity for each object so we can calculate the impluse
    float impt = ((vft - this.rightMotionVector.velocity) * this.massKg) / SECONDS_PER_TICK;
    println("Impulse 1: " + impt);
    this.addForce(new Force(0, impt, 1));    
    float impo = ((vfo - other.rightMotionVector.velocity) * other.massKg) / SECONDS_PER_TICK;
    println("Impulse 2: " + impo);
    other.addForce(new Force(0, impo, 1));
    
  }

  void move() {
    Coordinates dispXY = new Coordinates(0, 0);

    // Calculate the net force on the object for all directions
    float[] netForce = new float[] {0f,0f,0f,0f}; // RIGHT, UP, LEFT, DOWN
    for (Force f : forces.values()) { 

      if (f.isExhausted())
        continue;
        
      float norDeg = f.direction % 90;
      int quadrant = (int) f.direction / 90; // 0: 0-89, 1: 90-179, 2: 180-269, 3: 270-359
      // Use Pythagorean Theorem to calculate the runner and riser forces
      float riserForce = sin(radians(norDeg)) * f.magnitude;
      float runnerForce = cos(radians(norDeg)) * f.magnitude;
      netForce[quadrant % 4] += runnerForce;
      netForce[(quadrant+1) % 4] += riserForce;
    }     
   /* 
    if (DEBUG) {
      println("Net Force RIGHT: " + netForce[0]);
      println("Net Force UP: " + netForce[1]);
      println("Net Force LEFT: " + netForce[2]);
      println("Net Force DOWN: " + netForce[3]);
    }
    */
    
    // Calculate the displacement to move for each vector
    
    float disp = 0;
    disp = rightMotionVector.calculateDisplacement(massKg, netForce[0]-netForce[2]) * METRES_PER_PIXEL;    
    dispXY.x += (cos(radians(rightMotionVector.direction)) * disp);
    dispXY.y += (sin(radians(rightMotionVector.direction)) * disp * -1);        

    disp = upMotionVector.calculateDisplacement(massKg, netForce[1]-netForce[3]) * METRES_PER_PIXEL;    
    dispXY.x += (cos(radians(upMotionVector.direction)) * disp);
    dispXY.y += (sin(radians(upMotionVector.direction)) * disp * -1);        

    disp = leftMotionVector.calculateDisplacement(massKg, netForce[2]-netForce[0]) * METRES_PER_PIXEL;    
    dispXY.x += (cos(radians(leftMotionVector.direction)) * disp);
    dispXY.y += (sin(radians(leftMotionVector.direction)) * disp * -1);        

    disp = downMotionVector.calculateDisplacement(massKg, netForce[3]-netForce[1]) * METRES_PER_PIXEL;    
    dispXY.x += (cos(radians(downMotionVector.direction)) * disp);
    dispXY.y += (sin(radians(downMotionVector.direction)) * disp * -1);        
    
    // Set the new compos of the object,
    // rounding to the nearest pixel
    if (pcompos.x != compos.x || pcompos.y != compos.y) {
      pcompos.x = compos.x;
      pcompos.y = compos.y;
    }
    compos.x += round(dispXY.x);
    compos.y += round(dispXY.y);
    
    // Wrap to the opposite side of the screen 
    // if the object's position exceeds it 
    if (compos.x > width + _width)
      compos.x = _width * -1;
    if (compos.x < 0 - _width)
      compos.x = width + _width;
    if (compos.y > height + _height)
      compos.y = _height * -1;
    if (compos.y < 0 - _height)
      compos.y = height + _height;
  }
  
    void paintStats() {
      int y = 15;
      rightMotionVector.showStats(width-170, HALF_HEIGHT+15);
      y += 65;
      upMotionVector.showStats(HALF_WIDTH+15, 15);
      y += 65;
      leftMotionVector.showStats(25, HALF_HEIGHT+15);
      y += 65;
      downMotionVector.showStats(HALF_WIDTH+20, height-40);
    }
    
    abstract void paint();
  
}

class Spacecraft extends DriveableQbject {

  Spacecraft(float massKg, Coordinates compos, int _width, int _height, Force initForce) {
    super(massKg, compos, _width, _height, initForce);
    name = "spacecraft";
  }

  void paint() {
    fill(0);
    /*
    float angle = forces.size() > 0 ? forces.get(0).direction : 0;
    PShape sc = loadShape("shuttle.svg");
    smooth();
    sc.rotate(radians(90 - angle));
    sc.scale(0.2);
    shapeMode(CENTER);
    shape(sc, compos.x, compos.y, sc.width * 0.2, sc.height * 0.2);
    */
    
    float angle = forces.size() > 0 ? forces.get(0).direction : 0;
    Coordinates f = new Coordinates(
      round(compos.x + cos(radians(angle)) * (_height / 2)),
      round(compos.y - sin(radians(angle)) * (_height / 2))
    );
    Coordinates bm = new Coordinates(
      round(compos.x - cos(radians(angle)) * (_height / 2)),
      round(compos.y + sin(radians(angle)) * (_height / 2))
    );
    Coordinates bl = new Coordinates(
      round(bm.x - sin(radians(angle)) * (_width / 2)),
      round(bm.y - cos(radians(angle)) * (_width / 2))
    );
    Coordinates br = new Coordinates(
      round(bm.x + sin(radians(angle)) * (_width / 2)),
      round(bm.y + cos(radians(angle)) * (_width / 2))
    );
    vertices.clear();
    vertices.add(f);
    vertices.add(bm);
    vertices.add(bl);
    vertices.add(br);    
    
    smooth();
    beginShape(TRIANGLES);
    vertex(f.x, f.y);
    vertex(bl.x, bl.y);
    vertex(br.x, br.y);
    endShape();
    
    smooth();
    stroke(100);
    beginShape(LINES);
    for (Coordinates v : vertices) {
      vertex(v.x, v.y);
    }
    endShape();
  }

}

class Matheor extends Qbject {
  
  Matheor(float massKg, Coordinates compos, int _width, int _height, Force initForce) {
    super(massKg, compos, _width, _height, initForce);
    name = "matheor";
  }

  void paint() {
    fill(0);

    ellipseMode(CENTER);
    smooth();
    ellipse(compos.x, compos.y, _width, _height);

    vertices.clear();
    vertices.add(new Coordinates(round(compos.x - (_width / 2)), 
      round(compos.y - (_height / 2))));
    vertices.add(new Coordinates(round(compos.x + (_width / 2)), 
      round(compos.y + (_height / 2))));


    smooth();
    stroke(100);
    beginShape(LINES);
    for (Coordinates v : vertices) {
      vertex(v.x, v.y);
    }
    endShape();
    
    textFont(f);
    fill(100);
    text("10", compos.x, compos.y + 6);
    textAlign(CENTER);
  }

}

