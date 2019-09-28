package com.example.firebasedictionary;

import java.io.Serializable;


public class Kelimeler implements Serializable {
    private String kelime_uid;
    private String kelimeRusca;
    private String kelimeTurkce;
    private String userUid;

    public Kelimeler() {
    }

    public Kelimeler(String kelime_uid, String kelimeRusca, String kelimeTurkce, String userUid) {
        this.kelime_uid = kelime_uid;
        this.kelimeRusca = kelimeRusca;
        this.kelimeTurkce = kelimeTurkce;
        this.userUid = userUid;
    }

    public String getKelime_uid() {
        return kelime_uid;
    }

    public void setKelime_uid(String kelime_uid) {
        this.kelime_uid = kelime_uid;
    }

    public String getKelimeRusca() {
        return kelimeRusca;
    }

    public void setKelimeRusca(String kelimeRusca) {
        this.kelimeRusca = kelimeRusca;
    }

    public String getKelimeTurkce() {
        return kelimeTurkce;
    }

    public void setKelimeTurkce(String kelimeTurkce) {
        this.kelimeTurkce = kelimeTurkce;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
