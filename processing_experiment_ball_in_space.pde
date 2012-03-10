import java.util.Collections;

final float METRES_PER_PIXEL = 10;
final boolean DEBUG = true;

Qbject o1;
PFont f;

void setup() {
  size(1000,800);
  f = loadFont("CourierNewPSMT-22.vlw");
  textFont(f,12);
  fill(0);
  o1 = new Qbject(100, 100, 100);
}

void draw() {
  background(255);
  o1.move();
  o1.paint();
  delay(10);  
}

void mousePressed() {
  o1.chgAngle();
}

void keyPressed() {
  if (keyCode == UP) {
    o1.incForceOn = true;
  }
  if (keyCode == DOWN) {
    o1.decForceOn = true;
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
}

class Vector {
  float angle; // The direction of the angle
  private float force = 0;
  private float velocity; // The velocity, i.e., displacement over time
  private float acceleration; // The current acceleration of the vector

  Vector(float a) {
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
  
  void showStats(int y) {
    text("Force: " + round(force),10,y);
    text("Acceleration: " + acceleration + " m/s²", 10, y+15);
    text("Velocity: " + velocity + " m/s", 10, y+30);
  }
  
}

class Force {
  float angle;
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

  Qbject(float massKg, int posX, int posY) {
    this.massKg = massKg;
    this.posX = posX;
    this.posY = posY;
  }
  
  Vector rightVector = new Vector(0);
  Vector upVector = new Vector(90);
  Vector leftVector = new Vector(180);
  Vector downVector = new Vector(270);
  
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

  void chgAngle() {
    float pmX = mouseX;
    float pmY = mouseY;
    float distX = abs(pmX - posX);
    float distY = abs(pmY - posY);
    float newAngle = degrees(atan(distY / distX));
    if (pmX < posX && pmY < posY) {
      newAngle = (180 - newAngle);
    }
    if (pmX < posX && pmY > posY) {
      newAngle += 180;
    }
    if (pmX > posX && pmY > posY) {
      newAngle = (360 - newAngle);
    }
    if (DEBUG) {
      println("New force at angle " + newAngle);
    }    
    addForce(new Force(newAngle, currentForce == null ? 1 : currentForce.newtons));
  }
  
  void move() {
    float newX = 0;
    float newY = 0;
    
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
      float force1stDirection = (f.newtons / 90) * norDeg;
        netForce[quadrant % 4] += f.newtons - force1stDirection;
        netForce[(quadrant+1) % 4] += force1stDirection;
    }      
    if (DEBUG) {
      println("Net Force RIGHT: " + netForce[0]);
      println("Net Force UP: " + netForce[1]);
      println("Net Force LEFT: " + netForce[2]);
      println("Net Force DOWN: " + netForce[3]);
    }
    
    // Calculate the displacement to move for each vector
    
    float displacement = 0;
    displacement = rightVector.calculateDisplacement(massKg, netForce[0]-netForce[2]) * METRES_PER_PIXEL;    
    newX += (cos(radians(rightVector.angle)) * displacement);
    newY += (sin(radians(rightVector.angle)) * displacement * -1);        

    displacement = upVector.calculateDisplacement(massKg, netForce[1]-netForce[3]) * METRES_PER_PIXEL;    
    newX += (cos(radians(upVector.angle)) * displacement);
    newY += (sin(radians(upVector.angle)) * displacement * -1);        

    displacement = leftVector.calculateDisplacement(massKg, netForce[2]-netForce[0]) * METRES_PER_PIXEL;    
    newX += (cos(radians(leftVector.angle)) * displacement);
    newY += (sin(radians(leftVector.angle)) * displacement * -1);        

    displacement = downVector.calculateDisplacement(massKg, netForce[3]-netForce[1]) * METRES_PER_PIXEL;    
    newX += (cos(radians(downVector.angle)) * displacement);
    newY += (sin(radians(downVector.angle)) * displacement * -1);        
    
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
    ellipse(posX, posY, 50, 50);
    if (DEBUG) {
      int y = 15;
      rightVector.showStats(y);
      y += 65;
      upVector.showStats(y);
      y += 65;
      leftVector.showStats(y);
      y += 65;
      downVector.showStats(y);
    }
  }
  
}
