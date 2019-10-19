
# CacheSmith

SQLite ORM solution and database's management for Android.

## Version

v1.0.0-RC

## Getting started

### Prerequisites

CacheSmith requires at minimum Android API 19+.

The library uses kotlin and it needs some kotlin's dependencies to work.

```
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.41"
implementation "org.jetbrains.kotlin:kotlin-reflect:1.3.41"
```

### Installing

From Maven central:

Add the repository url in your project like this:

```
allprojects {
    repositories {
        //...
        maven {
            url 'https://dl.bintray.com/phmdacosta/cachesmith'
        }
        //...
    }
}
```

In the app module, add the following dependency to implement de library:

```
implementation 'com.cachesmith:cachesmith:1.0.0-RC'
```

Other way:

Download AAR file of the last [release](https://github.com/phmdacosta/cachesmith/releases).
Import this file to libs folder in your Android App module.

Add implamentation of aar files to your app, in the module's gradle, like below:

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.aar'])
}
```

Or from Maven central.

## How to use

Read the [FAQ](https://github.com/phmdacosta/cachesmith/blob/master/FAQ.md) to find details how to use the library.

## Authors

**Pedro da Costa** - *Initial work*

See also the list of [contributors](https://github.com/phmdacosta/cachesmith/graphs/contributors) who participated in this project.

## License

CacheSmith is licensed under [MIT License](https://github.com/phmdacosta/cachesmith/blob/master/LICENSE)

## Acknowledgments

* It uses SQLite library from Android API to access database;
* Project was inspirated in JPA annotations for ORM;
* It was developed using singleton partner. 
