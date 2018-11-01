package sammynorm.syncify.Model;

import android.content.Context;
import android.os.Handler;
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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import sammynorm.syncify.SpotifyDataManager.PlayerUpdates;
import sammynorm.syncify.SpotifyDataManager.UserUpdates;

public class FireBaseUtil {


    public static boolean doesUserExist;
    private static PlayerUpdates playerUpdates;
    private static UserUpdates userUpdates;
    private static ChildEventListener listener;
    private boolean userNameExists = false;

    public void isUserNameAvailable(final Context context, final String userName){
        userUpdates = new UserUpdates();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        User user = dsp.getValue(User.class);
                        doesUserExist=false;
                        if (user != null && userName.equals(user.getUserName())) {
                            System.out.println(user.getUserName() + userName);
                            userNameExists = true;
                        }
                    }
                    userUpdates.userNameExistanceActivityNotify(context, userNameExists);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

    }

    public static void doesUserExistByID(final String id, final String userName, final String display_name, final String imageURI) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(id)) {
                        addUserToDB(userName, id, display_name, imageURI);
                    }else if(!dataSnapshot.child(id).hasChild(userName)) {
                    addUserToDB(userName, id, display_name, imageURI);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static void addUserToDB(String userName, String id, String accName, String imageURL) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        Map<String, Object> user = new HashMap<>();
        DatabaseReference myRef = mDatabase.getReference();
        user.put("uid", id);
        user.put("userName", userName);
        user.put("accName", accName);
        user.put("imageUrl", imageURL);
        user.put("requestedUpdate", false);
        myRef.child("userDetails").child(id).updateChildren(user);
    }

    public static void updateFireBaseSongInfo(String userid, String uri, long songPosition, boolean songState, String songImageURI, boolean wasRemoteUserRequest) {
        final double initialTime = System.nanoTime();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> songDetails = new HashMap<>();
        songDetails.put("songPlayingStr", uri);
        songDetails.put("songState", songState);
        songDetails.put("songTime", songPosition);
        songDetails.put("songImageURI", songImageURI);
        songDetails.put("requestedUpdate", wasRemoteUserRequest);

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

    private static void subscribeToRemoteUserChanges(final String URI) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userDetails/" + URI);
        playerUpdates = PlayerUpdates.getInstance();

        listener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String prevChildKey) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot parent) {
                        final User updatedUser = parent.getValue(User.class);
                        if (updatedUser != null) {
                            if (!updatedUser.requestedUpdate) {
                                playerUpdates.setPlayBack(updatedUser);
                            } else if (playerUpdates.firstCall) {
                                playerUpdates.setPlayBack(updatedUser);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void getRemoteUserIfExists(final Context context, final String username) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("userDetails");
        playerUpdates = PlayerUpdates.getInstance();
        userUpdates = new UserUpdates();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("userName").getValue(String.class).toLowerCase().equals(username.toLowerCase())) {

                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userDetails/" + ds.getKey());
                        doesUserExist = true;
                        subscribeToRemoteUserChanges(ds.getKey());

                        userUpdates.dbUserExistsStartActivity(context);//send context to callback to start another activity

                        playerUpdates.connectedToUser = ds.getValue(User.class);

                        Map<String, Object> requestedUpdateBool = new HashMap<>();
                        requestedUpdateBool.put("requestedUpdate", true); //This lets other userapps know that it was a remote request and not a song change.
                        userRef.updateChildren(requestedUpdateBool);
                    } else {
                        doesUserExist = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //This is a listener to the Hosts field, so when it changes this will observe and trigger a force refresh of the local users player
    public static void addRequestObserver(final String uid) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userDetails/" + uid + "/requestedUpdate");
        final PlayerUpdates playerUpdates = PlayerUpdates.getInstance();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    if (dataSnapshot.getValue().toString().equals("true")) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playerUpdates.forceUpdateSongDetails(true);
                            }
                        }, 50);
                    }
                } else{
                    addRequestObserver(uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void clearRemoteUserListener(String URI) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userDetails/" + URI);
            ref.removeEventListener(listener);
    }
}

