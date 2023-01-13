package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Wheel extends Part {
    public enum Direction {
        Forward(new DirectionData(0.75,0.75,0.75,0.75)),
        Backward(new DirectionData(-0.75,-0.75,-0.75, -0.75)),
        Left(new DirectionData(-0.5,0.5,0.5,-0.5)),
        Right(new DirectionData(0.5,-0.5,-0.5,0.5)),
        TurnLeft(new DirectionData(-0.25,0.25,-0.25,0.25)),
        TurnRight(new DirectionData(0.25,-0.25,0.25,-0.25));

        private final DirectionData value;
        Direction(DirectionData i) {this.value = i;}
        DirectionData get_value() {return this.value;}

        public static class DirectionData{
            private double front_left_speed = 0.88;
            private double front_right_speed = 0.88;
            private double back_left_speed = 1.0;
            private double back_right_speed = 1.0;

            public double front_left, front_right, back_left, back_right;
            public DirectionData(double front_left, double front_right, double back_left, double back_right){
                this.front_left = front_left * front_left_speed;
                this.front_right = front_right * front_right_speed;
                this.back_left = back_left * back_left_speed;
                this.back_right = back_right * back_right_speed;
            }
        }
    };

    private DMotor front_left = new DMotor();
    private DMotor front_right = new DMotor();
    private DMotor back_left = new DMotor();
    private DMotor back_right = new DMotor();
    private Color color = new Color();
    private Gyro imu;

    public void init(HardwareMap hwm, Telemetry tel, Gyro imu){
        this.front_left.init(hwm, tel, "front left", DMotor.Direction.Reverse);
        this.front_right.init(hwm, tel, "front right", DMotor.Direction.Direct);
        this.back_left.init(hwm, tel, "back left", DMotor.Direction.Reverse);
        this.back_right.init(hwm, tel, "back right", DMotor.Direction.Direct);
        this.color.init(hwm, tel, "color");

        DMotor[] dl = {this.front_left, this.front_right, this.back_left, this.back_right};
        this.util = new RobotUtility();
        Sensor[] sen = {};
        SMotor[] sm = {};
        Color[] clr = {this.color};
        Distance[] dsl = {};
        this.util.init(dl, sm, sen, clr, dsl, imu, tel);

        this.step = 0;
        this.telemetry = tel;
        this.imu = imu;
        this.move_type = "";
        this.finish_step();
    }

    public void start(){

    }

    public void move(double speed, double angle, Direction dir){
        this.front_left.move(speed, angle * dir.get_value().front_left);
        this.front_right.move(speed, angle * dir.get_value().front_right);
        this.back_left.move(speed, angle * dir.get_value().back_left);
        this.back_right.move(speed, angle * dir.get_value().back_right);
    }

    public void move(double speed, Direction dir){
        this.front_left.move(speed * dir.get_value().front_left);
        this.front_right.move(speed * dir.get_value().front_right);
        this.back_left.move(speed * dir.get_value().back_left);
        this.back_right.move(speed * dir.get_value().back_right);
    }

    public void move_stop(){
        this.front_left.move(0);
        this.front_right.move(0);
        this.back_left.move(0);
        this.back_right.move(0);
    }

    protected void next_step(){
        switch (move_type)
        {
            case "signal detection":
                switch (step){
                    case 0:
                        this.move(0.5, 0.77, Direction.Forward);
                        this.color.detect_color(3);
                        break;
                    case 1:
                        this.move_stop();
                        this.delay(0.5);
                        this.finish_step();
                        break;
                }
                break;

            case "first rotation":
                switch(step){
                    case 0:
                        this.move(0.2, Direction.TurnRight);
                        this.imu.activate(-45, Direction.TurnRight);
                        break;
                    case 1:
                        this.move_stop();
                        this.delay(0.3);
                        this.move(0.1, Direction.TurnLeft);
                        this.imu.correction();
                        break;
                    case 2:
                        this.move_stop();
                        this.delay(0.3);
                        this.finish_step();
                        break;
                }
                break;

            case "junction range":
                switch(step){
                    case 0:
                        this.move(0.1, 0.05, Direction.Forward);
                        break;
                    case 1:
                        this.move_stop();
                        this.delay(0.3);
                        this.finish_step();
                        break;
                }
                break;

            case "end junction":
                switch(step){
                    case 0:
                        this.move(0.1, 0.05, Direction.Backward);
                        break;
                    case 1:
                        this.move_stop();
                        this.delay(0.3);
                        this.finish_step();
                        break;
                }
                break;

            case "second rotation":
                switch(step){
                    case 0:
                        this.move(0.2, Direction.TurnLeft);
                        this.imu.activate(45, Direction.TurnLeft);
                        break;
                    case 1:
                        this.move_stop();
                        this.delay(0.3);
                        this.move(0.05, Direction.TurnRight);
                        this.imu.correction();
                        break;
                    case 2:
                        this.move_stop();
                        this.delay(0.3);
                        this.finish_step();
                        break;
                }
                break;

            case "parking site":
                switch (step){
                    case 0:
                        this.move(0.3, 0.01, Direction.Backward);
                        break;
                    case 1:
                        this.move_stop();
                        this.delay(0.3);
                        this.finish_step();
                        break;
                }
                break;

            case "parking":
                switch (step){
                    case 0:
                        switch (this.color.get_parking_position()){
                            case 0:
                                this.move(0.3, 0.4, Direction.Backward);
                                telemetry.addData("Parking", "Fail");
                                break;
                            case 1:
                                this.move(0.3, 0.4, Direction.Left);
                                telemetry.addData("Parking", "1");
                                break;
                            case 2:
                                this.move_stop();
                                telemetry.addData("Parking", "2");
                                break;
                            case 3:
                                this.move(0.3, 0.4, Direction.Right);
                                telemetry.addData("Parking", "3");
                                break;
                        }
                        break;
                    case 1:
                        this.move_stop();
                        this.finish_step();
                        break;
                }
                break;

            default:
                this.finish_step();
                break;
        }
        this.step++;
    }
}