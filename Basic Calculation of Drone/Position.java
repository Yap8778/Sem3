public class Position
{
    public static final float DEFAULT_LATITUDE = 0.0f;
    public static final float DEFAULT_LONGTITUDE= 0.0f;

    private float latitude;
    private float longtitude;

    public Position()
    {
        this.latitude = DEFAULT_LATITUDE;
        this.longtitude = DEFAULT_LONGTITUDE;
    }

    public Position(float latitude, float longtitude)
    {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public float getLat()
    {
        return this.latitude;
    }

    public void setLat(float latitude)
    {
        this.latitude = latitude;
    }

    public float getLon()
    {
        return this.longtitude;
    }

    public void setLon (float longtitude)
    {
        this.longtitude = longtitude;
    }

    @Override
    public String toString()
    {
        return "Position[latitude=" + latitude +", longtitude=" + longtitude +" ]";
    }


    public Position getPosition()
    {
        return new Position(this.latitude, this.longtitude);
    }

    public void setPosition (float latitude, float longtitude)
    {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public double distance(float latitude, float longtitude)
    {
        return Math.sqrt(Math.pow((this.latitude - latitude), 2) + Math.pow((this.longtitude - longtitude), 2));
    }
}

