import java.util.Collections;

final float METRES_PER_PIXEL = 10;
final boolean DEBUG = true;
final int WIDTH = 800;
final int HEIGHT = 600;
final int HALF_WIDTH = WIDTH / 2;
final int HALF_HEIGHT = HEIGHT / 2;

Qbject o1;
PFont f;

void setup() {
  size(WIDTH,HEIGHT);
  f = loadFont("CourierNewPSMT-22.vlw");
  textFont(f,12);
  fill(0);
  o1 = new Qbject(100, HALF_WIDTH, HALF_HEIGHT);
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
  delay(10);  
}

void mousePressed() {
  changeDirection();
}

void changeDirection() {
  float pmX = mouseX;
  float pmY = mouseY;
  float distX = abs(pmX - HALF_WIDTH);
  float distY = abs(pmY - HALF_HEIGHT);
  float newAngle = degrees(atan(distY / distX));
  if (pmX < HALF_WIDTH && pmY < HALF_HEIGHT) {
    newAngle = (180 - newAngle);
  }
  if (pmX < HALF_WIDTH && pmY > HALF_HEIGHT) {
    newAngle += 180;
  }
  if (pmX > HALF_WIDTH && pmY > HALF_HEIGHT) {
    newAngle = (360 - newAngle);
  }
  if (DEBUG) {
    println("New force at angle " + newAngle);
  }    
  o1.addForce(new Force(newAngle, 0));
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
  float angle;
}

class MotionVector extends Vector {
  private float force = 0;
  private float velocity; // The velocity, i.e., displacement over time
  private float acceleration; // The current acceleration of the vector

  MotionVector(float a) {
    angle = a;
  }
      
  float calculateDisplacement(float massKg, float newtons) {    
    force = newtons;
    acceleration = newtons / massKg;
    float displacement = (velocity * 0.1) + (0.5 * acceleration * pow(0.1,2));
    velocity += acceleration;
    if (displacement < 0)
      return 0;
    else
      return displacement;
  }
  
  void showStats(int x, int y) {
    text("Force: " + round(force),x,y);
    text("Acceleration: " + acceleration + " m/s²", x, y+15);
    text("Velocity: " + velocity + " m/s", x, y+30);
  }
  
}

class Force extends Vector {
  float newtons;
  
  Force(float a, float n) {
    angle = a;
    newtons = n;
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

  Qbject(float massKg, int posX, int posY) {
    this.massKg = massKg;
    this.posX = posX;
    this.posY = posY;
  }
  
  MotionVector rightMotionVector = new MotionVector(0);
  MotionVector upMotionVector = new MotionVector(90);
  MotionVector leftMotionVector = new MotionVector(180);
  MotionVector downMotionVector = new MotionVector(270);
  
  List<Force> forces = new ArrayList();
  Force currentForce;
  
  void addForce(Force f) {
    forces.clear();
    forces.add(f);
    currentForce = f;
  }
  
  void zeroForce() {
    if (forces.size() > 0)
    forces.get(0).newtons = 0;
  }    

  void decForce() {
    if (forces.size() > 0)
    forces.get(0).newtons--;
  }    

  void incForce() {
    if (forces.size() > 0)
    forces.get(0).newtons++;
  }    

  void move() {
    float newX = 0;
    float newY = 0;
    
    if (rotateRight) {
      float na = forces.get(0).angle - 5;
      if (na < 0) 
        na = 360;
      addForce(new Force(na, 0));
    }
    if (rotateLeft) {
      float na = forces.get(0).angle + 5;
      if (na > 360) 
        na = 0;
      addForce(new Force(na, 0));
    }    
      
    if (incForceOn) {
      incForce();
    }
    if (decForceOn) {
      decForce();
    }

    // Calculate the net force on the object for all directions
    float[] netForce = new float[] {0f,0f,0f,0f}; // RIGHT, UP, LEFT, DOWN
    for (Force f : forces) {      
      float norDeg = f.angle % 90;
      println("Normalised degree: " + norDeg);
      int quadrant = (int) f.angle / 90; // 0: 0-89, 1: 90-179, 2: 180-269, 3: 270-359
      println("Quadrant: " + quadrant);
      // Use Pythagorean Theorem to calculate the runner and riser forces
      float riserForce = sin(radians(norDeg)) * f.newtons;
      float runnerForce = cos(radians(norDeg)) * f.newtons;
      netForce[quadrant % 4] += runnerForce;
      netForce[(quadrant+1) % 4] += riserForce;
    }      
    if (DEBUG) {
      println("Net Force RIGHT: " + netForce[0]);
      println("Net Force UP: " + netForce[1]);
      println("Net Force LEFT: " + netForce[2]);
      println("Net Force DOWN: " + netForce[3]);
    }
    
    // Calculate the displacement to move for each vector
    
    float displacement = 0;
    displacement = rightMotionVector.calculateDisplacement(massKg, netForce[0]-netForce[2]) * METRES_PER_PIXEL;    
    newX += (cos(radians(rightMotionVector.angle)) * displacement);
    newY += (sin(radians(rightMotionVector.angle)) * displacement * -1);        

    displacement = upMotionVector.calculateDisplacement(massKg, netForce[1]-netForce[3]) * METRES_PER_PIXEL;    
    newX += (cos(radians(upMotionVector.angle)) * displacement);
    newY += (sin(radians(upMotionVector.angle)) * displacement * -1);        

    displacement = leftMotionVector.calculateDisplacement(massKg, netForce[2]-netForce[0]) * METRES_PER_PIXEL;    
    newX += (cos(radians(leftMotionVector.angle)) * displacement);
    newY += (sin(radians(leftMotionVector.angle)) * displacement * -1);        

    displacement = downMotionVector.calculateDisplacement(massKg, netForce[3]-netForce[1]) * METRES_PER_PIXEL;    
    newX += (cos(radians(downMotionVector.angle)) * displacement);
    newY += (sin(radians(downMotionVector.angle)) * displacement * -1);        
    
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
  
  void paint() {
    //ellipse(posX, posY, 50, 50);
    float angle = forces.size() > 0 ? forces.get(0).angle : 0;
    float p1 = posY + sin(radians(angle)) * 40;
    float p2 = posX - cos(radians(angle)) * 40;
    float a2 = 90 - (angle%90);
    float p3 = sin(radians(angle)) * 20;
    float p4 = cos(radians(angle)) * 20;
    triangle(posX, posY, p2+p3, p1+p4, p2-p3, p1-p4);
    //line(posX, posY, p2, p1);
    //line(p2, p1, p2+p3, p1+p4);
    //line(p2, p1, p2-p3, p1-p4);
    if (DEBUG) {
      int y = 15;
      rightMotionVector.showStats(width-170, HALF_HEIGHT+15);
      y += 65;
      upMotionVector.showStats(HALF_WIDTH+15, 15);
      y += 65;
      leftMotionVector.showStats(25, HALF_HEIGHT+15);
      y += 65;
      downMotionVector.showStats(HALF_WIDTH+20, height-40);
    }
  }
  
}
