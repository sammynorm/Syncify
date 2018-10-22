package sammynorm.syncify.Model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import sammynorm.syncify.SpotifyDataManager.UserUpdates;

public class FireBaseUtil {


    private static void addUserToDB(String userName, String id, String accname, String imageURL) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, User> users = new HashMap<>();
        //imageURL looks like it's too long to put in there. WIll have to call mnually
        users.put(id, new User(userName, accname, id,  imageURL, null, true, 0));
        DatabaseReference myRef = mDatabase.getReference("userDetails");
        myRef.setValue(users);
    }

    public static void doesUserExist(final String id, final String display_name, final String imageURI) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(id)) {

                       // addUserToDB(username, id, display_name, imageURI); //use shared prefs
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }

        });
        }


    //Needs UserID as reference, only thing that matters is songpos,songuri,isPaused?
    public static void updateSongInfo(String userid, String uri, double songPosition, boolean songState) {
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
                        // Write failed
                        // ...
                    }
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
}