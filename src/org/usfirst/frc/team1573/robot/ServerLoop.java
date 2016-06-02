package org.usfirst.frc.team1573.robot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;

/* Commands: (25/5/16 - 12:56)
 * 
 * -> CANTalon:New:(int id):(int port):(boolean inverted) 
 * -> CANTalon:Set:(int id):(double speed)
 * 
 * -> Victor:New:(int id):(int port):(boolean inverted)
 * -> Victor:Set:(int id):(double speed)
 * 
 * -> Compressor:(int pcmPort)
 * 
 * -> Solenoid:New:(int id):(int pcmPort):(int port) 
 * -> Solenoid:Set:(int id):(boolean on)
 * 
 * -> Relay:New:(int id):(int port) 
 * -> Relay:Set:(int id):(boolean on)
 * 
 * -> AnalogInput:New:(int id):(int port)
 * <- AnalogInput:Val:(int id):(double value)
 */

class ServerLoop implements Runnable {
	private static Socket socket;
	private Robot robot;

	public ServerLoop(Robot robot) {
		this.robot = robot;
	}

	public void run() {
		while (true) {
			try {
				int port = 25000;
				ServerSocket serverSocket = new ServerSocket(port);
				System.out.println("Server Started and listening to the port " + port);

				// Server is running always.
				while (true) {
					String msg = "";
					socket = serverSocket.accept();
					System.out.println("PC connected on address: " + socket.getInetAddress());
					if (socket.isClosed()) {
						break;
					}
					InputStream is = socket.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					OutputStream os = socket.getOutputStream();
					OutputStreamWriter osw = new OutputStreamWriter(os);
					BufferedWriter bw = new BufferedWriter(osw);
					Thread sensorThread = new Thread("SensorThread"){
						public void run () {
							while (true) {
								for (int i = 0; i < robot.analogInputs.length; i++) {
									if (robot.analogInputs[i] == null) {
										continue;
									}
									try {
										bw.write("analoginput:val:"+i+":"+robot.analogInputs[i].getVoltage() + "\n");
										bw.flush();
									} catch (IOException e) {
									}
								}
							}
						}
					};
					sensorThread.start();
					while (msg != null) {
						// Reading the message from the client
						msg = br.readLine();
						if (msg != null && msg != "") {

							String[] message = msg.split(":");

							switch (message[0].toLowerCase()) {
							case "cantalon":
								switch (message[1].toLowerCase()) {
								case "new":
									assert message.length == 5;
									robot.motors[toInt(message[2])] = new CANTalon(toInt(message[3]));
									robot.motors[toInt(message[2])].setInverted(toBoolean(message[4]));
									break;
								case "set":
									assert message.length == 4;
									robot.motors[toInt(message[2])].set(-toDouble(message[3]));
									break;
								}
								break;
							case "victor":
								switch (message[1].toLowerCase()) {
								case "new":
									assert message.length == 5;
									robot.motors[toInt(message[2])] = new Victor(toInt(message[3]));
									robot.motors[toInt(message[2])].setInverted(toBoolean(message[4]));
									break;
								case "set":
									assert message.length == 4;
									robot.motors[toInt(message[2])].set(-toDouble(message[3]));
									break;
								}
								break;
							case "compressor":
								assert message.length == 2;
								robot.compressor = new Compressor(toInt(message[1]));
								break;
							case "solenoid":
								robot.cmd = msg;
								switch (message[1].toLowerCase()) {
								case "new":
									assert message.length == 5;
									robot.solenoids[toInt(message[2])] = new Solenoid(toInt(message[3]),
											toInt(message[4]));
									break;
								case "set":
									assert message.length == 4;
									robot.solenoids[toInt(message[2])].set(toBoolean(message[3]));
									break;
								}
								break;
							case "relay":
								robot.cmd = msg;
								switch (message[1].toLowerCase()) {
								case "new":
									assert message.length == 4;
									robot.relays[toInt(message[2])] = new Relay(toInt(message[3]));
									break;
								case "set":
									assert message.length == 4;
									robot.relays[toInt(message[2])].set(toInt(message[3]) > 0 ? Value.kForward : toInt(message[3]) < 0 ? Value.kReverse : Value.kOff);
									break;
								}
								break;
							case "analoginput":
								robot.cmd = msg;
								if (message[1].toLowerCase().equals("new")) {
									assert message.length == 4;
									robot.analogInputs[toInt(message[2])] = new AnalogInput(toInt(message[3]));
								}
								break;
							}
						}
					}
					try {
						sensorThread.interrupt();
					} catch (Exception e) {
						
					}
					robot.reset();
					serverSocket.close();
					System.out.println("PC disconnected.");
				}
			} catch (BindException e) {
				System.err.println("Attempted to open server but the port was taken. Not attempting anymore.");
				break;
			} catch (SocketException e) {
				// Ignore
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	private int toInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private Double toDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	private boolean toBoolean(String s) {
		return s.toLowerCase().equals("true") || s.equals("1") || s.equals("on") || s.equals("yes");
	}
}