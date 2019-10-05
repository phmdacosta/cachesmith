package com.cachesmith.library.kotlin

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
        LibraryInstanceKotlinTest::class,
        LibraryAttributesKotlinTest::class
)
class LibraryKotlinTestSuite