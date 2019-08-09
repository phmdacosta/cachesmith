package com.cachesmith.library.exceptions

import java.lang.Exception

class NoVersionException(message: String, cause: Throwable?): Exception(message, cause) {
    constructor(message: String) : this(message, null)
	constructor() : this("Could not get version for database.")
}

class SQLiteQueryException(message: String, cause: Throwable?): Exception(message, cause) {
    constructor(message: String) : this(message, null)
}