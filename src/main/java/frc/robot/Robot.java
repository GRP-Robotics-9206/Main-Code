// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser; 
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


//our added imports are below

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax; 
// import com.ctre.phoenix.motorcontrol.ControlMode;
// import com.ctre.phoenix.motorcontrol.NeutralMode;
//import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark; 
//import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup; 
//import edu.wpi.first.wpilibj.PWMMotorController; 
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController; 
import edu.wpi.first.wpilibj.drive.DifferentialDrive; 
import edu.wpi.first.wpilibj.Timer; 
import edu.wpi.first.wpilibj.XboxController; 
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 **/
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default"; //go across line only
  private static final String kSpeakerMiddle = "Speaker Middle and Backup";
  private static final String kRedLongAuto = "Red Long Speaker and Backup"; 
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private final XboxController driverController = new XboxController(0); 
  private final XboxController operatorController = new XboxController(2);  

  double driveLimit = 1; 
  double launchPower = 0; 
  double feedPower = 0; 
  //private Command m_autonomousCommand;
  //private RobotContainer m_robotContainer;


  //private final PWMSparkMax leftRear = new PWMSparkMax(1); 
  private PWMSparkMax leftRear = new PWMSparkMax(1); 
  private PWMSparkMax leftFront = new PWMSparkMax(2); 
  private PWMSparkMax rightFront = new PWMSparkMax(3); 
  private PWMSparkMax rightRear = new PWMSparkMax(4); 
  //private final PWMSparkMax leftFront = new PWMSparkMax(2); 
  //private final PWMSparkMax rightFront = new PWMSparkMax(3); 
  //private final PWMSparkMax rightRear = new PWMSparkMax(4); 

  PWMMotorControllerGroup.addFollower(leftFront); 
  PWMMotorControllerGroup.addFollower(rightFront); 
  PWMMotorControllerGroup.addFollower(leftRear); 
  PWMMotorControllerGroup.addFollower(rightRear); 

  //private final MotorControllerGroup leftMotors = new MotorControllerGroup(leftFront, leftRear); 
  //private final MotorControllerGroup rightMotors = new MotorControllerGroup(rightFront, rightRear); 
  //private final PWMMotorControllerGroup leftMotors = new PWMMotorControllerGroup(leftFront, leftRear); 
  private final PWMMotorControllerGroup rightRear = new PWMMotorControllerGroup(rightRear); 
  private final PWMMotorControllerGroup rightFront= new PWMMotorControllerGroup(rightFront); 
  private final PWMMotorControllerGroup leftFront= new PWMMotorControllerGroup(leftFront); 
  private final PWMMotorControllerGroup leftRear= new PWMMotorControllerGroup(leftRear); 


  //private final DifferentialDrive myDrive = new DifferentialDrive(leftMotors, rightMotors); 
  private final DifferentialDrive myDrive = new DifferentialDrive(leftFront, leftRear); 
  private final DifferentialDrive myDrive = new DifferentialDrive(rightFront, rightRear); 




  //leftRear.follow(leftRear); 
  //leftFront.follow(leftFront); 
  //rightRear.follow(rightRear); 
  //rightFront.follow(rightRear);


  private final PWMSparkMax feedWheel = new PWMSparkMax(5); 
  private final PWMSparkMax launchWheel = new PWMSparkMax(6);

  private final Timer timer1 = new Timer(); 

  DifferentialDrive drive = new DifferentialDrive(
    (double output) -> {
      leftFront.set(output); 
      leftRear.set(output); 
    }, 
    (double output) -> {
      rightFront.set(output); 
      rightRear.set(output); 
    }); 

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("Speaker Middle and Backup", kSpeakerMiddle);
    m_chooser.addOption("Red Long Speaker and Backup", kRedLongAuto); 
    SmartDashboard.putData("Auto choices", m_chooser);


    //leftMotors.setInverted(true); 
    leftFront.setInverted(true); 
    leftRear.setInverted(true); 
    rightFront.setInverted(false);
    rightRear.setInverted(false); 
    //rightMotors.setInverted(false); 

    feedWheel.setInverted(true);
    launchWheel.setInverted(true);


    timer1.start(); 

    //m_robotContainer = new RobotContainer();
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    timer1.reset(); 
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kRedLongAuto: 
        if(timer1.get() < 3.0) {
          launchWheel.set(1.0); 
          feedWheel.set(0.0);
          myDrive.tankDrive(0.0, 0.0); 
        }
        else if(timer1.get() < 5.0){ //turn on feed wheel to launch the note
          launchWheel.set(1.0); 
          feedWheel.set(1.0); 
          myDrive.tankDrive(0.0, 0.0); 
        }
        else if(timer1.get() < 6.5){ //backup over the line for leave points 
          launchWheel.set(0); 
          feedWheel.set(0); 
          myDrive.tankDrive(-.5, -.5); 
        }
        else if(timer1.get() < 7.5){ 
          launchWheel.set(0);
          feedWheel.set(0);
          myDrive.tankDrive(.4, -.4); 
        } else if(timer1.get() < 9.5){ 
          launchWheel.set(0); 
          feedWheel.set(0); 
          myDrive.tankDrive(-.5, -.5); 
        }
      }
        else{ //done turning off all motors
          launchWheel.set(0.0); 
          feedWheel.set(0.0); 
          myDrive.tankDrive(0, 0); 
        }
      case kSpeakerMiddle: //start middle speaker launch then backup 
        if(timer1.get() < 3.0){
          launchWheel.set(1.0); 
          feedWheel.set(0.0);
          myDrive.tankDrive(0.0, 0.0); 
        }
        else if(timer1.get() < 5.0){ //turn on feed wheel to launch the note
          launchWheel.set(1.0); 
          feedWheel.set(1.0); 
          myDrive.tankDrive(0.0, 0.0); 
        }
        else if(timer1.get() < 6.5){ //backup over the line for leave points 
          launchWheel.set(0); 
          feedWheel.set(0); 
          myDrive.tankDrive(-.5, -.5); 
        }
        else if(timer1.get() < 7.5) { 
          launchWheel.set(0); 
          feedWheel.set(0); 
          myDrive.tankDrive(.4, -.4); 
        }
        else if (timer1.get() < 9.5) { 
         launchWheel.set(0.0); 
          feedWheel.set(0.0); 
          myDrive.tankDrive(-.5, -.5);
        }  
        else{ //done turning off all motors
          launchWheel.set(0.0); 
          feedWheel.set(0.0); 
          myDrive.tankDrive(0, 0); 
        }
      break;
      case kDefaultAuto:
      default: //just cross the line backwards for points 
      //put default auto code here
        if(timer1.get() < 1.5){
          myDrive.tankDrive(-.5, -.5); 
        }
        else{
          myDrive.tankDrive(0, 0); 
        }
        // Put default auto code here
        myDrive.tankDrive(.3, .3);
        //leftFront.set(.3); 
        //leftRear.set(.3); 
        //rightFront.set(.3); 
        //rightRear.set(.3);  
        break; 
      }
    


  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
     // drive train code here
    if(driverController.getRightBumper() == true){
      driveLimit = 1; 
    }
    else if(driverController.getLeftBumper()){
      driveLimit = 0.5;  
    }
    //myDrive.tankDrive(-driverController.getLeftY(), -driverController.getRightY()); 
    myDrive.arcadeDrive(-driverController.getLeftY()*driveLimit, -driverController.getRightX()*driveLimit);

    
    //launcher code here
    if(operatorController.getLeftBumper()) {
      launchPower = -1; 
      feedPower = -.2; 
    }
    else{
      if(operatorController.getAButtonPressed()){
        timer1.reset(); 
      }

      if(timer1.get() < 1.0){ //spool up the launch wheel
        launchPower = 1; 
        feedPower = 0; 
      }
      else if(timer1.get() < 2.0){
        launchPower = 1; 
        feedPower = 1; 
    } 
    else{
      launchPower = 0; 
      feedPower = 0; 
    }
  }

    launchWheel.set(launchPower); 
    feedWheel.set(feedPower); 
  }
}

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}