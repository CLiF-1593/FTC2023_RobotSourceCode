package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@TeleOp(name = "TeleOp", group = "")
public class TeleOpMode extends OpMode {
    private Wheel wheel_part = new Wheel();
    private Linear linear_part = new Linear();
    private Pincer pincer_part = new Pincer();

    private double slow_rate;
    private boolean adjusting = false;
    private boolean stop = false;

    @Override
    public void init()
    {
        wheel_part.init(hardwareMap, telemetry);
        linear_part.init(hardwareMap, telemetry);
        pincer_part.init(hardwareMap, telemetry);
    }

    @Override
    public void start()
    {
        wheel_part.start();
        linear_part.start();
        pincer_part.start();
    }

    @Override
    public void loop()
    {
        //Update
        wheel_part.update();
        linear_part.update();
        pincer_part.update();

        //Wheel Part
        if(gamepad1.left_trigger > 0.7 || gamepad1.right_trigger > 0.7)
        {
            slow_rate = 0.3;
        }
        else
        {
            slow_rate = 1.0;
        }

        if(gamepad1.dpad_up) {
            wheel_part.move(0.5*slow_rate, Wheel.Direction.Forward);
        }
        else if(gamepad1.dpad_down) {
            wheel_part.move(0.5*slow_rate, Wheel.Direction.Backward);
        }
        else if(gamepad1.dpad_right) {
            wheel_part.move(0.5*slow_rate, Wheel.Direction.Right);
        }
        else if(gamepad1.dpad_left) {
            wheel_part.move(0.5*slow_rate, Wheel.Direction.Left);
        }
        else if(gamepad1.left_bumper){
            wheel_part.move(0.5*slow_rate, Wheel.Direction.TurnLeft);
        }
        else if(gamepad1.right_bumper){
            wheel_part.move(0.5*slow_rate, Wheel.Direction.TurnRight);
        }
        else {
            wheel_part.move(0.0,  Wheel.Direction.Forward);
        }

        if(!adjusting){
            //Linear
            if(linear_part.finish() && pincer_part.finish()) {
                if (gamepad2.y) {
                    pincer_part.start_step("release");
                    linear_part.start_step("simple_stack_cup");
                } else if (gamepad2.x) {
                    pincer_part.start_step("release");
                    linear_part.start_step("go_low");
                } else if (gamepad2.b) {
                    pincer_part.start_step("release");
                    linear_part.start_step("go_high");
                }
            }

            //Pincer
            if(pincer_part.finish()) {
                if (gamepad2.a) {
                    pincer_part.start_step("pick_up");
                }
            }
        }

        //Emergency Stop
        if((gamepad1.left_bumper && gamepad1.right_bumper && (gamepad1.left_trigger>0.9) && (gamepad1.right_trigger>0.9))
        || (gamepad2.left_bumper && gamepad2.right_bumper && (gamepad2.left_trigger>0.9) && (gamepad2.right_trigger>0.9))){
            this.stop = true;
            wheel_part.emergency_stop();
            linear_part.emergency_stop();
            pincer_part.emergency_stop();

            wheel_part.finish_step();
            linear_part.finish_step();
            pincer_part.finish_step();

            wheel_part.init(hardwareMap, telemetry);
            linear_part.init(hardwareMap, telemetry);
            pincer_part.init(hardwareMap, telemetry);
        }
        if(this.stop){
            if(gamepad1.left_stick_button && gamepad1.right_stick_button){
                this.stop = false;
            }
        }

        //Adjusting
        if(gamepad2.right_bumper && gamepad2.left_bumper && !this.stop){
            if(this.pincer_part.finish() && this.linear_part.finish()){
                this.adjusting = true;
            }
        }
        else if(adjusting){
            this.pincer_part.adjust_axis(0);
            this.linear_part.adjust_rope(0);
            this.adjusting = false;
        }

        //adjusting pincer & linear (adjust mode or emergency stop mode)
        if(this.adjusting || this.stop) {
            if (pincer_part.finish()) {
                if (gamepad2.dpad_up) {
                    this.pincer_part.adjust_axis(1);
                } else if (gamepad2.dpad_down) {
                    this.pincer_part.adjust_axis(-1);
                } else {
                    this.pincer_part.adjust_axis(0);
                }
            }
            if (linear_part.finish()) {
                if (gamepad2.right_trigger > 0.1) {
                    this.linear_part.adjust_rope(gamepad2.right_trigger);
                } else if (gamepad2.left_trigger > 0.1) {
                    this.linear_part.adjust_rope(-gamepad2.left_trigger);
                } else {
                    this.linear_part.adjust_rope(0);
                }
            }
        }
    }
}
