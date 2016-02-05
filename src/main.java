import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.utility.Delay;

public class main {

	private static final float DISTANCE_TIRES = 0;
	private static final float TIRE_DIAMETER = 0;
	private static final float maxSpeed = 800;
	private static boolean lineFollower = true;
	private static EV3ColorSensor sensor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static EV3LargeRegulatedMotor leftMotor;
	
	private static void turnLeft(int degree, boolean both) {
		float distanceFullCircle = DISTANCE_TIRES * (float) Math.PI;
		float distanceToMove = distanceFullCircle / 360.0f * degree;

		float distanceOneRotation = TIRE_DIAMETER * (float) Math.PI;

		float amountRotations = distanceToMove / distanceOneRotation;
		int degreesToRotate = (int) (amountRotations * 360.0f);

		rightMotor.setSpeed(maxSpeed);
		if (both) {
			leftMotor.setSpeed(maxSpeed);
			leftMotor.rotate((-1) * degreesToRotate, true);
		}
		rightMotor.rotate(degreesToRotate, true);
		if (both) {// Wait for completion of turn
			leftMotor.waitComplete();
		} // Wait for completion of turn
		rightMotor.waitComplete();
	}
	private static void turnRight(int degree, boolean both) {
		float distanceFullCircle = DISTANCE_TIRES * (float) Math.PI;
		float distanceToMove = distanceFullCircle / 360.0f * degree;

		float distanceOneRotation = TIRE_DIAMETER * (float) Math.PI;

		float amountRotations = distanceToMove / distanceOneRotation;
		int degreesToRotate = (int) (amountRotations * 360.0f);

		leftMotor.setSpeed(maxSpeed);
		leftMotor.rotate(degreesToRotate, true);
		if (both) {
			rightMotor.setSpeed(maxSpeed);
			rightMotor.rotate((-1) * degreesToRotate, true);
		}
		leftMotor.waitComplete();
		if (both) {// Wait for completion of turn
			rightMotor.waitComplete();
		}
	}
	private static void searchLine() {
		int deg = 0;
		int inc = 10;
		char lastState = 's';
		float [] samples = new float[sensor.getRedMode().sampleSize()];
		
		while(deg < 170) {
			if(lineFollower) {
				break;
			}
			if(deg < 90) {
				turnLeft(inc, false);
				deg += inc;
				sensor.getRedMode().fetchSample(samples, 0);
				if(samples[0] > 0.9)
				{
					lastState = 'l';
					//Sound.beep();
					break;
				}
					
			}
			
			if(deg == 90)
			{
				turnRight(90, false);
			}
			
			if(deg >= 90)
			{
				turnRight(inc, false);
				deg += inc;
				sensor.getRedMode().fetchSample(samples, 0);
				if(samples[0] > 0.9)
				{
					lastState = 'r';
					//Sound.buzz();
					break;
				}
			}
			
			LCD.drawString("Deg: " + String.valueOf(deg), 0, 2);
			LCD.drawString("State: " + lastState, 0, 4);
		}
		LCD.clear();
		LCD.drawString("Last state: " + lastState, 0, 5);
		LCD.clear();
		leftMotor.stop(true);
		rightMotor.stop(true);
		leftMotor.setSpeed(maxSpeed);
		rightMotor.setSpeed(maxSpeed);
		if(lastState == 'r')
		{
			leftMotor.forward();
			leftMotor.forward();
			while(lineFollower) {
				if(lineFollower) {
					break;
				}
				sensor.fetchSample(samples, 0);
				if(samples[0] > 0.9)
				{
					leftMotor.stop(true);
					rightMotor.stop(true);
					break;
				}
				
			}
		}
	}
	public static void main(String[] args) {
		
		sensor = new EV3ColorSensor(SensorPort.S1);
		leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		float leftSpeed, rightSpeed = 0;
		float[] sample = new float[sensor.sampleSize()];
		float maxSpeed = 800, diffSpeed = 250, initSpeed, count = 0;
		initSpeed = maxSpeed - 2 * diffSpeed;
		LCD.clear();
		LCD.drawString("Line Follower", 0, 0);
		
		Button.DOWN.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(Key k) {
				lineFollower = false; 		
			}

			@Override
			public void keyReleased(Key k) {
				// TODO Auto-generated method stub
				
			}});
		searchLine();
		
		while(lineFollower) {
			if(count > 50000)
			{
				break;
			}
			
			sensor.fetchSample(sample, 0);
			if(Math.abs(leftMotor.getSpeed() - rightMotor.getSpeed()) < 50 && sample[0] < 0.2) {
				leftMotor.stop(true);
				rightMotor.stop(true);
				break;
			}
			leftSpeed = (sample[0] * maxSpeed - diffSpeed);
			rightSpeed = initSpeed - leftSpeed;
			
			if(leftSpeed  > 0) {
				leftMotor.setSpeed(leftSpeed);
				leftMotor.forward();	
			} else {
				leftMotor.setSpeed((-1) * leftSpeed);
				leftMotor.backward();	
			}
			
			if(rightSpeed  > 0) {
				rightMotor.setSpeed(rightSpeed);
				rightMotor.forward();	
			} else {
				rightMotor.setSpeed((-1) * rightSpeed);
				rightMotor.backward();	
			}
			count++;
		}
	}

}
