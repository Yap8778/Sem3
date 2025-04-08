public class FlightDuration
{
    public static final float DEFAULT_SPEED = 0.0f;
    public static final float DEFAULT_DISTANCE = 0.0f;
    // The speed must be in km/h
    private float speed;
    // Distance in km
    private float distance;

    public FlightDuration()
    {
        this.speed = DEFAULT_SPEED;
        this.distance = DEFAULT_DISTANCE;
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

    public float calculateDuration ()
    {
        if (speed == 0)
        {
            System.out.println("The data is invalid");
            return DEFAULT_SPEED;
        }
        else
        {
            // Convert the speed from m/s to km/h then doing the calculation
            // The answer will show hours as unit
            return distance / (speed * 3.6f);
        }
    }

    @Override
    public String toString()
    {
        return "Flight Duration[Speed=" + speed +" m/s, Distance=" + distance +" km ]";
    }
}
