package com.cachesmith.example.java;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.cachesmith.example.java.models.Address;
import com.cachesmith.example.java.models.Teste;
import com.cachesmith.example.java.models.User;
import com.cachesmith.library.CacheSmith;

public class TesteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CacheSmith cacheSmith = CacheSmith.create(this);
        cacheSmith.addModel(Teste.class);
        cacheSmith.addModel(User.class);
        cacheSmith.addModel(Address.class);
        cacheSmith.initDatabase();
    }
}
