package sammynorm.syncify.Model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sammynorm.syncify.Activity.HomeActivity;
import sammynorm.syncify.Activity.UserNameSelect;
import sammynorm.syncify.SpotifyDataManager.PlayerUpdates;
import sammynorm.syncify.SpotifyDataManager.UserUpdates;

import static android.content.ContentValues.TAG;

public class FireBaseUtil {

    private static void addUserToDB(String userName, String id, String accName, String imageURL) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, User> users = new HashMap<>();
        DatabaseReference myRef = mDatabase.getReference("userDetails");

        users.put(id, new User(id, userName, accName, imageURL, null, true, 0));
        myRef.setValue(users);
    }
    public static void doesUserExist(final String id, final String userName, final String display_name, final String imageURI) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(id)) {
                        addUserToDB(userName, id, display_name, imageURI); //use shared prefs
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){}});}

    //Needs UserID as reference, only thing that matters is songpos,songuri,isPaused?
    //updates Songinfo to FIREBASE
    public static void updateSongInfo(String userid, String uri, long songPosition, boolean songState) {
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
                    public void onFailure(@NonNull Exception e) { }
                });
        }

        public static List<User> getSearchList(final String queryString) {
            final List<User > myList = new ArrayList<User>();

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("username").getValue(String.class).toLowerCase().equals(queryString.toLowerCase())) {
                            User user = ds.getValue(User.class);
                            myList.add(user);
                        } else {
                            System.out.println("user does not exist!");
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
            return myList;
        }

    public static void subscribeToUserbyUNCheck(final String username) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("username").getValue(String.class).toLowerCase().equals(username.toLowerCase())) {
                        subscriptionMethod(ds.getKey());
                    } else {
                        System.out.println("user does not exist!");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public static void subscriptionMethod(String URI)
    {
        System.out.println("User URI is: " + URI);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userDetails");
        final PlayerUpdates playerUpdates = PlayerUpdates.getInstance();


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("The updated post title is: " + dataSnapshot.child("songPlayingStr"));
                User updatedUser = dataSnapshot.getValue(User.class);
                playerUpdates.setPlayBack(updatedUser);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}