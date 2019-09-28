package com.example.firebasedictionary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    Animation frombottom,fromtop;
    Button btnjoin;
    EditText emailText,complateNameText,userPasswordText,locationText;
    TextView signUpText;
    private FirebaseAuth mAuth;
    FirebaseUser girisYapanKullanici;
    String girisyapanKullaniciUid;
    ImageView profilImage;
    Uri selectedImage;
    FirebaseFirestore db;
    private StorageReference mStorageRef;
    CircleImageView profilCircleImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loadAllAnimation();
        findViewAllStuff();
        startAllAnimation();

        db = FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        girisYapanKullanici=mAuth.getCurrentUser();
        emailText.setText(girisYapanKullanici.getEmail());
        complateNameText.setText(girisYapanKullanici.getDisplayName());
        girisyapanKullaniciUid=girisYapanKullanici.getUid();
        //Toast.makeText(this, girisyapanKullaniciUid, Toast.LENGTH_SHORT).show();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        girisYapanKullanici=mAuth.getCurrentUser();



        Picasso.get().load(girisYapanKullanici.getPhotoUrl().toString()).into(profilImage);




    }

    public void uploadUserCloudFireStore(View view){


                        HashMap<String, Object> userAccountInfoToCloudStore = new HashMap<>();
                        userAccountInfoToCloudStore.put("useruid", girisyapanKullaniciUid);
                        userAccountInfoToCloudStore.put("email", emailText.getText().toString());
                        userAccountInfoToCloudStore.put("complatename", complateNameText.getText().toString());
                        userAccountInfoToCloudStore.put("password", userPasswordText.getText().toString());
                        userAccountInfoToCloudStore.put("profilimageurl", girisYapanKullanici.getPhotoUrl().toString());
                        userAccountInfoToCloudStore.put("date", FieldValue.serverTimestamp());


                        FirebaseFirestore.getInstance().collection("users").document(girisyapanKullaniciUid)

                                .set(userAccountInfoToCloudStore)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("UserAdd", "kayıt başarılı " );
                                        Intent intent=new Intent(SignUp.this,AllActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("UserAdd", "kullanıcı eklenemedi", e);
                                    }
                                });


                    }










/*
//eğer resim seçtirelecek ise
    public void uploadUserCloudFireStore(View view){
        UUID uuıd=UUID.randomUUID();
        final String imageName="profil_images/"+uuıd+".jpg";
        //Toast.makeText(this, imageName, Toast.LENGTH_SHORT).show();
        StorageReference storageReference=mStorageRef.child(imageName);


        storageReference.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //download url
                StorageReference newReference= FirebaseStorage.getInstance().getReference(imageName);
                newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageProfilUrl=uri.toString();
                        //Toast.makeText(SignUp.this, imageProfilUrl, Toast.LENGTH_SHORT).show();
                        FirebaseUser user=mAuth.getCurrentUser();
                        String socialFirstName = user.getDisplayName();
                        Toast.makeText(SignUp.this, "sdad"+socialFirstName, Toast.LENGTH_SHORT).show();
                        String userEmail=user.getEmail();
                        UUID postuuid=UUID.randomUUID();
                        String postUuidString=postuuid.toString();

                        HashMap<String, Object> userAccountInfoToCloudStore = new HashMap<>();
                        userAccountInfoToCloudStore.put("useruid", girisyapanKullaniciUid);
                        userAccountInfoToCloudStore.put("email", emailText.getText().toString());
                        userAccountInfoToCloudStore.put("complatename", complateNameText.getText().toString());
                        userAccountInfoToCloudStore.put("password", userPasswordText.getText().toString());
                        userAccountInfoToCloudStore.put("profilimageurl", imageProfilUrl);
                        userAccountInfoToCloudStore.put("date", FieldValue.serverTimestamp());


                        FirebaseFirestore.getInstance().collection("users").document(girisyapanKullaniciUid)

                                .set(userAccountInfoToCloudStore)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("UserAdd", "kayıt başarılı " );
                                        Intent intent=new Intent(SignUp.this,AllActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("UserAdd", "kullanıcı eklenemedi", e);
                                    }
                                });


                        //döküman adını kendim seçmezsem bu kod bloğu aktif edilecek

//                        FirebaseFirestore.getInstance().collection("users")
//
//                                .add(userAccountInfoToCloudStore)
//                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                    @Override
//                                    public void onSuccess(DocumentReference documentReference) {
//                                        Log.d("UserAdd", "kayıt başarılı " + documentReference.getId());
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.w("UserAdd", "kullanıcı eklenemedi", e);
//                                    }
//                                });



                    }
                });
                //Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(intent);



            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(SignUp.this, "", Toast.LENGTH_SHORT).show();
                Log.w("UserAdd", "Resim yüklenemedi", e);
            }
        });

    }
    */

    public void loadAllAnimation(){
        //İlk bunlar on create kısmına konu veya başlatılır
        frombottom= AnimationUtils.loadAnimation(this,R.anim.frombutton);
        fromtop= AnimationUtils.loadAnimation(this,R.anim.fromtop);
    }

    public void findViewAllStuff(){
        //ikinci olarak on create koy
        signUpText=findViewById(R.id.signUpText);
        profilCircleImage=findViewById(R.id.profilImage);
        emailText=findViewById(R.id.emailText);
        complateNameText=findViewById(R.id.complateNameText);
        userPasswordText=findViewById(R.id.userPasswordText);
        locationText=findViewById(R.id.locationText);
        btnjoin=findViewById(R.id.btnjoin);
        profilImage=findViewById(R.id.profilImage);
    }

    public void startAllAnimation(){
        btnjoin.startAnimation(frombottom);

        signUpText.startAnimation(fromtop);
        profilCircleImage.startAnimation(fromtop);

        emailText.startAnimation(fromtop);
        complateNameText.startAnimation(fromtop);
        userPasswordText.startAnimation(fromtop);
        locationText.startAnimation(fromtop);
    }


    public void selectImage(View view){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }else {
            Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==1){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }




        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==2 && resultCode==RESULT_OK && data!=null){

            selectedImage=data.getData();
            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                profilImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
