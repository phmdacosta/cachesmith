package com.cachesmith.library.util.db.models

import com.cachesmith.library.util.ActionType

class ForeignKeyObject @JvmOverloads constructor(var referenceTable: String = "", var referenceColumn: String = "",  var referenceColumnType: String = "") {
	
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