package sammynorm.syncify.Model;

public class User {
    String uid;
    String username;
    String accName;
    String imageURL;
    String songPlayingStr;
    boolean songState;
    long songTime;
    boolean requestedUpdate;



    public User(String uid, String username, String accName, String imageURL, String songPlayingStr, boolean songState, long songTime, boolean requestedUpdate) {
        this.uid = uid;
        this.username = username;
        this.accName = accName;
        this.imageURL = imageURL;
        this.songPlayingStr = songPlayingStr;
        this.songState = songState;
        this.songTime = songTime;
        this.requestedUpdate = requestedUpdate;
    }

    public User() {
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

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSongPlayingStr() {
        return songPlayingStr;
    }

    public void setSongPlayingStr(String songPlayingStr) {
        this.songPlayingStr = songPlayingStr;
    }

    public boolean getSongState() {
        return songState;
    }

    public void setSongState(boolean songState) {
        this.songState = songState;
    }

    public long getSongTime() {
        return songTime;
    }

    public boolean isRequestedUpdate() {
        return requestedUpdate;
    }

    public void setRequestedUpdate(boolean requestedUpdate) {
        this.requestedUpdate = requestedUpdate;
    }

}

