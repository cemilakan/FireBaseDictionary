package com.example.firebasedictionary;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;


public class KelimelerAdapter extends RecyclerView.Adapter<KelimelerAdapter.CardTasarimTutucu> {
    private Context mContext;
    private List<Kelimeler> kelimelerListe;
    private FirebaseAuth mAuth;
    FirebaseUser girisYapanKullanici;
    FirebaseFirestore db;
    private StorageReference mStorageRef;

    public KelimelerAdapter(Context mContext, List<Kelimeler> kelimelerListe) {
        this.mContext = mContext;
        this.kelimelerListe = kelimelerListe;
    }

    @Override
    public CardTasarimTutucu onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_view,parent,false);

        return new CardTasarimTutucu(view);
    }

    public void uploadUserCloudFireStore(String UidKelime,String trKelime,String ruKelime){


        String postUuidString=UidKelime;

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

                        Toast.makeText(mContext, "Kayıt Tamamlandı", Toast.LENGTH_SHORT).show();
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
    public void onBindViewHolder(final CardTasarimTutucu holder, int position) {
        final Kelimeler kelime = kelimelerListe.get(position);
        final String UidKelime=kelime.getKelime_uid();

        holder.kelimeTr.setText(kelime.getKelimeTurkce());
        holder.kelimeRu.setText(kelime.getKelimeRusca());

        holder.kelime_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Toast.makeText(mContext,""+UidKelime, LENGTH_SHORT).show();

                db = FirebaseFirestore.getInstance();
                mAuth=FirebaseAuth.getInstance();
                girisYapanKullanici=mAuth.getCurrentUser();
                mStorageRef = FirebaseStorage.getInstance().getReference();

                final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
                LayoutInflater inflater = LayoutInflater.from( mContext );
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                final TextView textView=(TextView)dialogView.findViewById(R.id.textView);

                final EditText etTurkceKelime = (EditText) dialogView.findViewById(R.id.editTextTurkce);
                final EditText etRuscaKelime = (EditText) dialogView.findViewById(R.id.editTextRusca);
                Button buttonSubmit = (Button) dialogView.findViewById(R.id.buttonSubmit);
                Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancel);
                textView.setText("Lütfen Kelimeyi Düzenleyiniz");

                etTurkceKelime.setText(kelime.getKelimeTurkce());

                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadUserCloudFireStore(UidKelime,etTurkceKelime.getText().toString(),etRuscaKelime.getText().toString());
                        dialogBuilder.dismiss();
                    }
                });
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();


            }
        });

        holder.kelime_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return kelimelerListe.size();
    }

    public class CardTasarimTutucu extends RecyclerView.ViewHolder{
        private TextView kelimeTr;
        private TextView kelimeRu;
        private CardView kelime_card;

        public CardTasarimTutucu(View itemView) {
            super(itemView);
            kelimeTr = itemView.findViewById(R.id.kelimeTr);
            kelimeRu = itemView.findViewById(R.id.kelimeRu);
            kelime_card = itemView.findViewById(R.id.kelime_card);
        }
    }
}
