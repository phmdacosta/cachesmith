package com.cachesmith.library.config

import android.content.pm.PackageInfo
import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException

object BuildInfo {
	
	private var info: PackageInfo? = null
	
	@Throws(NameNotFoundException::class)
	fun initInfo(context: Context) {
		try {
		    info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (e: NameNotFoundException) {
			throw NameNotFoundException("Could not retrieve info from App in package ".plus(context.getPackageName()))
		}
	}
	
	@Throws(NameNotFoundException::class)
	fun getVersionCode(context: Context): Int {
		if (info == null) {
			initInfo(context)
		}
		
		return info!!.versionCode
	}
}