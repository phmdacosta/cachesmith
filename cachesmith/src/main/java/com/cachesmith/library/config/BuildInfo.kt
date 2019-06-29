package com.cachesmith.library.config

import android.content.pm.PackageInfo
import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException

object BuildInfo {
	
	private var info: PackageInfo? = null
	
	fun initInfo(context: Context) {
		try {
		    info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (e: NameNotFoundException) {
		    e.printStackTrace();
		}
	}
	
	fun getVersionCode(context: Context): Int {
		if (info == null) {
			initInfo(context)
		}
		
		return info!!.versionCode
	}
}