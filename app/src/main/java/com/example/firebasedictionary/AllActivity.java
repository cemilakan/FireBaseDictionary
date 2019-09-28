package com.example.firebasedictionary;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    AlertDialog.Builder alert;
    String girisyapanKullaniciUid, kullaniciMail;
    private RecyclerView rv;
    private ArrayList<Kelimeler> kelimelerListe;
    private KelimelerAdapter adapter;
    String firestoreSiralama = "date";
    String firestoreSiralamaTuru = "DESCENDING";
    CircleImageView profimage;
    TextView profAdSoyad, profEmail;
    private FirebaseAuth mAuth;
    FirebaseUser girisYapanKullanici;
    FirebaseFirestore db;
    private StorageReference mStorageRef;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.all);
        setSupportActionBar(toolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //firestore


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        girisYapanKullanici = mAuth.getCurrentUser();
        girisyapanKullaniciUid = girisYapanKullanici.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        kullaniciMail = girisYapanKullanici.getEmail();


        //firestore


        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        kelimelerListe = new ArrayList<>();
        adapter = new KelimelerAdapter(this, kelimelerListe);


        rv.setAdapter(adapter);
        tumKelimeler(firestoreSiralama, firestoreSiralamaTuru);


        alert = new AlertDialog.Builder(AllActivity.this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertViewShowUp();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        profimage = hView.findViewById(R.id.profile_image_kaydir);
        profAdSoyad = hView.findViewById(R.id.profAdSoyad);
        profEmail = hView.findViewById(R.id.profEmail);
        Picasso.get().load(girisYapanKullanici.getPhotoUrl().toString()).into(profimage);
        profAdSoyad.setText(girisYapanKullanici.getDisplayName());
        profEmail.setText(girisYapanKullanici.getEmail());

    }

    private void signOut() {

        mAuth.signOut();


        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(AllActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }


    public void tumKelimeler(String siralama, String siralamaturu) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("words");

        collectionReference
                .orderBy(siralama, Query.Direction.valueOf(siralamaturu))
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        kelimelerListe.clear();


                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                            Kelimeler kelime = new Kelimeler();
                            kelime.setKelime_uid(snapshot.getId());
                            kelime.setKelimeTurkce("" + snapshot.get("turkcekelime"));
                            kelime.setKelimeRusca("" + snapshot.get("ruscakelime"));
                            kelime.setUserUid("" + snapshot.get("useruid"));
                            if (snapshot.get("useruid").equals(girisYapanKullanici.getUid())) {
                                kelimelerListe.add(kelime);
                            }


                        }
                        adapter.notifyDataSetChanged();

                    }
                });



    }


    public void alertViewShowUp() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        final TextView textView = dialogView.findViewById(R.id.textView);

        final EditText etTurkceKelime = dialogView.findViewById(R.id.editTextTurkce);
        final EditText etRuscaKelime = dialogView.findViewById(R.id.editTextRusca);
        Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        textView.setText("Lütfen Yeni Kelime Giriniz");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadUserCloudFireStore(etTurkceKelime.getText().toString(), etRuscaKelime.getText().toString());
                dialogBuilder.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    //firestore upload
    public void uploadUserCloudFireStore(String trKelime, String ruKelime) {


        UUID postuuid = UUID.randomUUID();
        String postUuidString = postuuid.toString();

        HashMap<String, Object> userAccountInfoToCloudStore = new HashMap<>();
        userAccountInfoToCloudStore.put("useruid", girisYapanKullanici.getUid());
        userAccountInfoToCloudStore.put("email", girisYapanKullanici.getEmail());
        userAccountInfoToCloudStore.put("isim", girisYapanKullanici.getDisplayName());
        userAccountInfoToCloudStore.put("turkcekelime", trKelime);
        userAccountInfoToCloudStore.put("ruscakelime", ruKelime);
        userAccountInfoToCloudStore.put("kullanimsikligi", 0);
        userAccountInfoToCloudStore.put("date", FieldValue.serverTimestamp());


        FirebaseFirestore.getInstance().collection("words").document(postUuidString)

                .set(userAccountInfoToCloudStore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(AllActivity.this, "Kayıt Tamamlandı", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("UserAdd", "kullanıcı eklenemedi", e);
                    }
                });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_searchBtn).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("Ara");
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.siralaTarihEY) {

            tumKelimeler("date", "ASCENDING");

        } else if (id == R.id.siralaTarihYE) {

            tumKelimeler("date", "DESCENDING");

        } else if (id == R.id.siralaAlfabeAZ) {

            tumKelimeler("turkcekelime", "ASCENDING");

        } else if (id == R.id.siralaAlfabeZA) {
            tumKelimeler("turkcekelime", "DESCENDING");

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {



        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_edit_profile) {

            Intent intent = new Intent(getApplicationContext(), SignUp.class);
            startActivity(intent);

        } else if (id == R.id.nav_signOut) {

            signOut();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String search) {
        searchAction(search);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searc) {
        searchAction(searc);
        return false;
    }

    public void searchAction(final String searchString){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("words");
        collectionReference
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        kelimelerListe.clear();


                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                            Kelimeler kelime = new Kelimeler();
                            kelime.setKelime_uid(snapshot.getId());
                            kelime.setKelimeTurkce("" + snapshot.get("turkcekelime"));
                            kelime.setKelimeRusca("" + snapshot.get("ruscakelime"));
                            kelime.setUserUid("" + snapshot.get("useruid"));
                            if (kelime.getKelimeTurkce().contains(searchString)) {
                                kelimelerListe.add(kelime);
                            }

                        }
                        adapter.notifyDataSetChanged();

                    }
                });


    }

}
