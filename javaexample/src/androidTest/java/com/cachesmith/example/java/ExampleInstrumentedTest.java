package com.cachesmith.example.java;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.cachesmith.example.java.dao.TesteDataSource;
import com.cachesmith.example.java.models.Address;
import com.cachesmith.example.java.models.Teste;
import com.cachesmith.example.java.models.User;
import com.cachesmith.library.CacheSmith;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    Context appContext;

    @Before
    public void initContext() {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testLibrary() {
        // Context of the app under test.

        CacheSmith cacheSmith = CacheSmith.create(appContext);
        cacheSmith.addModel(Teste.class);
        cacheSmith.addModel(User.class);
        cacheSmith.addModel(Address.class);
        cacheSmith.initDatabase();
    }
}
