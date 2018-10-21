package sammynorm.syncify.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FireBaseUtil {

    private static void addUserToDB(String id, String displayname, String imageURL) {
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        User user = new User(id, displayname, imageURL, null, true, 0);
        mDatabase.collection("Accounts").document(id).set(user);
    }

    public static void doesUserExist(final String id, final String display_name, final String imageURI) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Accounts").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.d(TAG, "User Found -- DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        Log.d(TAG, "No such document");
                        FireBaseUtil.addUserToDB(id, display_name, imageURI);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    //Needs UserID as reference, only thing that matters is songpos,songuri,isPaused?
    public static void updateSongInfo(String userid, String uri, double songPosition, boolean songState) {
        final double initialTime = System.nanoTime();
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        DocumentReference updateRef = mDatabase.collection("Accounts").document(userid);

        Map<String, Object> songDetails = new HashMap<>();
        songDetails.put("songPlayingStr", uri);
        songDetails.put("songState", songState);
        songDetails.put("songTiming", songPosition);


        //ahh my god average write time to firebase is like .3 seconds going to have to try a different solution
        updateRef
                .update(songDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        double finalTime = System.nanoTime();

                        Log.d(TAG, "DocumentSnapshot successfully updated");
                        System.out.println((finalTime - initialTime) / 1000000);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}