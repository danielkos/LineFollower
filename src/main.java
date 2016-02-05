import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

public class main {

	private static boolean lineFollower = true;
	private static EV3ColorSensor sensor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static EV3LargeRegulatedMotor leftMotor;
	
	
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
		
		while(lineFollower) {
			if(count > 50000)
			{
				break;
			}
			
			sensor.fetchSample(sample, 0);
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
