package com.cachesmith.library.java;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.cachesmith.library.CacheSmith;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LibraryInstanceJavaTest {

    Context appContext;
    CacheSmith cacheSmith;

    @Before
    public void prepareTest() {
        appContext = InstrumentationRegistry.getTargetContext();
        cacheSmith = CacheSmith.Builder.build(appContext);
    }

    @Test
    public void testLibraryBuild() {
        Assert.assertNotNull(cacheSmith);
    }

    @Test
    public void testDatabaseDefaultNameDefined() {
        Assert.assertNotNull(cacheSmith.getDatabaseName());
    }
}
