# How to use

## Contents

* [About library](#about-library)
* [Initiate](#initiate)
* [Mapping models](#mapping-models)
* [Creating your DataSource](#creating-your-datasource)
* [QueryBuilders](#querybuilders)


## About library

CacheSmith is a ORM solution to make easier developing SQLite in Android apps. It was also designed to be simple to use, with a one line of code you can create the database, the table and get access to it.
But it also gives you a alternative to have more control of your database.

## Initiate

To use the library, just call it's creator and loads your data source like below:

Java:
```
MyDataSource myDataSource = CacheSmith.Builder.build(context).load(MyDataSource.class);
```

Kotlin:
```
val myDataSource = CacheSmith.build(context).load(MyDataSource::class)
```

With this your data source will be ready with database created and upgraded to freely manage the data.

If you are the kind of person that likes to take control of everything, don't worry, CacheSmith gives you that possibility.
Every time you call *load* method, it will check if there are any changes in your model class, or if it's table exists, to update or create it.
Every time the library finds a change, it will update database's version.
If you don't want to leave it the way it is, you can use methods/functions like *setManualVersion*, *setVersion* and *setDatabaseName* to take full control of database's life cicle.

Put it in your Application class:

Java:

```
CacheSmith cacheSmith = CacheSmith.Builder.build(context);
cacheSmith.setManualVersion(true); // This will tell library that database's version will be set manually
cacheSmith.setVersion(1); // Set your current version
cacheSmith.setDatabaseName("my_db_name"); // Set your database name
cacheSmith.addModel(MyModel.class); // Add your mapped model to a list of models
// You can also add an entire list of models with addAllModels method
cacheSmith.initDatabase(); // Initiate your database's creation
```

Kotlin:

```
val cacheSmith = CacheSmith.build(context)
cacheSmith.setManualVersion(true) // This will tell library that database's version will be set manually
cacheSmith.setVersion(1) // Set your current version
cacheSmith.setDatabaseName("my_db_name") // Set your database name
cacheSmith.addModel(MyModel::class) // Add your mapped model to a models list
cacheSmith.initDatabase() // Initiate your database's creation
```

## Mapping models

Library gives to developers a mapping type based on annotations, with that they can map their models while they create it.
Here there are two examples of a model created using CacheSmith annotations in Java and Kotlin:

Java:

```
public class Test {

    @PrimaryKey
    @AutoIncrement
    @Unique
    private int pk;

    @Column(name="name")
    private String nameTest;

    @Column(type = DataType.BLOB)
    private Object obj;

    // getters and setters
}
```

Kotlin:

```
@Table("table_test")
class Test(@Column("test_name") val name: String) {

    @PrimaryKey
    @AutoIncrement
    @Unique
    var id: Int = 0

    var active: Boolean = false
}
```

## Creating your DataSource

To create the data source class, by default, developers need to follow a requirement: to extend the DataSource class from library.
Extending DataSource class, library can instantiate your class automatic and give you the database object prepared to the use.
Developers should also set @Entity annotation in the class.
CacheSmith uses this annotation to get model class automatic and create or update model's table.
If you have full control of database setting version manually and the list of models, this is optional.
See code below to define Model annotation in datasource class:

Java:

```
@Entity("com.cachesmith.example.models.TestJava")
public class TestJavaDataSource extends DataSource {
    // ...
}
```

Kotlin:

```
@Entity("com.cachesmith.example.models.Test")
class TestDataSource(dbHelper: SQLiteOpenHelper) : DataSource(dbHelper) {
    // ...
}
```

## QueryBuilders

CacheSmith uses a self solution to abstract creation of queries and this component is also available to all who incorporate this library in their projects.
QueryBuilders were created to build simple query for common situations. Instead of writing a whole query, you can use QueryBuilder to build that for you.
It can be extended to be modified based on developer's need.
