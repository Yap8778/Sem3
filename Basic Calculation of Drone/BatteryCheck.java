public class BatteryCheck
{
    public static final float DEFAULT_SPEED = 0.0f;
    public static final float DEFAULT_DISTANCE = 0.0f;
    public static final float DEFAULT_TIME = 0.0f;
    public static final float DEFAULT_BATTERY = 0.0f;

    private float speed;
    private float distance;
    private float time;
    private float battery;

    public BatteryCheck()
    {
        this.speed = DEFAULT_SPEED;
        this.distance = DEFAULT_DISTANCE;
        this.time = DEFAULT_TIME;
        this.battery = DEFAULT_BATTERY;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public float getDistance()
    {
        return this.distance;
    }

    public void setDistance(float distance)
    {
        this.distance = distance;
    }

    public float getTime()
    {
        return this.time;
    }

    public void setTime(float time)
    {
        this.time = time;
    }

    public float getBattery()
    {
        return this.battery;
    }

    public void setBattery(float battery)
    {
        this.battery = battery;
    }

    public float CalculateBattery ()
    {
        if (speed == 0)
        {
            System.out.println("The data is invalid");
            return DEFAULT_SPEED;
        }
        else
        {
            // Calculate the flight time with minutes as a unit
            float flightTime = (distance / (speed * 3.6f))*60;
            // This is to know in that flightTime how many battery life get consume
            float batteryConsuming = (flightTime/time)*battery;
            int Battery = (int) batteryConsuming;
            // Let users know how many battery life have been consumed
            System.out.println("In this fly it total consumed " + Battery + " mAh");
            // Using the total battery life minus the consuming battery life can get the remaining battery life
            return (battery-batteryConsuming);
        }
    }

    @Override
    public String toString()
    {
        return "Battery Check[Speed=" + speed +" m/s, Distance=" + distance +" km, Time= " + time + " minutes, Battery=" + battery + " mAh]";
    }
}
