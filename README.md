PCRobot Client
==============

This is the server counterpart to the [PCRobot library](https://github.com/Wazzaps/PCRobot).

Installation
------------
* Copy this project to your workspace, and import it in eclipse
* In project preferences, change the team number to yours so eclipse can find the robot
* Clone the PCRobot library and follow the instructions there

Communication protocol
----------------------
The communication takes place using a buffered tcp socket on port 25000

Messages are seperated using the newline character (\n)

The message structure is

component: command: id: [arguments...]

(no spaces)

_Except compressor as it has only one command so it is omitted_

###Commands: (As of 25/5/16 - 12:56)
_-> means program to robot_

_<- means robot to program_

-> CANTalon:New:(int id):(int port):(boolean inverted) 

-> CANTalon:Set:(int id):(double speed)


-> Victor:New:(int id):(int port):(boolean inverted)

-> Victor:Set:(int id):(double speed)


-> Compressor:(int pcmPort)


-> Solenoid:New:(int id):(int pcmPort):(int port) 

-> Solenoid:Set:(int id):(boolean on)


-> Relay:New:(int id):(int port) 

-> Relay:Set:(int id):(boolean on)


-> AnalogInput:New:(int id):(int port)

<- AnalogInput:Val:(int id):(double value)


Licence
-------
This software is licenced under the Mozilla Public License Version 2.0 licence

See the LICENCE file for more information