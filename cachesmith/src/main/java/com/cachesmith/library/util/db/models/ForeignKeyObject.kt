package com.cachesmith.library.util.db.models

import com.cachesmith.library.util.ActionType

class ForeignKeyObject() {
	
	constructor(referenceTable: String, referenceColumn: String) : this() {
		this.referenceTable = referenceTable
		this.referenceColumn = referenceColumn
	}
	
	var referenceTable: String = ""
	var referenceColumn: String = ""
	var deferred: Boolean = false
	var onDeleteAction: ActionType = ActionType.NO_ACTION
	var onUpdateAction: ActionType = ActionType.NO_ACTION
	
	val isEmpty: Boolean
		get() {
			if ("".equals(referenceTable) || "".equals(referenceColumn))
				return true
			return false
		}
}