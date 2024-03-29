// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.security.KeyFactory;

import javax.swing.JApplet;
import javax.xml.namespace.QName;

//import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
//import com.ctre.phoenix6.controls.VictorSPX; 
//import frc.robot.VictorSPX; 
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

//our added imports are below

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


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
  double hangPower = 1; 
  double thirdPower = 1; 
  
  //private VictorSPX hangTopVictor = new VictorSPX(Constants.OperatorConstants.hangTopCANID); 
  VictorSP leftRear = new VictorSP(7); 
  VictorSP leftFront = new VictorSP(8);
  PWMSparkMax rightRear = new PWMSparkMax(3); 
  PWMSparkMax rightFront = new PWMSparkMax(4); 
  PWMSparkMax hangTopMotor = new PWMSparkMax(0); 

  //moters for shooting
  PWMSparkMax feedWheel = new PWMSparkMax(5); 
  PWMSparkMax launchWheel = new PWMSparkMax(6);
  PWMSparkMax thirdWheel = new PWMSparkMax(2); 

  private final Timer timer1 = new Timer(); 

  private final Timer timer2 = new Timer(); 

  //leftFront.set(ControlMode.PercentOutput, left);
  //leftRear.set(ControlMode.PercentOutput, left);

  DifferentialDrive myDrive = new DifferentialDrive(
    (double output) -> {
      leftFront.set(output); 
      leftRear.set(output); 
    }, 
    (double output) -> {
      rightFront.set(output); 
      rightRear.set(output); 
    } 
  );

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


    rightFront.setInverted(false);
    rightRear.setInverted(false); 
    hangTopMotor.setInverted(true); 
    


    feedWheel.setInverted(true);
    launchWheel.setInverted(true);
    thirdWheel.setInverted(false); 


    timer1.start();
    //timer2.start(); 

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
      default: // just cross the line backwards for points
        // put default auto code here
        if (timer1.get() < 4.0) {
          myDrive.tankDrive(-.5, -.5);
        } else {
          myDrive.tankDrive(0, 0);
        }
        break;
      case kSpeakerMiddle: // start middle speaker launch then backup
        if (timer1.get() < 3.0) {
          launchWheel.set(-1.0);
          feedWheel.set(0.0);
          thirdWheel.set(-1.0); 
          myDrive.tankDrive(0.0, 0.0);
        } else if (timer1.get() < 5.0) { // turn on feed wheel to launch the note
          launchWheel.set(-1.0);
          feedWheel.set(-1.0);  
          thirdWheel.set(-1.0);
          myDrive.tankDrive(0.0, 0.0);
        } else if (timer1.get() < 6.5) { // backup over the line for leave points
          launchWheel.set(0);
          feedWheel.set(0);
          thirdWheel.set(0); 
          myDrive.tankDrive(-.5, -.5);
        } else { // done turning off all motors
          launchWheel.set(0.0);
          feedWheel.set(0.0);
          thirdWheel.set(0);
          myDrive.tankDrive(0, 0);
        }
        break;
      case kRedLongAuto:
        if (timer1.get() < 3.0) {
          launchWheel.set(-1.0);
          feedWheel.set(0.0);
          thirdWheel.set(-1.0);
          myDrive.tankDrive(0.0, 0.0);
        } else if (timer1.get() < 5.0) { // turn on feed wheel to launch the note
          launchWheel.set(-1.0);
          feedWheel.set(-1.0);
          thirdWheel.set(-1.0); 
          myDrive.tankDrive(0.0, 0.0);
        } else if (timer1.get() < 6.5) { // backup over the line for leave points
          launchWheel.set(0);
          feedWheel.set(0);
          thirdWheel.set(0);
          myDrive.tankDrive(-.5, -.5);
        } else if (timer1.get() < 7.5) {
          launchWheel.set(0);
          feedWheel.set(0);
          thirdWheel.set(0); 
          myDrive.tankDrive(.4, -.4);
        } else if (timer1.get() < 9.5) {
          launchWheel.set(0);
          feedWheel.set(0);
          thirdWheel.set(0); 
          myDrive.tankDrive(-.5, -.5);
        } else { // done turning off all motors
          launchWheel.set(0.0);
          feedWheel.set(0.0);
          thirdWheel.set(0); 
          myDrive.tankDrive(0, 0);
        }
        break;
    }
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
    else if(driverController.getLeftBumper() == true){
      driveLimit = 0.5;  
    }
    myDrive.tankDrive(-driverController.getLeftY(), -driverController.getRightY()); 
    //myDrive.arcadeDrive(-driverController.getLeftY()*driveLimit, -driverController.getRightX()*driveLimit);

    //hanging code here
    /* if(operatorController.getBButtonPressed()){ 
      if(hangPower == 0){
        timer2.reset();
      }     
    } 
    
    if(timer2.get() < 4.0){
      hangPower = 1.0;
    }
    else{ 
      if(timer2.get() < 7.0){
        hangPower = -1.0; 
    }
    else{
      hangPower = 0;
    }
   
    //hangTopVictor.set(hangPower) */
  
    //launcher code here
    if(operatorController.getAButton()) {
      launchPower = 0.5; 
      thirdPower = 0.5;  
      feedPower = 0.5; 
      
    }
  

    

    else{
      if(operatorController.getLeftBumperPressed()){
        timer1.reset(); 
      }

      if(timer1.get() < 2.0){ //spool up the launch wheel
        launchPower = -1.0;
        thirdPower = -1.0; 
      }

      else if(timer1.get() < 3.0){
        feedPower = -1.0;
        launchPower = -1.0;
        thirdPower = -1.0; 
      }
   
 //     else if(timer1.get() < 4.0){
   //     launchPower = 1.0; 
     //   feedPower = 1.0; 
       // thirdPower = 1.0;
   // } 
  
    else{
      launchPower = 0; 
      feedPower = 0; 
      thirdPower = 0;
    }
  }

    launchWheel.set(launchPower); 
    feedWheel.set(feedPower); 
    thirdWheel.set(thirdPower); 
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
  public void simulationPeriodic() {
  }
} 