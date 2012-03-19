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


Qbject o1;
Qbject o2;
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
  o1 = new Qbject(5, HALF_WIDTH, HALF_HEIGHT, new Force(0, 0));
  o2 = new Qbject(10, 100, 100, new Force(300, 5, FPS * 3));
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
  o1.move();
  o1.paint();
  if (DEBUG)
    o1.paintStats();
  o2.move();
  o2.paint();
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
    o1.collideWith(o2);
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

class Qbject {

  int posX;
  int posY;
  float massKg;
  boolean incForceOn = false;
  boolean decForceOn = false;
  boolean rotateRight = false;
  boolean rotateLeft = false;

  Qbject(float massKg, int posX, int posY, Force initForce) {
    this.massKg = massKg;
    this.posX = posX;
    this.posY = posY;
    addForce(0, initForce);
  }
  
  MotionVector rightMotionVector = new MotionVector(0);
  MotionVector upMotionVector = new MotionVector(90);
  MotionVector leftMotionVector = new MotionVector(180);
  MotionVector downMotionVector = new MotionVector(270);
  
  Map<Integer, Force> forces = new HashMap();
  int forceNumber = 0;
  
  void addForce(Force f) {
    addForce(++forceNumber, f);
  }
  
  void addForce(int id, Force f) {
    forces.put(id, f);
  }

  void zeroForce() {
    if (forces.size() > 0)
    forces.get(0).magnitude = 0;
  }    

  void decForce() {
    if (forces.size() > 0)
    forces.get(0).magnitude--;
  }    

  void incForce() {
    if (forces.size() > 0)
    forces.get(0).magnitude++;
  }    

  void collideWith(Qbject other) {
    // Find the velocity of the center of mass of the 2-Qbject system. 
    float vcm = (this.upMotionVector.calculateMomentum(massKg) + other.upMotionVector.calculateMomentum(other.massKg)) / (this.massKg + other.massKg);
    println("Initial velcoity 1: " + this.upMotionVector.velocity);
    println("Initial velcoity 2: " + other.upMotionVector.velocity);
    println("System Velcoity: " + vcm);
    // Next we find the velocity of each Qbject in the coordinate system (frame) that is moving along with the center of mass.
    float vcmt = this.upMotionVector.velocity - vcm;
    float vcmo = other.upMotionVector.velocity - vcm;
    // Next we reflect (reverse) each velocity in this center of mass frame, and translate back to the stationary coordinate system. 
    float vft = (vcmt * -1) + vcm;
    float vfo = (vcmo * -1) + vcm;
    println("Final velcoity 1: " + vft);
    println("Final velcoity 2: " + vfo);
    // Now we know the change in velocity for each object so we can calculate the impluse
    float impt = ((vft - this.upMotionVector.velocity) * this.massKg) / SECONDS_PER_TICK;
    println("Impulse 1: " + impt);
    this.addForce(new Force(90, impt, 1));
    
    float impo = ((vfo - other.upMotionVector.velocity) * other.massKg) / SECONDS_PER_TICK;
    println("Impulse 2: " + impo);
    other.addForce(new Force(90, impo, 1));
    
    
  }
  
  void move() {
    float newX = 0;
    float newY = 0;
    
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
    
    float displacement = 0;
    displacement = rightMotionVector.calculateDisplacement(massKg, netForce[0]-netForce[2]) * METRES_PER_PIXEL;    
    newX += (cos(radians(rightMotionVector.direction)) * displacement);
    newY += (sin(radians(rightMotionVector.direction)) * displacement * -1);        

    displacement = upMotionVector.calculateDisplacement(massKg, netForce[1]-netForce[3]) * METRES_PER_PIXEL;    
    newX += (cos(radians(upMotionVector.direction)) * displacement);
    newY += (sin(radians(upMotionVector.direction)) * displacement * -1);        

    displacement = leftMotionVector.calculateDisplacement(massKg, netForce[2]-netForce[0]) * METRES_PER_PIXEL;    
    newX += (cos(radians(leftMotionVector.direction)) * displacement);
    newY += (sin(radians(leftMotionVector.direction)) * displacement * -1);        

    displacement = downMotionVector.calculateDisplacement(massKg, netForce[3]-netForce[1]) * METRES_PER_PIXEL;    
    newX += (cos(radians(downMotionVector.direction)) * displacement);
    newY += (sin(radians(downMotionVector.direction)) * displacement * -1);        
    
    // Set the new coordinates of the object,
    // rounding to the nearest pixel
    posX += round(newX);
    posY += round(newY);
    
    // Wrap to the opposite side of the screen 
    // if the object's position exceeds it 
    if (posX > width + 25)
      posX = -25;
    if (posX < 0 + -25)
      posX = width + 25;
    if (posY > height + 25)
      posY = -25;
    if (posY < 0 + -25)
      posY = height + 25;
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
    
    void paint() {
    //ellipse(posX, posY, 50, 50);
    float angle = forces.size() > 0 ? forces.get(0).direction : 0;
    float p1 = posY + sin(radians(angle)) * 40;
    float p2 = posX - cos(radians(angle)) * 40;
    float a2 = 90 - (angle%90);
    float p3 = sin(radians(angle)) * 15;
    float p4 = cos(radians(angle)) * 15;
    triangle(posX, posY, p2+p3, p1+p4, p2-p3, p1-p4);
    //line(posX, posY, p2, p1);
    //line(p2, p1, p2+p3, p1+p4);
    //line(p2, p1, p2-p3, p1-p4);
  }
  
}
