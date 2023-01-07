package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Linear extends Part
{
    private DMotor rope = new DMotor();
    private SMotor ring = new SMotor();
    private Sensor bottom = new Sensor();
    private Sensor low = new Sensor();
    private Sensor high = new Sensor();

    public void init(HardwareMap hwm, Telemetry tel)
    {
        this.rope.init(hwm, tel, "rope", DMotor.Direction.Direct);
        this.ring.init(hwm, tel, "ring", SMotor.Direction.Reverse, 0);
        this.bottom.init(hwm, tel, "bottom", true);
        this.low.init(hwm, tel, "low", true);
        this.high.init(hwm, tel, "high", true);


        DMotor[] dl = {this.rope};
        SMotor[] sl = {this.ring};
        Sensor[] snl = {this.bottom, this.low, this.high};

        this.util.init(dl, sl, snl);

        this.step = 0;
        this.telemetry = tel;
    }

    public void start()
    {

    }

    protected void next_step()
    {
        switch (move_type)
        {
            case "go_low" :
                switch (step)
                {
                    case 0:
                        rope.move(0.1);
                        low.activate();
                        break;
                    case 1:
                        rope.move(0.0);
                        break;
                }
                break;

            case "go_high" :
                switch (step)
                {
                    case 0 :
                        rope.move(0.1);
                        high.activate();
                        break;
                    case 1 :
                        rope.move(0.0);
                        break;
                }
                break;

            case "stack_cup" :
                switch (step)
                {
                    case 0 :
                        ring.move(0.9, 0.6, 0.3);
                        break;
                    case 1 :
                        ring.move(-0.9, 0.3);
                        break;
                }
                break;

            case "down" :
                switch (step)
                {
                    case 0 :
                        rope.move(-0.1);
                        bottom.activate();
                        break;
                    case 1 :
                        rope.move(0.0);
                        break;
                }
                break;
        }
        step++;
    }
}
