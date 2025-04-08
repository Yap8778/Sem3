public class TestPosition {

    public static void main(String[] args) {

        Position p1 = new Position();
        System.out.println("Default Position: " + p1);

        p1.setLat(2.5f);
        p1.setLon(3.0f);
        System.out.println("Updated Position: " + p1);


        float currentLat = p1.getLat();
        float currentLon = p1.getLon();
        System.out.println("Current Latitude: " + currentLat);
        System.out.println("Current Longitude: " + currentLon);


        /*p1.setPosition(4.0f, 5.0f);
        System.out.println("Position after using setPosition: " + p1);  */

        // Position p2 = p1.getPosition();
        System.out.println("Copied Position p2 from getPosition: " + p1.getPosition());

        System.out.println("");
        // Find flight duration
        System.out.println("Question2a");
        FlightDuration x1 = new FlightDuration();
        System.out.println("Default Information: " + x1);

        // Input with m/s
        x1.setSpeed(21f);
        // Input with km
        x1.setDistance(20.0f);
        System.out.println("Updated Information: " + x1);

        // Calculate and display the flight duration
        float duration = x1.calculateDuration();
        // Get the hours
        int Hours = (int) duration;
        // Translate remaining time to minutes
        int Minutes = (int) ((duration - Hours) * 60);
        // Translate remaining time to seconds
        int Seconds = (int) ((((duration - Hours) * 60) - Minutes) * 60);
        System.out.println("Duration: " + Hours + " hours " + Minutes + " minutes " + Seconds + " seconds");

        System.out.println("");
        // Find the flight Distance
        System.out.println("Question2b");
        FlightDistance x2 = new FlightDistance();
        System.out.println("Default Information: " + x2);

        // Input with m/s
        x2.setSpeed(21f);
        // Input with minutes
        x2.setTime(15.0f);
        System.out.println("Updated Information: " + x2);

        // Calculate and display the flight distance
        float distance = x2.calculateDistance();
        System.out.println("Calculated Flight Duration: " + distance + " km");

        System.out.println("");
        // Find the Remaining Battery life
        System.out.println("Question2c");
        BatteryCheck x3 = new BatteryCheck();
        System.out.println("Default Information: " + x3);

        // Input with m/s
        x3.setSpeed(21f);
        // The total time that Drone can fly, the input with minutes
        x3.setTime(42.0f);
        // Input with km
        x3.setDistance(11f);
        // Input with mAh
        x3.setBattery(4241f);
        System.out.println("Updated Information: " + x3);

        // Calculate and display the Remaining Battery
        float EstimatedBatteryLife = x3.CalculateBattery();
        int FinalBattery = (int) EstimatedBatteryLife;
        System.out.println("Estimated battery life has left: " + FinalBattery + " mAh");
    }
}
