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
  float opposingForce; // The sum of the force opposing the force of this vector
  float velocity; // The velocity, i.e., displacement over time
  float acceleration; // The current acceleration of the vector
  List<Vector> updateOppForceList;
  
  Vector(float a, float f) {
    this(a, f, 0);
    updateOppForceList = new ArrayList();
  }
  
  Vector(float a, float f, float of) {
    angle = a;
    force = f;
    opposingForce = of;
  }
      
  void decrementForce() {
    force--;
  }    

  void incrementForce() {
    force++;
  }
  
  float calculateDisplacement(float massKg) {
    acceleration = ((force - opposingForce) / massKg);
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

  void setOpposingForceTo(float f) {
    opposingForce = f;
    if (opposingForce < 0)
      opposingForce = 0;
  }
  
  void showStats(int y) {
    text("Force: " + round(force),10,y);
    text("Opposing force: " + opposingForce,10,y+15);
    text("Acceleration: " + acceleration + " m/s²", 10, y+30);
    text("Velocity: " + velocity + " m/s", 10, y+45);
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
    for (Vector ov : vectors) {
        ov.setOpposingForceTo(ov.opposingForce + v.calculateOppsingForce(ov.angle));        
    }
    vectors.add(v);
    currectVector = v;
  }
  
  int posX;
  int posY;
  
  float massKg;
  
  void decForce() {
    currectVector.decrementForce();
    for (Vector ov : vectors) {
      if (!ov.equals(currectVector)) {          
        if (abs(ov.angle - currectVector.angle) > 90) {
          float cop = ov.opposingForce;
          cop -= (1f/90) * (abs(ov.angle - currectVector.angle) - 90);
          ov.setOpposingForceTo(cop);
        }
      }
    }      
  }    

  void incForce() {
    currectVector.incrementForce();
    for (Vector ov : vectors) {
      if (!ov.equals(currectVector)) {   
        if (abs(ov.angle - currectVector.angle) > 90) {
          float cop = ov.opposingForce;
          cop += (1f/90) * (abs(ov.angle - currectVector.angle) - 90);
          ov.setOpposingForceTo(cop);
        }
      }
    }      
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
    Vector v = new Vector(angle, currectVector.force, 0);
    currectVector.setForceTo(0);
    addVector(v);
    
  }
  
  void move(long time) {
    float newX = 0;
    float newY = 0;
    List<Vector> toClean = new ArrayList();

    for (Vector v : vectors) {
      float displacement = v.calculateDisplacement(massKg) * metresPerPixel;    
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
