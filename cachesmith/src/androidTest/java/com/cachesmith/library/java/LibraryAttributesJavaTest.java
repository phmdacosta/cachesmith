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
public class LibraryAttributesJavaTest {

    Context appContext;
    CacheSmith cacheSmith;

    @Before
    public void prepareTest() {
        appContext = InstrumentationRegistry.getTargetContext();
        cacheSmith = CacheSmith.Builder.build(appContext);
        cacheSmith.setDatabaseName("Database_Test_Name");
        cacheSmith.setManualVersion(true);
        cacheSmith.setVersion(23);
    }

    @Test
    public void testAttributesFromInstance() {
        Assert.assertEquals("Database_Test_Name", cacheSmith.getDatabaseName());
        Assert.assertTrue(cacheSmith.isManualVersionDefined());
        Assert.assertEquals(23, cacheSmith.getVersion());
    }

    @Test
    public void testAttributesFromContext() {
        CacheSmith localInstance = CacheSmith.Builder.build(appContext);
        Assert.assertEquals("Database_Test_Name", localInstance.getDatabaseName());
        Assert.assertTrue(localInstance.isManualVersionDefined());
        Assert.assertEquals(23, localInstance.getVersion());
    }
}
