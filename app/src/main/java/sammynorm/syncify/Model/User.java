package sammynorm.syncify.Model;

public class User {


    public User(String uid, String username, String songPlaying, int songState, double songTime) {
        this.uid = uid;
        this.username = username;
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

    public int getSongState() {
        return songState;
    }

    public void setSongState(int songState) {
        this.songState = songState;
    }

    public double getSongTime() {
        return songTime;
    }

    public void setSongTime(double songTime) {
        this.songTime = songTime;
    }

    String uid;
    String username;
    String songPlaying;
    int songState;
    double songTime;


}
