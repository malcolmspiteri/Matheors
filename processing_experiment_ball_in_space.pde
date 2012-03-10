import java.util.Collections;

long time = 0;
float metresPerPixel = 10;
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
  o1.move(time);
  o1.paint();
  delay(10);  
  time += 10;
}

void mousePressed() {
  o1.chgAngle();
}

void keyPressed() {
  if (keyCode == UP) {
    o1.incForce();
  } else if (keyCode == DOWN) {
    o1.decForce();
  }
}

class Vector {
  float angle; // The direction of the angle
  float force; // The magnitude of the force being applied, in Newtons
  float velocity; // The velocity, i.e., displacement over time
  float acceleration; // The current acceleration of the vector
  
  Vector(float a, float f) {
    angle = a;
    force = f;
  }
      
  void decrementForce() {
    force--;
  }    

  void incrementForce() {
    force++;
  }
  
  float calculateDisplacement(float massKg, float[] netForce) {    
    int quadrant = (int) angle / 90; // 0: 0-89, 1: 90-179, 2: 180-269, 3: 270-359
    float nfr = (netForce[quadrant % 4] + netForce[(quadrant+1) % 4])
      - (netForce[(quadrant+2) % 4] + netForce[(quadrant+3) % 4]);
    acceleration = nfr / massKg;
    float displacement = (velocity * 0.1) + (0.5 * acceleration * pow(0.1,2));
    velocity += acceleration;
    return displacement;
  }
  
  float calculateOppsingForce(float angle) {
    float degDiff = abs(angle - this.angle);
    if (degDiff > 90) {
      return (this.force / 90) * (degDiff - 90);
    } else {
      return 0f;
    }
  }
  
  void setForceTo(float f) {
    force = f;
  }

  void showStats(int y) {
    text("Force: " + round(force),10,y);
    text("Acceleration: " + acceleration + " m/s²", 10, y+15);
    text("Velocity: " + velocity + " m/s", 10, y+30);
  }
  
}

class Qbject {

  Qbject(float massKg, int posX, int posY) {
    this.massKg = massKg;
    this.posX = posX;
    this.posY = posY;
    
    vectors = new ArrayList();
    Vector initVec = new Vector(30,1);
    addVector(initVec);
  }
  
  List<Vector> vectors;
  Vector currectVector;

  void addVector(Vector v) {
    vectors.add(v);
    currectVector = v;
  }
  
  int posX;
  int posY;
  
  float massKg;
  
  void decForce() {
    currectVector.decrementForce();
  }    

  void incForce() {
    currectVector.incrementForce();
  }    

  void chgAngle() {
    float pmX = mouseX;
    float pmY = mouseY;
    float distX = abs(pmX - posX);
    float distY = abs(pmY - posY);
    float na = degrees(atan(distY / distX));
    if (pmX < posX && pmY < posY) {
      na = (180 - na);
    }
    if (pmX < posX && pmY > posY) {
      na += 180;
    }
    if (pmX > posX && pmY > posY) {
      na = (360 - na);
    }
    float angle = round(na);
    Vector v = new Vector(angle, currectVector.force);
    currectVector.setForceTo(0);
    addVector(v);
    
  }
  
  void move(long time) {
    float newX = 0;
    float newY = 0;
    List<Vector> toClean = new ArrayList();

    // Calculate the net force on the object for all directions
    float[] netForce = new float[] {0f,0f,0f,0f}; // RIGHT, UP, LEFT, DOWN
    for (Vector v : vectors) {      
      float norDeg = v.angle % 90;
      int quadrant = (int) v.angle / 90; // 0: 0-89, 1: 90-179, 2: 180-269, 3: 270-359
      float force1stDirection = (v.force / 90) * norDeg;
      netForce[quadrant % 4] += force1stDirection;
      netForce[(quadrant+1) % 4] += v.force - force1stDirection;
    }      
      println("Net Force RIGHT: " + netForce[0]);
      println("Net Force UP: " + netForce[1]);
      println("Net Force LEFT: " + netForce[2]);
      println("Net Force DOWN: " + netForce[3]);
    for (Vector v : vectors) {      
      float displacement = v.calculateDisplacement(massKg, netForce) * metresPerPixel;    
      if (displacement <= 0 && !v.equals(currectVector)) {
        toClean.add(v);
        continue;
      }
      newX += (cos(radians(v.angle)) * displacement);
      newY += (sin(radians(v.angle)) * displacement * -1);        
    }
    for (Vector c : toClean) {
      vectors.remove(c);
    }
    
    posX += round(newX);
    posY += round(newY);
    if (posX > 1000 + 25)
      posX = -25;
    if (posX < 0 + -25)
      posX = 1025;
    if (posY > 800 + 25)
      posY = -25;
    if (posY < 0 + -25)
      posY = 825;
  }
  
  void paint() {
     ellipse(posX, posY, 50, 50);
     int y = 15;
     for (Vector v : vectors) {
       v.showStats(y);
       y += 65;
      }      
  }
  
}
