
package org.usfirst.frc.team1573.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public SpeedController[] motors = new SpeedController[64];
	public Compressor compressor;
	public Solenoid solenoids[] = new Solenoid[64];
	public Relay relays[] = new Relay[64];
	public AnalogInput analogInputs[] = new AnalogInput[64];

	CameraServer server;

	Thread serverThread;
	public String cmd = "";

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		server = CameraServer.getInstance();
		server.setQuality(80);
		//the camera name (ex "cam0") can be found through the roborio web interface
		server.startAutomaticCapture("cam0");
		serverThread = new Thread(new ServerLoop(this));
		serverThread.start();
	}
	
	public void reset() {
		for (int i = 0; i < motors.length; i++) {
			if (motors[i] == null) {
				continue;
			}
			if (motors[i] instanceof CANTalon) {
				((CANTalon) motors[i]).delete();
			} else if (motors[i] instanceof Victor) {
				((Victor) motors[i]).free();
			}
		}
		motors = new SpeedController[64];
		
		for (int i = 0; i < solenoids.length; i++) {
			if (solenoids[i] == null) {
				continue;
			}
			solenoids[i].free();
		}
		solenoids = new Solenoid[64];

		for (int i = 0; i < relays.length; i++) {
			if (relays[i] == null) {
				continue;
			}
			relays[i].free();
		}
		relays = new Relay[64];

		for (int i = 0; i < analogInputs.length; i++) {
			if (analogInputs[i] == null) {
				continue;
			}
			analogInputs[i].free();
		}
		analogInputs = new AnalogInput[64];
		
		if (compressor != null)
		compressor.free();
	}

}
