public class FlightDistance
{
    public static final float DEFAULT_SPEED = 0.0f;
    public static final float DEFAULT_TIME = 0.0f;
    private float speed;
    private float time;

    public FlightDistance()
    {
        this.speed = DEFAULT_SPEED;
        this.time = DEFAULT_TIME;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public float getTime()
    {
        return this.time;
    }

    public void setTime(float time)
    {
        this.time = time;
    }

    public float calculateDistance ()
    {
        // Convert the minutes to hours anc m/s to km/h
        // Therefore, the answer will show km as unit
        return (speed*3.6f)*(time/60);
    }

    @Override
    public String toString()
    {
        return "Flight Distance[Speed=" + speed +" m/s, Time=" + time +" minutes]";
    }
}
