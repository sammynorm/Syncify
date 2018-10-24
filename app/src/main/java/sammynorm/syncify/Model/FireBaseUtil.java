package sammynorm.syncify.Model;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sammynorm.syncify.SpotifyDataManager.PlayerUpdates;

public class FireBaseUtil {

    static boolean doesUserExist;

    private static void addUserToDB(String userName, String id, String accName, String imageURL) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, User> users = new HashMap<>();
        DatabaseReference myRef = mDatabase.getReference("userDetails");
        users.put(id, new User(id, userName, accName, imageURL, null, true, 0, false));
        myRef.setValue(users);
    }

    public static void doesUserExistByID(final String id, final String userName, final String display_name, final String imageURI) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(id)) {
                    addUserToDB(userName, id, display_name, imageURI);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Needs UserID as reference, only thing that matters is songpos,songuri,isPaused?
    //updates Songinfo to FIREBASE
    public static void updateFireBaseSongInfo(String userid, String uri, long songPosition, boolean songState) {
        final double initialTime = System.nanoTime();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> songDetails = new HashMap<>();
        songDetails.put("songPlayingStr", uri);
        songDetails.put("songState", songState);
        songDetails.put("songTime", songPosition);

        mDatabase.child("userDetails").child(userid).updateChildren(songDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        double finalTime = System.nanoTime();
                        System.out.println((finalTime - initialTime) / 1000000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }


    //For Suggestions in searchBar
    public static List<User> getUserListFromQuery(final String queryString) {
        final List<User> myList = new ArrayList<>();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("username").getValue(String.class).toLowerCase().equals(queryString.toLowerCase())) {
                        User user = ds.getValue(User.class);
                        myList.add(user);
                    } else {
                        System.out.println("user does not exist!");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return myList;
    }

    public static boolean subscribeToRemoteUserIfExists(final String username) {
         doesUserExist(username, new UserNameCallback() {
            @Override
            public boolean onCallback(boolean doesExist) {
                ;//NEEDS TO TELL IF USERNAME EXISTS IN DB FROM CALLBACK
            }
        });
    }

    private static void remoteSubscriptionMethod(final String URI) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userDetails/" + URI);
        final PlayerUpdates playerUpdates = PlayerUpdates.getInstance();

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String prevChildKey) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot parent) {
                        User updatedUser = parent.getValue(User.class);
                        playerUpdates.setPlayBack(updatedUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) { }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private static void doesUserExist(final String username, final UserNameCallback userNameCallback) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("username").getValue(String.class).toLowerCase().equals(username.toLowerCase())) {
                        doesUserExist = true;
                        remoteSubscriptionMethod(ds.getKey());
                        userNameCallback.onCallback(true);
                    } else{
                        doesUserExist = false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    //This is a listener to the USERS field, so when it changes this will observe and trigger a force refresh of the local users player
    public static void addSongDetailsRequestObserver(String uid){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userDetails/" + uid + "/requestedUpdate");
        final PlayerUpdates playerUpdates = PlayerUpdates.getInstance();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("true")){
                    playerUpdates.forceUpdateSongDetails();
                    Map<String, Object> users = new HashMap<>();
                    users.put("requestedUpdate", false);
                    ref.updateChildren(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private interface UserNameCallback{
        boolean onCallback(boolean doesExist);
        }
}

