package sammynorm.syncify.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FireBaseUtil {



    private static void addUserToDB(String id, String displayname, String imageURL) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, User> users = new HashMap<>();
        users.put(id, new User(id, displayname, imageURL, null, true, 0));
        DatabaseReference myRef = mDatabase.getReference("userDetails");
        myRef.setValue(users);
        }

    public static void doesUserExist(final String id, final String display_name, final String imageURI) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(id)) {
                    addUserToDB(id, display_name, imageURI);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }


   //Needs UserID as reference, only thing that matters is songpos,songuri,isPaused?
    public static void updateSongInfo(String userid, String uri, double songPosition, boolean songState) {
        final double initialTime = System.nanoTime();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> songDetails = new HashMap<>();
        songDetails.put("songPlayingStr", uri);
        songDetails.put("songState", songState);
        songDetails.put("songTiming", songPosition);


        //ahh my god average write time to firebase is like .3 seconds going to have to try a different solution
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
}