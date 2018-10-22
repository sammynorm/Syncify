package sammynorm.syncify.Model;

public class User {

    String uid;
    String username;
    String imageURL;
    String songPlaying;
    boolean songState;
    double songTime;

    public User(String uid, String username, String imageURL, String songPlaying, boolean songState, double songTime) {
        this.uid = uid;
        this.username = username;
        this.imageURL = imageURL;
        this.songPlaying = songPlaying;
        this.songState = songState;
        this.songTime = songTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSongPlaying() {
        return songPlaying;
    }

    public void setSongPlaying(String songPlaying) {
        this.songPlaying = songPlaying;
    }

    public boolean getSongState() {
        return songState;
    }

    public void setSongState(boolean songState) {
        this.songState = songState;
    }

    public double getSongTime() {
        return songTime;
    }

    public void setSongTime(double songTime) {
        this.songTime = songTime;
    }


}
