//test
package org.firstinspires.ftc.teamcode.FTC2019;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import java.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.List;

@Autonomous(name="FTC_2019_Finalbot_USA_Auto_Silver_Side", group ="FTC 2019")

public class FTC_2019_Finalbot_USA_Auto_Silver_Side extends Nav {

    FTC_2019_USA_Init robot = new FTC_2019_USA_Init();

    String turn;
    public void initial(){

        robot.init(hardwareMap);
        robot.Lfront.setPower(0);
        robot.Rfront.setPower(0);
        robot.Rback.setPower(0);
        robot.Lback.setPower(0);

        robot.runModeSet("reset");
        robot.runModeSet("encoder");

        robot.Latch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.Latch.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.Latch.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void Latching(double Power, int Posistion){
        robot.Latch.setTargetPosition(Posistion);
        robot.Latch.setPower(Power);
    }
    public void Latchup()
    {
        Latching(1,0);
    }
    public void Latchdown()
    {
        Latching(1,robot.Latch_Limit );
    }

    public void SetDistanceToGo(double DistanceInCm, double LocalPowerAll, int LfrontEncoder, int RfrontEncoder, int LbackEncoder, int RbackEncoder){
        double DiameterOfWheel = 10;
        final double EncoderValue = 1680;
        final double GearRatio = 1;
        final double Pi = Math.PI;
        double Circumference = DiameterOfWheel*Pi;
        double DistanceOfEachCircleOfMotor = Circumference*GearRatio;
        double ValueForEncoderFor1Cm = (EncoderValue/DistanceOfEachCircleOfMotor);
        double ValueForEncoderTemp = 0;
        int ValueForEncoder = 0;
        int tmp = 0;

        ValueForEncoderTemp = ValueForEncoderFor1Cm*DistanceInCm;
        ValueForEncoder = (int) ValueForEncoderTemp;

        robot.runModeSet("reset");
        robot.runModeSet("position");

        robot.Lfront.setTargetPosition(ValueForEncoder*LfrontEncoder);
        robot.Rfront.setTargetPosition(ValueForEncoder*RfrontEncoder);
        robot.Lback.setTargetPosition(ValueForEncoder*LbackEncoder);
        robot.Rback.setTargetPosition(ValueForEncoder*RbackEncoder);

        robot.Lfront.setPower(LocalPowerAll);
        robot.Rfront.setPower(LocalPowerAll);
        robot.Lback.setPower(LocalPowerAll);
        robot.Rback.setPower(LocalPowerAll);

        while (opModeIsActive() && robot.Lfront.isBusy() && robot.Rfront.isBusy() && robot.Lback.isBusy() && robot.Rback.isBusy()){
            telemetry.update();
        }

        robot.Lfront.setPower(0);
        robot.Rfront.setPower(0);
        robot.Lback.setPower(0);
        robot.Rback.setPower(0);
    }

    public void forward(double Distance, double Power)
    {

        SetDistanceToGo(Distance, Power,1,1,1,1);
    }

    public void backward(double Distance, double Power)
    {
        SetDistanceToGo(Distance, Power,-1,-1,-1,-1);
    }

    public void turnleft(double Distance, double Power)
    {
        SetDistanceToGo(Distance, Power,-1,1,-1,1);
    }

    public void turnright(double Distance, double Power)
    {
        SetDistanceToGo(Distance, Power,1,-1,1,-1);
    }
    public double in(double cm)
    {
        return cm/2.54 ;
    }

    public void turn (double Angle, double Power) {
        double TargetAngle = 0;

        Orientation orientation = robot.imu.getAngularOrientation(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
        robot.runModeSet("encoder");

        if (Angle < 0){
            turn = "left";
            TargetAngle = 360 + Angle;
        }
        else {
            turn = "right";
            TargetAngle = Angle;
        }

        if(turn == "right"){
            while (orientation.thirdAngle < TargetAngle && opModeIsActive()){
                robot.Lfront.setPower(Power);
                robot.Rfront.setPower(Power*-1);
                robot.Lback.setPower(Power);
                robot.Rback.setPower(Power*-1);
            }
        }
        else{           //turn left
            while (orientation.thirdAngle > TargetAngle){
                robot.Lfront.setPower(Power*-1);
                robot.Rfront.setPower(Power);
                robot.Lback.setPower(Power*-1);
                robot.Rback.setPower(Power);
            }
        }

        robot.Lfront.setPower(0);
        robot.Rfront.setPower(0);
        robot.Lback.setPower(0);
        robot.Rback.setPower(0);

    }
    public void LEFT()
    {
        telemetry.addLine("LEFT");
        //go to gold
        go_sideways(135,0,0.6,50);
        //Latchup();
        //turn to claiming
        turn_to_heading(45);
        //push wall
        go_sideways(90,45,0.5,15);
        //wait for alliance
        //sleep(4000);
        //go claiming
        go_forward(80,45,1,false);
        //go right to give space for team marker
        go_sideways(270,45,0.5,10);
        //drop team marker
        robot.Hammer.setPosition(0.6);
        sleep(2000);
        //hammer down
        robot.Hammer.setPosition(0.03);
        //go left to avoid possible silver mineral
        go_sideways(90,45,0.5,10);
        //go to crater
        go_forward(90,45,-1,false);
    }
    public void CENTRE()
    {
        telemetry.addLine("Center");
        //go to gold
        go_forward(25,0,-1,false);
        //return
        go_forward(15,0,1,false);
        //go away
        go_sideways(103,0,1,40);
        //Latchup();
        //turn to claiming
        turn_to_heading(45);
        //push wall
        go_sideways(90,45,0.5,20);
        sleep(500);

        //wait for alliance
        //sleep(4000);

        //go claiming
        go_forward(65,45,1,false);
        //leave wall
        go_sideways(270,45,0.5,5);

        //drop team marker
        robot.Hammer.setPosition(0.6);
        sleep(2000);
        //hammer down
        robot.Hammer.setPosition(0.03);
        //push wall
        go_sideways(90,45,0.5,2);
        //go to crater
        go_forward(80,45,-1,false);
    }

    public void RIGHT()
    {
        telemetry.addLine("RIGHT");
        //go to gold
        go_sideways(225,0,0.7,45);
        //return
        go_sideways(45,0,0.7,23);
        //go away
        go_sideways(90,0,1,60);
        //Latchup();
        //turn to claiming
        turn_to_heading(45);
        //push wall
        go_sideways(90,45,0.5,10);

        //wait for alliance
        //sleep(4000);

        //go claiming
        go_forward(55,45,1,false);
        //leave wall
        go_sideways(270,45,0.5,5);

        //drop team marker
        robot.Hammer.setPosition(0.6);
        sleep(2000);
        //hammer down
        robot.Hammer.setPosition(0.03);
        //push wall
        go_sideways(90,45,0.5,2);
        //go to crater
        go_forward(80,45,-1,false);
    }
    public void left(double Distance, double Power)
    {
        SetDistanceToGo(Distance, Power,-1,1,1,-1);
    }
    public void right(double Distance, double Power)
    {
        SetDistanceToGo(Distance, Power,1,-1,-1,1);
    }

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final String VUFORIA_KEY = "AY8TKWz/////AAABmVxCzalxrU1fupYh/xwHhc46D6Xbwe+vCr4oD8Z4xAyjlTIiFRZ3aYKRHB+QZ89PLKp9XsgoskoC6x9t0udnNkbAV8pKqQqcd1NDQyBWhza6GIccC7BLF63DdSGidSqut5hZ7BGzXRo9m9CAgBqq1rPgNdyEncD4MnPJryST0Tzv0UDuJpiVu04Fub2ErVH9gVadMw5VDzgLKQCRSCHTD/jnXN2Tyw9agjtfMf3rbmg+1jVniX5RsQCbpAdH+Vj/yWAuH1bbYw46FGmDI7RoAuUVsKuVgD0WuDG0fduLZNyHG+d/L7DG7zANu62mHaBDCGkiblTr+QnkBu1JC+ZZWd9XVaXzIwBkEbG6UYfN79fy";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    public String goldmineral = "UNKNOWN";

    public int g1 = 0;
    public int s1 = 0;
    public int s2 = 0;

    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        parameters.cameraName = robot.Webcam;  //use this when use webcam

        sleep(4000);

        // parameters.cameraDirection = CameraDirection.BACK; //use this when use phone

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        sleep(4000);

        //     vuforia.enableConvertFrameToFormat()
    }

    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        sleep(4000);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    public void runOpMode() {

        initial();

        initVuforia();

        Nav_Init();

   /*     while (robot.Latch.isBusy()){
            Latching(1,robot.Latch_Limit);
            telemetry.addData(">",robot.Latch.getCurrentPosition());
            telemetry.update();
        }*/

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        //telemetry.addData(">",robot.Latch.getCurrentPosition());
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();
        Latchdown();
        sleep(4000);
        telemetry.addData(">",robot.Latch.getCurrentPosition());
        ElapsedTime Timer = new ElapsedTime();
        Timer.reset();
        if (opModeIsActive()) {
            if (tfod != null) {
                tfod.activate();
            }

            while (((goldmineral!="Left" && goldmineral!="Right" && goldmineral!="Center") || Timer.seconds() < 3) && Timer.seconds() < 10) {  // min max
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 3) {
                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            int silverMineral2X = -1;
                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = (int) recognition.getLeft();
                                } else {
                                    silverMineral2X = (int) recognition.getLeft();
                                }
                            }

                            g1 = goldMineralX;
                            s1 = silverMineral1X;
                            s2 = silverMineral2X;

                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                    goldmineral = "Left";
                                    telemetry.addData("Gold Mineral Position", "Left");
                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                    goldmineral = "Right";
                                    telemetry.addData("Gold Mineral Position", "Right");
                                } else {
                                    goldmineral = "Center";
                                    telemetry.addData("Gold Mineral Position", "Center");
                                }
                            }
                        }
                        telemetry.addData("Timer",Timer);
                        telemetry.addData("g1: ", g1);
                        telemetry.addData("s1: ", s1);
                        telemetry.addData("s2: ", s2);
                        telemetry.update();
                    }
                }
            }
        }

        if (tfod != null) {
            tfod.shutdown();
        }
        //Codes put bellow here


        go_forward(5,0,-1,false);

        Latching(1,0);

        telemetry.addData("Correct2",robot.Latch.getCurrentPosition());
        telemetry.update();

        switch (goldmineral){
            case "Left":
                LEFT();
                break;
            case "Center":
                CENTRE();
                break;
            case "Right":
                RIGHT();
                break;
            case "UNKNOWN":
                //go to gold
                RIGHT();
                break;
        }

        sleep(5000);
        //expand LS for parking
        robot.LS2.setPower(0.8);
        sleep(1000);
        robot.LS2.setPower(0);








    }

}