package sammynorm.syncify.Model;

public class User {
    String uid;
    String userName;
    String accName;
    String imageURL;
    String songPlayingStr;
    boolean songState;
    long songTime;
    String songImageURI;
    boolean requestedUpdate;

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", userName='" + userName + '\'' +
                ", accName='" + accName + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", songPlayingStr='" + songPlayingStr + '\'' +
                ", songState=" + songState +
                ", songTime=" + songTime +
                ", requestedUpdate=" + requestedUpdate +
                '}';
    }

    public User(String uid, String userName, String accName, String imageURL, String songPlayingStr, boolean songState, long songTime,String songImageURI, boolean requestedUpdate) {
        this.uid = uid;
        this.userName = userName;
        this.accName = accName;
        this.imageURL = imageURL;
        this.songPlayingStr = songPlayingStr;
        this.songState = songState;
        this.songTime = songTime;
        this.songImageURI = songImageURI;
        this.requestedUpdate = requestedUpdate;
    }

    public String getSongImageURI() {
        return songImageURI;
    }

    public void setSongImageURI(String songImageURI) {
        this.songImageURI = songImageURI;
    }

    public User() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

