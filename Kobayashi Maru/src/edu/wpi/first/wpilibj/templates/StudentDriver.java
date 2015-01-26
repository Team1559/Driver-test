/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/**
	THIS IS THE MOST RECENT VERSION OF THE CODE
	CHECK: Working after Saturday (WK3) 1/24/15

	Status: Ready for driver's test
*/


package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.Accelerometer;
import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class StudentDriver extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    CANJaguar l;
    CANJaguar r;
//    Victor l;
//    Victor r;
    RobotDrive rd;
    int mode;
    Timer time;
    double startTime;
    double endTime;
    Joystick gamepad;
    Joystick evaluator;
    Joystick joy1;
    Joystick joy2;
    int issue;
    Accelerometer a;
    double averageAccel;
    double maxAccel;
    double dropValue = 2;
    int totesDropped;
    double read ;
    int sabotages;
    double totalAccel;
    int readNum;
    String s;
    
    public void robotInit() {
        
        try {
            l = new CANJaguar(7);
            r = new CANJaguar(6);
            rd = new RobotDrive(l, r);
            rd.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
            rd.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
            mode = 0;
            a = new Accelerometer(5);
            a.setZero(0.0);
            averageAccel = 0.0;
            totesDropped = 0;
            read = 0.0;
            totalAccel = 0.0;
            readNum = 0;
            
            /*
            VALUE   MODE
            0       arcade (1 stick, wpilibj)
            1       tank (2 stick, gamepad, custom)
            3       alternate [specify]
            */
            
            time = new Timer();
            gamepad = new Joystick(1);
            joy1 = new Joystick(1);
            joy2 = new Joystick(3);
            evaluator = new Joystick(2);
            issue = 0;
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This function is called periodically during operator control
     */
    
    public void startTimer(){
        time.reset();
        startTime = time.get();
        time.start();
    }
    
    public void stopTimer(){
        time.stop();
        endTime = time.get();
    }
    
    public void teleopInit(){
        startTimer();
        System.out.println("================================\nKobayashi Maru: " + mode + "\n================================");
        System.out.println("Captain, 3 Klingon warbirds de-cloaking in this sector!");
        System.out.println("                        ______   ______\n" +
"                      _/_/__\\_\\_/_/__\\_\\_\n" +
"                     /.--  / //-\\\\ \\  --.\\\n" +
"                    // \\  |-|=====|-|  / \\\\\n" +
"                   || _.-' \\-\\_@_/-/ `-._ ||\n" +
"                  _|-'      `-----'      `-|_\n" +
"              _.-'                           `-._\n" +
"          _.-'                                   `-._\n" +
"      _.-'                                           `-._\n" +
"     /                                                   \\\n" +
"   _/                                                     \\_\n" +
"  (o)                                                     (o)\n" +
"  '                                                         `");
        averageAccel = 0.0;
        totesDropped = 0;
        read = 0.0;
    }
    
    public void accel(){
            read = Math.abs(a.getAcceleration());
            readNum++;
            if(read > maxAccel){
                maxAccel = read;
            }

            totalAccel += read;
            averageAccel = totalAccel/readNum;

            if(read > dropValue){
                totesDropped++;
                System.out.println("\n\n\n\n\n\n\n\n\n\n*******TOTES DROPPED!!!!*******");
            }


//        System.out.println(read);

    }
    public void disabledInit(){
       stopTimer();
       System.out.println("\n\n\n\n\n\n\n\n\n\nKOBAYASHI MARU TEST RESULTS:\n---------------------------------");
       System.out.println("Totes dropped: " + totesDropped + " totes");
       System.out.println("Average Accel: " + averageAccel + "g");
       System.out.println("Max Accel:     " + maxAccel + "g");
       System.out.println("Jerk Score:    " + totesDropped*averageAccel + " jRk...Lower is better");
       System.out.println("Time:          " + endTime);
       System.out.println("Sabotages:     " + sabotages + "x15 = " + (sabotages * 10) + " points");
       
       s = "KOBAYASHI MARU TEST RESULTS:\n---------------------------------\nTOTES DROPPED: " + totesDropped + " totes\n"
               + "Average Accel: " + averageAccel + "g\n" + "Max Accel:     " + maxAccel + "g\n" + "Jerk Score:    " + totesDropped*averageAccel + " jRk...Lower is better\n"
               + "Time:          " + endTime + "\nSabotages:     " + sabotages + "x15 = " + (sabotages * 10) + " points";
       SmartDashboard.putString("RESULTS", s);
    }

    public void teleopPeriodic() {
        accel();
        //switch mode
        if(evaluator.getRawButton(1)){ //standard arcade
            mode = 0;
        } else if(evaluator.getRawButton(2)){ //tank dual stick
            mode = 1;
        } else if(evaluator.getRawButton(3)){ //battlefield tank
            mode = 2;
        } else if(evaluator.getRawButton(4)){ //alternate (2-stick tank?)
            mode = 3;
        }
        
        //time controls, breaking
        if(evaluator.getRawButton(5)){
            startTimer();
        } else if(evaluator.getRawButton(6)){
            stopTimer();
        }
        
        if(evaluator.getRawButton(7)){
            issue = 0; //normal driving
            System.out.println("NORMAL DRIVING");
            SmartDashboard.putBoolean("NORMAL", true);
            SmartDashboard.putBoolean("SLOW ROBOT", false);
            SmartDashboard.putBoolean("LEFT MOTOR DEATH!", false);
            SmartDashboard.putBoolean("LAG-O-RAMA", false);
            SmartDashboard.putBoolean("PERIODIC STOP", false);
        } else if(evaluator.getRawButton(8)){
            rd.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
            issue = 1; //slow robot
            sabotages++;
            System.out.println("SLOW ROBOT");
            SmartDashboard.putBoolean("NORMAL", false);
            SmartDashboard.putBoolean("SLOW ROBOT", true);
            SmartDashboard.putBoolean("LEFT MOTOR DEATH!", false);
            SmartDashboard.putBoolean("LAG-O-RAMA", false);
            SmartDashboard.putBoolean("PERIODIC STOP", false);
        } else if(evaluator.getRawButton(9)){
            issue = 2; //leftMotor death
            sabotages++;
            System.out.println("LEFT MOTOR DEATH");
            SmartDashboard.putBoolean("NORMAL", false);
            SmartDashboard.putBoolean("SLOW ROBOT", false);
            SmartDashboard.putBoolean("LEFT MOTOR DEATH!", true);
            SmartDashboard.putBoolean("LAG-O-RAMA", false);
            SmartDashboard.putBoolean("PERIODIC STOP", false);
        } else if(evaluator.getRawButton(10)){
            issue = 3; //delay
            sabotages++;
            System.out.println("LAG-O-RAMA");
            SmartDashboard.putBoolean("NORMAL", false);
            SmartDashboard.putBoolean("SLOW ROBOT", false);
            SmartDashboard.putBoolean("LEFT MOTOR DEATH!", false);
            SmartDashboard.putBoolean("LAG-O-RAMA", true);
            SmartDashboard.putBoolean("PERIODIC STOP", false);
        } else if(evaluator.getRawButton(11)){
            issue = 4; //periodic stop
            sabotages++;
            System.out.println("PERIODIC STOP");
            SmartDashboard.putBoolean("NORMAL", false);
            SmartDashboard.putBoolean("SLOW ROBOT", false);
            SmartDashboard.putBoolean("LEFT MOTOR DEATH!", false);
            SmartDashboard.putBoolean("LAG-O-RAMA", false);
            SmartDashboard.putBoolean("PERIODIC STOP", true);
        }
        
        switch(issue){
            case 0: //normal driving
               rd.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
                rd.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
                 rd.setMaxOutput(1);
                switch(mode){
                case 0: //standard arcade
                    rd.arcadeDrive(gamepad);
                    break;
                case 1: //tank dual stick
                    rd.tankDrive(joy1, joy2);
                    break;
                case 2: //standard arcade - joystick
                    rd.arcadeDrive(joy1);
                    break;
                case 3: //alternate

                    break;                     
                }
            break;
                
            case 1: //SLOW ROBOT
                switch(mode){
                case 0: //standard arcade
                    rd.setMaxOutput(.5);
                    rd.arcadeDrive(gamepad);
                    break;
                case 1: //tank dual stick
                    rd.setMaxOutput(.5);
                    rd.tankDrive(joy1, joy2);
                    break;
                case 2: //standard arcade - joystick
                    rd.setMaxOutput(.5);
                    rd.arcadeDrive(joy1);
                    break;
                case 3: //alternate
                    rd.setMaxOutput(.5);
                    break;                     
                }
            break;
                
            case 2: //left motor death!
                switch(mode){
                case 0: //standard arcade
                    rd.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
                    rd.arcadeDrive(gamepad);
                    break;
                case 1: //tank dual stick
                    rd.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
                    rd.tankDrive(joy1, joy2);
                    break;
                case 2: //standard arcade - joystick
                    rd.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
                    rd.arcadeDrive(joy1);
                    break;
                case 3: //alternate
                    rd.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
                    break;                     
                }
            break;
                
            case 3: //LAG!!!!!
                switch(mode){
                case 0: //standard arcade
                    Timer.delay(Math.abs(evaluator.getZ()));
                    rd.arcadeDrive(gamepad);
                    break;
                case 1: //tank dual stick
                    Timer.delay(Math.abs(evaluator.getZ()));
                    rd.tankDrive(joy1, joy2);
                    break;
                case 2: //standard arcade - joystick
                    Timer.delay(Math.abs(evaluator.getZ()));
                    rd.arcadeDrive(joy1);
                    break;
                case 3: //alternate
                    Timer.delay(Math.abs(evaluator.getZ()));
                    break;                     
                }
            break;
                
            case 4: //Occasional stop
                switch(mode){
                case 0: //standard arcade
                    if(evaluator.getRawButton(12))
                        rd.stopMotor();
                    rd.arcadeDrive(gamepad);
                    break;
                case 1: //tank dual stick
                    if(evaluator.getRawButton(12))
                        rd.stopMotor();
                    rd.tankDrive(joy1, joy2);
                    break;
                case 2: //standard arcade - joystick
                    if(evaluator.getRawButton(12))
                        rd.stopMotor();
                    rd.arcadeDrive(joy1);
                    break;
                case 3: //alternate
                    if(evaluator.getRawButton(12))
                        rd.stopMotor();
                    break;                     
                }
            break;
        }        
 
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
