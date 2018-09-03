package example.com.asknsit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 1;

    public static final String ANONYMOUS = "anonymous";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseStorage firebaseStorage;
    private StorageReference firebaseStorageReference;
    private FirebaseRemoteConfig remoteConfig;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("messages");
        firebaseAuth = FirebaseAuth.getInstance();

        mUsername = ANONYMOUS;


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    SignInFailure();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                } else {
                    afterSignIn(firebaseUser.getDisplayName());
                    Toast.makeText(MainActivity.this, "Welcome " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();

                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Sign in Success", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
//        mMessageAdapter.clear();
//        if(childEventListener!=null){
//            databaseReference.removeEventListener(childEventListener);
//            childEventListener = null;
//        }
    }

    private void afterSignIn(String Username){
        mUsername = Username;

//        if(childEventListener==null) {
//            childEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
//                    mMessageAdapter.add(friendlyMessage);
//                }
//
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                }
//
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                }
//
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                }
//
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            };
//            databaseReference.addChildEventListener(childEventListener);
//        }
    }

    private void SignInFailure(){
        mUsername = ANONYMOUS;
//        mMessageAdapter.clear();
//        if(childEventListener!=null){
//            databaseReference.removeEventListener(childEventListener);
//            childEventListener = null;
//        }



    }


}