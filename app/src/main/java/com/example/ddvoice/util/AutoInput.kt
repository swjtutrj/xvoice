package com.example.ddvoice.util

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.example.ddvoice.gAccessibilityService
import com.github.stuxuhai.jpinyin.PinyinException
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper

//fun PinyinHelper.toPinyin(string: String) {
//    PinyinHelper.convertToPinyinString(string, "", PinyinFormat.WITHOUT_TONE)
//}

/**
 * Created by Clearlee
 * 2017/12/22.
 */
//object WechatUtils {

var NAME: String? = null
var CONTENT: String? = null


private val TAG: String = "WechatUtils"


fun findTextAndClick2(text: String, bContain:
Boolean = false): Boolean {
    if (!findTextAndClick(text, bContain)) {
        Thread.sleep(1000)
        return findTextAndClick(text, bContain)
    } else {
        return true
    }
}
/**
 * 在当前页面查找文字内容并点击
 *
 * @param text
 */
fun findTextAndClick(text: String, bContain:
Boolean = false): Boolean {
    val accessibilityNodeInfo = gAccessibilityService.rootInActiveWindow ?: return false
    val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
    if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
        for (nodeInfo in nodeInfoList) {
//            Log.i("lyn----------", "text:" + nodeInfo.text)
            if (nodeInfo != null && (text == nodeInfo.text || text == nodeInfo.contentDescription)) {
                performClick(nodeInfo)
                //                    break
                return true
            }
        }
    }
    return false
}


fun findTextPYAndClick(accessibilityService: AccessibilityService, pinyin: String): Boolean {
    
    var info = accessibilityService.rootInActiveWindow
    var found = false
    
    fun traverse(info: AccessibilityNodeInfo) {
        if (info.childCount == 0 && !info.text.isNullOrEmpty()) {
            //            Log.i(TAG, "child widget----------------------------" + info.className)
            //            Log.i(TAG, "showDialog:" + info.canOpenPopup())
            Log.i(TAG, "Text：" + info.text)
            //            Log.i(TAG, "windowId:" + info.windowId)
            if (PinyinHelper.convertToPinyinString(info.text.toString(), "", PinyinFormat.WITHOUT_TONE)
                            .contains(pinyin)) {
                performClick(info.parent.parent.parent)
                found = true
            }
        } else {
            for (i in 0 until info.childCount) {
                if (!found && info.getChild(i) != null) {
                    traverse(info.getChild(i))
                }
            }
        }
    }
    
    traverse(info)
    
    if (!found) {
        Thread.sleep(1000)
        info = accessibilityService.rootInActiveWindow
        traverse(info)
    }
    
    return found
}


fun findTextShortPYAndClick(accessibilityService: AccessibilityService, shortPY: String):
        Boolean {
    var accessibilityNodeInfo = accessibilityService.rootInActiveWindow
    var found = false
    
    fun traverse(info: AccessibilityNodeInfo) {
        if (info.childCount == 0 && !info.text.isNullOrEmpty()) {
            //            Log.i(TAG, "child widget----------------------------" + info.className)
            //            Log.i(TAG, "showDialog:" + info.canOpenPopup())
            Log.i(TAG, "Text：" + info.text)
            //            Log.i(TAG, "windowId:" + info.windowId)
            if (PinyinHelper.getShortPinyin(info.text.toString())
                            .contains(shortPY)) {
                performClick(info.parent.parent.parent)
                found = true
            }
            
        } else {
            for (i in 0 until info.childCount) {
                if (info.getChild(i) != null) {
                    traverse(info.getChild(i))
                }
            }
        }
        //            return false
    }
    
    traverse(accessibilityNodeInfo)
    
    if (!found) {
        Thread.sleep(1000)
        accessibilityNodeInfo = accessibilityService.rootInActiveWindow
        traverse(accessibilityNodeInfo)
    }
    
    return found
}


fun findFocusAndPaste(content: String): Boolean {
    var accessibilityNodeInfo = gAccessibilityService.rootInActiveWindow ?: return false
    var nodeInfo = accessibilityNodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
    Log.i("lyn----------", "nodeInfoList:" + nodeInfo)
    
    if (nodeInfo != null) {
        Log.i("lyn----------", "nodeInfo.text:" + nodeInfo.text)
        val arguments = Bundle()
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            return true
        }
    }
    
    Thread.sleep(1000)
    
    accessibilityNodeInfo = gAccessibilityService.rootInActiveWindow ?: return false
    nodeInfo = accessibilityNodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
    if (nodeInfo != null) {
        Log.i("lyn----------", "nodeInfo.text:" + nodeInfo.text)
        val arguments = Bundle()
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            return true
        }
    }
    
    return false
}

fun findEditableAndPaste(content: String): Boolean {
    Log.d(TAG, "content:" + content)
    var accessibilityNodeInfo = gAccessibilityService.rootInActiveWindow ?: return false
    fun traverse(info: AccessibilityNodeInfo): Boolean {
        if (info.childCount == 0) {
            if (info.isEditable) {
                val arguments = Bundle()
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                }
                return true
            }
        } else {
            for (i in 0 until info.childCount) {
                if (info.getChild(i) != null) {
                    traverse(info.getChild(i))
                }
            }
        }
        return false
    }
    
    if (traverse(accessibilityNodeInfo)) {
        return true
    } else {
        Thread.sleep(1000)
        accessibilityNodeInfo = gAccessibilityService.rootInActiveWindow ?: return false
        return traverse(accessibilityNodeInfo)
    }
}


fun findShortPYAndClick(accessibilityService: AccessibilityService, text: String) {
    
    val accessibilityNodeInfo = accessibilityService.rootInActiveWindow ?: return
    
    accessibilityNodeInfo
    
    val nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
    if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
        for (nodeInfo in nodeInfoList) {
            Log.i("lyn----------", "nodeInfo.text:" + nodeInfo.text)
            try {
                if (nodeInfo != null && text == PinyinHelper.getShortPinyin(nodeInfo.text.toString())) {
                    performClick(nodeInfo)
                    break
                }
            } catch (e: PinyinException) {
                e.printStackTrace()
            }
            
        }
    }
}


/**
 * 检查viewId进行点击
 *
 * @param accessibilityService
 * @param id
 */
fun findViewIdAndClick(accessibilityService: AccessibilityService, id: String) {
    
    val accessibilityNodeInfo = accessibilityService.rootInActiveWindow ?: return
    
    var nodeInfoList: List<AccessibilityNodeInfo>? = null
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
        nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id)
    }
    if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
        for (nodeInfo in nodeInfoList) {
            if (nodeInfo != null) {
                performClick(nodeInfo)
                break
            }
        }
    }
}


fun findViewByIdAndPasteContent(accessibilityService: AccessibilityService, id: String, content: String): Boolean {
    val rootNode = accessibilityService.rootInActiveWindow
    if (rootNode != null) {
        var editInfo: List<AccessibilityNodeInfo>? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            editInfo = rootNode.findAccessibilityNodeInfosByViewId(id)
        }
        Log.i("lyn----------", "editinfo:" + editInfo)
        if (editInfo != null && !editInfo.isEmpty()) {
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
            editInfo[0].performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            return true
        }
        return false
    }
    return false
}

fun findTextById(accessibilityService: AccessibilityService, id: String): String? {
    val rootInfo = accessibilityService.rootInActiveWindow
    if (rootInfo != null) {
        var userNames: List<AccessibilityNodeInfo>? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            userNames = rootInfo.findAccessibilityNodeInfosByViewId(id)
        }
        if (userNames != null && userNames.size > 0) {
            return userNames[0].text.toString()
        }
    }
    return null
}


/**
 * 在当前页面查找对话框文字内容并点击
 *
 * @param text1 默认点击text1
 * @param text2
 */
fun findDialogAndClick(accessibilityService: AccessibilityService, text1: String, text2: String) {
    
    val accessibilityNodeInfo = accessibilityService.rootInActiveWindow ?: return
    
    val dialogWait = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text1)
    val dialogConfirm = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text2)
    if (!dialogWait.isEmpty() && !dialogConfirm.isEmpty()) {
        for (nodeInfo in dialogWait) {
            if (nodeInfo != null && text1 == nodeInfo.text) {
                performClick(nodeInfo)
                break
            }
        }
    }
    
}

//模拟点击事件
fun performClick(nodeInfo: AccessibilityNodeInfo?) {
    if (nodeInfo == null) {
        return
    }
    if (nodeInfo.isClickable) {
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    } else {
        performClick(nodeInfo.parent)
    }
}

//模拟返回事件
fun performBack(service: AccessibilityService?) {
    if (service == null) {
        return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        try {
            Thread.sleep(200)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
}
//}
