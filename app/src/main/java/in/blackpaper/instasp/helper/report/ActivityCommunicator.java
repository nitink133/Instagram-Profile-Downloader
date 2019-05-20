package in.blackpaper.instasp.helper.report;



/**
 * Singleton:
 * Used to send data between certain Activity/Services within the same process.
 * This can be considered as an ugly hack inside the Android universe. **/
public class ActivityCommunicator {

    private static ActivityCommunicator activityCommunicator;

    public static ActivityCommunicator getCommunicator() {
        if(activityCommunicator == null) {
            activityCommunicator = new ActivityCommunicator();
        }
        return activityCommunicator;
    }

    public volatile Class returnActivity;
}
