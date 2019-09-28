package com.example.firebasedictionary;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;



public class MainActivity extends AppCompatActivity implements
        View.OnClickListener  {

    ImageView bgone;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    Intent intentSignUpActivity;
    FirebaseUser girisyapanKullanici;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yenilogin_denemesi);


        bgone=findViewById(R.id.imageView);

        bgone.animate().scaleX(1.2F).scaleY(1.2F).setDuration(7000).start();

        // Button listeners
        findViewById(R.id.btnSignGoogle).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.calisBtn).setOnClickListener(this);
        findViewById(R.id.btnKayitOl).setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseApp.initializeApp(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();

        girisyapanKullanici = mAuth.getCurrentUser();
        if (girisyapanKullanici!=null){
            Intent intent=new Intent(getApplicationContext(),AllActivity.class);
            startActivity(intent);
        }

        intentSignUpActivity=new Intent(MainActivity.this,SignUp.class);


    }




    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        try {
           // Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){

        }


        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {

                Log.w(TAG, "xdxGoogle sign in failed", e);

                updateUI(null);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent=new Intent(MainActivity.this,AllActivity.class);
                            startActivity(intent);

                        } else {

                            Log.w(TAG, "xdxsignInWithCredential:failure", task.getException());
                            updateUI(null);

                        }

                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




    private void signOut() {

        mAuth.signOut();


        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }



    @SuppressLint("StringFormatInvalid")
    private void updateUI(FirebaseUser user) {
        if (user != null) {


            findViewById(R.id.btnSignGoogle).setVisibility(View.GONE);
            findViewById(R.id.afterLoginAnyUser).setVisibility(View.VISIBLE);

        } else {

            findViewById(R.id.btnSignGoogle).setVisibility(View.VISIBLE);
            findViewById(R.id.afterLoginAnyUser).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.btnSignGoogle) {

            signIn();

        } else if (i == R.id.signOutButton) {

            signOut();

        } else if (i == R.id.calisBtn) {

            Intent inteb=new Intent(MainActivity.this,AllActivity.class);
            startActivity(inteb);

        }else if (i == R.id.btnKayitOl){

            if (girisyapanKullanici!=null){

                Intent intent=new Intent(getApplicationContext(),AllActivity.class);
                startActivity(intent);

            }else {
                Toast.makeText(getApplicationContext(),"Önce Oturum Açmalısınız.",Toast.LENGTH_SHORT).show();
            }


        }
    }


}
