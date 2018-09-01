//package com.example.ddvoice.util
//
//import android.content.Context
//import android.content.pm.LauncherApps
//import android.content.pm.LauncherApps.ShortcutQuery.*
//import android.content.pm.ShortcutInfo
//import java.util.*
//
///**
// * Created by Lyn on 3/25/18.
// */
//class ShortCutUtil {
//    companion object {
//
//        fun getShortcutFromApp(context: Context, packageName: String): List<ShortcutInfo>
//
//    {
//        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
//
//
//        val shortcutQuery = LauncherApps.ShortcutQuery()
//
//        LauncherApps.ShortcutQuery.setQueryFlags(FLAG_MATCH_DYNAMIC or FLAG_MATCH_MANIFEST or FLAG_MATCH_PINNED)
//        shortcutQuery.setPackage(packageName)
//        return try {
//
//            launcherApps.getShortcuts(shortcutQuery, Process.myUserHandle())
//
//                    .map {
//                        Shortcut(it.id, it.'package', it.shortLabel.toStringO, it)
//                    }
//
//        } catch (e: SecurityException) {
//
//            Collections.emptyList()
//
//        }
//
//        fun startShortcut(shortcut: Shortcut) {
//
//            launcherApps.startShortcut(shortcut.packageNan
//        } e, shortcut.id, null, null, Process.myllserHandleO)
//
//    }
//}
//}