package com.example.ddvoice

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.telephony.TelephonyManager
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View.inflate
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.baidu.mobstat.StatService
import com.example.ddvoice.action.*
import com.github.dfqin.grantor.PermissionListener
import com.github.dfqin.grantor.PermissionsUtil
import com.github.dfqin.grantor.PermissionsUtil.requestPermission
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import com.iflytek.aiui.AIUIAgent
import com.iflytek.aiui.AIUIConstant
import com.iflytek.aiui.AIUIListener
import com.iflytek.aiui.AIUIMessage
import com.iflytek.sunflower.FlowerCollector
import kotlinx.android.synthetic.main.layout_microphone.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

//wake up word: 关闭手电筒,打开手电筒,小美小美,播放,暂停,拍照,下一首,微信扫码,天气天气

var gAIUIAgent: AIUIAgent? = null
//var gUrlToLoad = ""
//var gPlayer: MediaPlayer? = null //播放音乐
var gBHit = false
var gBAction = false
var gLatePostLog = false
var gStrTts = ""
//var gBBackKeyPressed = false

class MainActivity : Activity(), EventListener {
    
    // AIUI
    private var mAIUIDialog: AlertDialog? = null //RecognizerDialog
    // 用HashMap存储听写结果
    //    private val mIatResults = LinkedHashMap<String, String>()
    // 引擎类型
    //    private val mEngineType = SpeechConstant.TYPE_CLOUD
    private lateinit var mSharedPreferences: SharedPreferences
    //    private lateinit var mSharedPreferencesTTS: SharedPreferences
    
    
    private val TAG: String = "MainActivity"
    
    //    private var mBHeardSth = false
    
    /**
     * 初始化监听器。
     */
    /*private val mInitListener = InitListener { code ->
        Log.d(TAG, "SpeechRecognizer init() code = " + code)
        if (code != ErrorCode.SUCCESS) {
            println("初始化失败，错误码：" + code)
        }
    }*/
    
    
    fun initDialog() { //初始化
        
        //初始化居中显示的按住说话动画
        //        val view = View.inflate(this, R.layout.layout_microphone, null)
        //        VolumeView = view.findViewById<ImageView>(R.id.iv_recording_icon)
        //        mVoicePop = PopupWindowFactory(this, view)
        
        
        val dlgView = inflate(this, R.layout.layout_microphone, null)
        //        VolumeView = dlgView.findViewById(R.id.iv_recording_icon)
        
        dlgView.setting.setOnClickListener {
            startActivity(Intent(this, PreferencesActivity::class.java))
        }
    
        val txt = dlgView.findViewWithTag("textlink") as TextView
        
        mTipsLocked = FlowerCollector.getOnlineParams(applicationContext, "tipsLocked")?.split(",")
        mTips = FlowerCollector.getOnlineParams(applicationContext, "tips")?.split(",")
        val tipsSize = mTips?.size ?: 0
        if (tipsSize > 0) {
            //先随机产生一个下标再获取元素
            val index = (Math.random() * (if (gIsPhoneLocked) mTipsLocked!!.size
            else mTips!!.size))
                    .toInt()
            txt.text = /*if (BuildConfig.DEBUG) "尝试说：打给xx"
            else */("" + if (gIsPhoneLocked) mTipsLocked!![index] else mTips!![index])
            txt.setOnClickListener {
                val index = (Math.random() * (if (gIsPhoneLocked) mTipsLocked!!.size else mTips!!
                        .size)).toInt()
                txt.text = "" + if (gIsPhoneLocked) mTipsLocked!![index] else mTips!![index]
            }
        }
        
        mAIUIDialog = AlertDialog.Builder(this)
                .setView(dlgView)
                .create()
        mAIUIDialog?.setOnDismissListener {
            Log.i("lyn----------" + localClassName, "dialog:" + "dimiss")
            
            //if finish at once, the webviewAct can't display over lock screen
            gAIUIAgent?.sendMessage(AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "", null))
            
            Handler().postDelayed({ finish() }, if (gIsPhoneLocked) 1000L else 0L)
        }
        
        //        setParam()
    }
    
    //    override fun onBackPressed() {
    //        super.onBackPressed()
    //        Log.i("lyn----------" + localClassName, "onBackPressed:")
    ////        mBIsSleeped = false
    //    }
    
    private var mTips: List<String>? = null
    private var mTipsLocked: List<String>? = null
    private var mTipsToast: List<String>? = null
    //    private var mIsHome = false
    
    //    private var mPausedMusic: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.i("lyn-" + getLocalClassName(), "onCreate!")
        
        
        /*if (BuildConfig.DEBUG) {
        
            //            onAsrResult("淘宝搜索待定")
        
            //            CallAction("爸爸", "", this).start()
            //            speak("你好，二货")
            //            CallAction("巴巴", "", this).start()
            //            SRResult = "电话"
            //            onAsrResult()
        
            //            handleAIUIResult("")
        
            handleAIUIResult("{\n" +
                    "\"category\": \"LXY.app\",\n" +
                    "\"intentType\": \"custom\",\n" +
                    "\"rc\": 0,\n" +
                    "\"semanticType\": 0,\n" +
                    "\"service\": \"LXY.app\",\n" +
                    "\"uuid\": \"atn03a0b54f@dx00070ed47856a10e01\",\n" +
                    "\"vendor\": \"LXY\",\n" +
                    "\"version\": \"7.0\",\n" +
                    "-\"semantic\": [\n" +
                    "-{\n" +
                    "\"entrypoint\": \"ent\",\n" +
                    "\"hazard\": false,\n" +
                    "\"intent\": \"launch_app\",\n" +
                    "\"score\": 0.8999999761581421,\n" +
                    "-\"slots\": [\n" +
                    "-{\n" +
                    "\"begin\": 0,\n" +
                    "\"end\": 2,\n" +
                    "\"name\": \"launch\",\n" +
                    "\"normValue\": \"打开\",\n" +
                    "\"value\": \"打开\"\n" +
                    "},\n" +
                    "-{\n" +
                    "\"begin\": 2,\n" +
                    "\"end\": 4,\n" +
                    "\"name\": \"any\",\n" +
                    "\"normValue\": \"小美\",\n" +
                    "\"value\": \"小美\"\n" +
                    "}\n" +
                    "],\n" +
                    "\"template\": \"{launch}{any}\"\n" +
                    "}\n" +
                    "],\n" +
                    "\"state\": null,\n" +
                    "\"sid\": \"atn03a0b54f@dx00070ed47856a10e01\",\n" +
                    "\"text\": \"打开小美\"\n" +
                    "}".replace("-","").replace("\n",""))
            
            return
        }*/
        
        if (intent.action == "show_wake_up_tip") {
            speak("主子，因为不想占用麦克风，而影响需要录音的app，解锁且不在桌面时我就听不到你的召唤了哦。" +
                    "此时可以长按home键或音量键叫我.息屏,锁定以及显示桌面状态都可以叫我“小美小美”召唤我。" +
                    "还可以不叫我直接说“拍照”，以及“打开或关闭手电筒”哦。")
            AlertDialog.Builder(this).setTitle("小美：")
                    .setMessage("主子，因为不想占用麦克风，而影响需要录音的app，解锁且不在桌面时我就听不到你的召唤了哦。\n\n此时可以长按home" +
                            "键或音量键叫我\n" +
                            "\n" +
                            "息屏,锁定以及显示桌面状态都可以叫我“小美小美”召唤我。\n\n还可以不叫我直接说“拍照”，以及“打开/关闭手电筒”哦。")
                    .setPositiveButton("确定", null)
                    .setOnDismissListener { finish() }
                    .show()
            return
        }
        
        gIsMainActActive = true
        
        gFromHeadset = (intent.action == "android.intent.action.VOICE_COMMAND")
        
        val myKM = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        gIsPhoneLocked = myKM.inKeyguardRestrictedInputMode()
        
        
        //        Log.d("lyn-" + getLocalClassName(), "isPhoneLocked:" + gIsPhoneLocked)
        
        /*dialog theme, don't need */
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        //        setShowWhenLocked(true)
        //        setTurnScreenOn(true)
        
        if (/*!BuildConfig.DEBUG && */!checkAccessibilitySetting(this)) {
            if (!gAccessibilityEnabled) {
                speak("尊贵的主人，您好，很高兴为您服务。为了保证服务体验，需要授予小美一些权限哦。首先在接下来的界面中开启小美的辅助功能吧。一定要保证小美常驻后台不会杀死，否则将无法唤醒我，而且下次辅助功能又需要再次开启哦。")
            } else {
                speak("主人，糟糕，小美之前好像确实被暗杀了，辅助功能被自动关闭了呢。为了避免悲剧再次重演，还请您仔细阅读当前显示的说明。")
            }
            AlertDialog.Builder(this).setTitle("辅助服务开启提示，非常重要！")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(Html.fromHtml("请在接下来弹出的界面的<b>\"服务\"</b>下面找到<b>\"小美语音助手\"</b> -> " +
                            "<b>开启</b><br><br>之前<b>已经开启过</b>或者不希望<b>反复开启</b>？<br><br>请看下面的<b" +
                            ">重要提醒！</b><br><br" +
                            ">只要小美的后台进程被杀掉了，辅助服务就会被关闭，就需要重新打开。" +
                            "<br><br>要保证小美<b>不被暗杀</b>，请参考以下教程：<br><br>" +
                            //                        "。<br><br>详细操作方法：" +
                            "<a href='https://zhidao.baidu.com/question/205190961411672205.html'>华为</a>&thinsp;&thinsp;&thinsp;&thinsp;" +
                            "<a href='https://jingyan.baidu.com/article/60ccbceb51de3d64cbb19756" +
                            ".html'>小米/vivo</a>&thinsp;&thinsp;&thinsp;&thinsp;" +
                            "<a href='https://jingyan.baidu.com/article/ff42efa9da45f9c19e2202f2" +
                            ".html'>一加</a>&thinsp;&thinsp;&thinsp;&thinsp;" +
                            "<a href='http://bbs.m.qq.com/thread-71711-1-1.html'>oppo</a>&thinsp;&thinsp;" +
                            "<br><br>为什么必须：长按HOME或音量键唤醒依赖此服务<br><br><br>" +
                            "另外由于功能较多，需要的权限也不少。但作者保证每项权限都必不可少，请您都给予授权，感谢您的信任！"))
                    .setPositiveButton("确定", { _, _ ->
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        finish()
                    })
                    .setCancelable(false)
                    .show()
                    //设置可点击
                    .findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
            return
        } else {
            gAccessibilityEnabled = true
            saveBool(SP_ACCESSIBILITY_ENABLED, gAccessibilityEnabled)
            //request permissions
            if (!requestAllPower()) return
            
            if (gDeviceId.isNullOrEmpty()) {
                val tm = gApplicationContext!!.getSystemService(Context.TELEPHONY_SERVICE) as
                        TelephonyManager
                @SuppressLint("MissingPermission")
                gDeviceId = tm.deviceId
            }
            
            createAgent()
            
            sayWakeAnswer()
            stopWakeUp()
            gTts?.stop()
            gIsSpeaking = false
            
            //            mPausedMusic = false
            //        Handler().postDelayed({  }, 500L)
            initDialog()
            showDialog()
            
            showTipToast()
            
            //xun fei online parameters
            FlowerCollector.updateOnlineConfig(applicationContext) {
                //回调仅在参数有变化时发生
            }
    
            //baidu statistic
            StatService.start(this)
        }
    }
    
    fun showTipToast() {
        mTipsToast = FlowerCollector.getOnlineParams(applicationContext, "tipsToast")?.split(",")
        val tipsSize = mTipsToast?.size ?: 0
        if (tipsSize > 0) {
            val index = (Math.random() * tipsSize).toInt()
            showTipLong(/*if (BuildConfig.DEBUG) "尝试说：天气" else */mTipsToast!![index])
        }
    }
    
    
    override fun onResume() {
        super.onResume()
        
        Log.d("lyn-" + getLocalClassName(), "onResume!")
        
        FlowerCollector.onResume(this)  //讯飞统计
    }
    
    override fun onPause() {
        super.onPause()
        FlowerCollector.onPause(this)    //讯飞统计
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("lyn-" + getLocalClassName(), "onDestroy!")
        
        gIsMainActActive = false
        
        gFromHeadset = false
        
        Handler().postDelayed({
            //wait for nlp result
            if (!gIsMainActActive) destroyAgent()   //if activity created again, not destroy
            if (!gIsSpeaking) abandonAudioFocus()
        }, 2000)
    }
    
    
    private fun getAIUIParams(): String {
        var params = ""
        
        val assetManager = resources.assets
        try {
            val ins = assetManager.open("cfg/aiui_phone.cfg")
            val buffer = ByteArray(ins.available())
            
            ins.read(buffer)
            ins.close()
            
            params = String(buffer)
            
            val paramsJson = JSONObject(params)
            
            params = paramsJson.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        
        return params
    }
    
    private val KEY_SEMANTIC = "semantic"
    private val KEY_OPERATION = "operation"
    private val SLOTS = "slots"
    
    private var mAIUIState = AIUIConstant.STATE_IDLE
    private var mSyncSid: String = ""
    private var parsedSemanticResult: SemanticResult? = null
    
    
    private fun initSemanticResult(str: String) {
        if (parsedSemanticResult != null) return
        // 解析语义结果
        val semanticResult: JSONObject
        parsedSemanticResult = SemanticResult()
        try {
            semanticResult = JSONObject(str)
            parsedSemanticResult!!.rc = semanticResult.optInt("rc")
            parsedSemanticResult!!.text = semanticResult.optString("text")
            if (parsedSemanticResult!!.rc === 4) {
                parsedSemanticResult!!.service = ""
            } else if (parsedSemanticResult!!.rc === 1) {
                parsedSemanticResult!!.service = semanticResult.optString("service")
                parsedSemanticResult!!.answer = "语义错误"
            } else {
                parsedSemanticResult!!.service = semanticResult.optString("service")
                parsedSemanticResult!!.answer =
                        if (semanticResult.optJSONObject("answer") == null) "已为您完成操作"
                        else semanticResult.optJSONObject("answer").optString("text")
                // 兼容3.1和4.0的语义结果，通过判断结果最外层的operation字段
                /*val isAIUI3_0 = semanticResult.has(KEY_OPERATION)
                if (isAIUI3_0) {
                    //将3.1语义格式的语义转换成4.1
                    val semantic = semanticResult.optJSONObject(KEY_SEMANTIC)
                    if (semantic != null) {
                        val slots = semantic.optJSONObject(SLOTS)
                        val fakeSlots = JSONArray()
                        val keys = slots.keys()
                        while (keys.hasNext()) {
                            val item = JSONObject()
                            val name = keys.next()
                            item.put("name", name)
                            item.put("value", slots.get(name))
                            
                            fakeSlots.put(item)
                        }
                        
                        semantic.put(SLOTS, fakeSlots)
                        semantic.put("intent", semanticResult.optString(KEY_OPERATION))
                        parsedSemanticResult!!.semantic = semantic
                    }
                } else*/
                
                parsedSemanticResult!!.semantic = if (semanticResult.optJSONArray(KEY_SEMANTIC) == null)
                    semanticResult.optJSONObject(KEY_SEMANTIC)
                else
                    semanticResult.optJSONArray(KEY_SEMANTIC).optJSONObject(0)
                
                parsedSemanticResult!!.answer = parsedSemanticResult!!.answer //.replaceAll("\\[[a-zA-Z0-9]{2}\\]", "")
                parsedSemanticResult!!.data = semanticResult.optJSONObject("data")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            parsedSemanticResult!!.rc = 4
            parsedSemanticResult!!.service = ""
        }
        
    }
    
    
    //    private var mBIsSleeped: Boolean = false
    //    private lateinit var mAsrTxt: String
    private val mAIUIListener = AIUIListener { event ->
        val EVENT_MAP = mapOf(Pair(1, "EVENT_RESULT"), Pair(2, "EVENT_ERROR"), Pair(3, "EVENT_STATE"), Pair(4, "EVENT_WAKEUP"), Pair
        (5, "EVENT_SLEEP"), Pair(6, "EVENT_VAD"), Pair(8, "EVENT_CMD_RETURN"), Pair(12,
                "EVENT_STOP_RECORD"), Pair(11,
                "EVENT_START_RECORD"), Pair(13, "EVENT_CONNECTED_TO_SERVER"))
        
        //        Log.i(TAG, "on event origin: " + event.eventType)
        Log.i(TAG, "lyn-----on event: " + EVENT_MAP.get(event.eventType) + "(${event.eventType})")
        
        //        if (BuildConfig.DEBUG) {
        //            mAIUIDialog!!
        //        }
        
        when (event.eventType) {
            AIUIConstant.EVENT_CONNECTED_TO_SERVER -> {
                println("已连接服务器")
                //                if (Looper.myLooper() == Looper.getMainLooper()) {
                //                    // Current thread is the UI/Main thread
                //                    Log.i("lyn----------" + localClassName, "Current thread is the UI/Main thread:")
                //                }
                
                if (!gContactSyncOK) {
                    thread {
                        //                        Thread.sleep(2000)
                        Log.i("lyn----------" + localClassName, "syncContacts")
                        try {
                            syncContacts(applicationContext)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            
            AIUIConstant.EVENT_SERVER_DISCONNECTED -> println("与服务器断连")
            
            //            AIUIConstant.EVENT_WAKEUP -> showTip("进入识别状态")
            
            AIUIConstant.EVENT_RESULT -> {
                
                try {
                    val bizParamJson = JSONObject(event.info)
                    val data = bizParamJson.getJSONArray("data").getJSONObject(0)
                    val params = data.getJSONObject("params")
                    val content = data.getJSONArray("content").getJSONObject(0)
                    
                    if (content.has("cnt_id")) {
                        val cnt_id = content.getString("cnt_id")
                        val cntStr = String(event.data.getByteArray(cnt_id)!!, Charsets.UTF_8)
                        
                        // 获取该路会话的id，将其提供给支持人员，有助于问题排查
                        // 也可以从Json结果中看到
                        val sid = event.data.getString("sid")
                        val tag = event.data.getString("tag")
                        
                        //                        showTip("tag=" + tag!!)
                        
                        // 获取从数据发送完到获取结果的耗时，单位：ms
                        // 也可以通过键名"bos_rslt"获取从开始发送数据到获取结果的耗时
                        //                        val eosRsltTime = event.data.getLong("eos_rslt", -1)
                        //                        mTimeSpentText.setText(eosRsltTime.toString() + "ms")
                        
                        if (TextUtils.isEmpty(cntStr)) {
                            return@AIUIListener
                        }
                        
                        val cntJson = JSONObject(cntStr)
                        
                        //                        if (mNlpText.getLineCount() > 1000) {
                        //                            mNlpText.setText("")
                        //                        }
                        //
                        //                        mNlpText.append("\n")
                        //                        mNlpText.append(cntJson.toString())
                        //                        mNlpText.setSelection(mNlpText.getText().length)
                        
                        val sub = params.optString("sub")
                        if ("nlp" == sub) {
                            // 解析得到语义结果
                            val resultStr: String = cntJson.optString("intent")
                            Log.i(TAG, "语义结果:$resultStr")
                            //                            showTip("语义结果:$resultStr")
                            if (!resultStr.isNullOrEmpty()) {
                                handleAIUIResult(resultStr)
                            } /*else mIatDialog.dismiss()*/
                            
                            //                            mAIUIDialog?.dismiss()
                        } //else mIatDialog.dismiss()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    //                    mNlpText.append("\n")
                    //                    mNlpText.append(e.localizedMessage)
                }
                
                //                mNlpText.append("\n")
            }
            
            AIUIConstant.EVENT_ERROR -> {
                //                mNlpText.append("\n")
                //                mNlpText.append("错误: " + event.arg1 + "\n" + event.info)
                
                println("lyn-------------EVENT_ERROR: " + event.arg1 + "\n" + event.info)
                //                showTip("错误: " + event.arg1 + "\n" + event.info)
                if (event.arg1 == 20006) {
                    speak("糟糕，麦克风被占用")
                } else {
                    speak("脑子有点短路")
                }
                //                if (BuildConfig.DEBUG) {
                //                   mAIUIDialog!!
                //                }
                mAIUIDialog?.dismiss()
            }
            
            AIUIConstant.EVENT_SLEEP -> {
                //                speak("没听清")
                //                mAIUIDialog?.dismiss()
                //                mBIsSleeped = true
            }
            
            AIUIConstant.EVENT_VAD -> {
                if (AIUIConstant.VAD_BOS == event.arg1) {
                    println("lyn--------找到vad_bos")
                } else if (AIUIConstant.VAD_EOS == event.arg1) {
                    println("lyn--------找到vad_eos")
                } else {
                    //                    println("" + event.arg2)
                }
                
                //                val level = 5000 + 8000 * event!!.arg2 / 100
                
                //                Log.d("lyn-" + localClassName, "vad_vol:" + level)
                
                //更新居中的音量信息
                //                if (VolumeView != null && VolumeView!!.getDrawable().setLevel(level)) {
                //                    VolumeView!!.getDrawable().invalidateSelf()
                //                }
            }
            
            AIUIConstant.EVENT_START_RECORD -> {
                //                println("已开始录音")
                gIsRecording = true
            }
            
            AIUIConstant.EVENT_STOP_RECORD -> {
                //                Log.i("lyn----------" + localClassName, "isHome:" + gIsHome)
                //                gIsRecording = false
                gIsRecording = false
                if (gIsPhoneLocked) startWakeUp()
                
                //                speak("没听清")
                //                mAIUIDialog?.dismiss()
                //                println("已停止录音")
            }
            
            AIUIConstant.EVENT_STATE -> {    // 状态事件
                val state = event.arg1
                
                Log.i("lyn----------" + localClassName, "state:" + state)
                
                if (AIUIConstant.STATE_IDLE == state) {
                    // 闲置状态，AIUI未开启
                    println("lyn-------------STATE_IDLE")
                } else if (AIUIConstant.STATE_READY == state) {
                    // AIUI已就绪，等待唤醒
                    println("lyn-------------STATE_READY")
                    Log.i("lyn-" + localClassName, "mAIUIState:" + mAIUIState)
                    if (BuildConfig.DEBUG) {
                        assert(mAIUIDialog != null)
                    }
                    if (mAIUIState == AIUIConstant.STATE_WORKING) {
                        Log.d("lyn----------" + localClassName, "mAIUIDialog?.dismiss()")
                        mAIUIDialog?.dismiss()
                    }
                } else if (AIUIConstant.STATE_WORKING == state) {
                    // AIUI工作中，可进行交互
                    println("lyn-------------STATE_WORKING")
                }
                
                mAIUIState = state
            }
            
            AIUIConstant.EVENT_CMD_RETURN -> {
                if (AIUIConstant.CMD_SYNC == event.arg1) {    // 数据同步的返回
                    val dtype = event.data.getInt("sync_dtype", -1)
                    val retCode = event.arg2
                    
                    when (dtype) {
                        AIUIConstant.SYNC_DATA_SCHEMA -> {
                            if (AIUIConstant.SUCCESS == retCode) {
                                
                                gContactSyncOK = true
                                // 上传成功，记录上传会话的sid，以用于查询数据打包状态
                                // 注：上传成功并不表示数据打包成功，打包成功与否应以同步状态查询结果为准，数据只有打包成功后才能正常使用
                                mSyncSid = event.data.getString("sid")
                                
                                // 获取上传调用时设置的自定义tag
                                val tag = event.data.getString("tag")
                                
                                // 获取上传调用耗时，单位：ms
                                //                                val timeSpent = event.data.getLong("time_spent", -1)
                                //                                if (-1 != timeSpent) {
                                //                                    mTimeSpentText.setText(timeSpent.toString() + "ms")
                                //                                }
                                
                                println("上传成功，sid=$mSyncSid，tag=$tag，你可以试着说“打电话给刘德华”")
                            } else {
                                mSyncSid = ""
                                println("上传失败，错误码：$retCode")
                            }
                        }
                    }
                } else if (AIUIConstant.CMD_QUERY_SYNC_STATUS == event.arg1) {    // 数据同步状态查询的返回
                    // 获取同步类型
                    val syncType = event.data.getInt("sync_dtype", -1)
                    if (AIUIConstant.SYNC_DATA_QUERY == syncType) {
                        // 若是同步数据查询，则获取查询结果，结果中error字段为0则表示上传数据打包成功，否则为错误码
                        val result = event.data.getString("result")
                        
                        println(result)
                    }
                }
            }
            
            else -> {
            }
        }
    }
    
    private fun requestAllPower(): Boolean {
        //        if (ContextCompat.checkSelfPermission(this,
        //                        Manifest.permission.CALL_PHONE) != PackageManager
        //                        .PERMISSION_GRANTED) {
        //            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
        //                            Manifest.permission.CALL_PHONE)) {
        //            } else {
        //                ActivityCompat.requestPermissions(this,
        //                        arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.CALL_PHONE), 1)
        //            }
        //        }
        
        if (!PermissionsUtil.hasPermission(this, Manifest.permission.RECORD_AUDIO) ||
                !PermissionsUtil.hasPermission(this, Manifest.permission.CALL_PHONE) ||
                !PermissionsUtil.hasPermission(this, Manifest.permission.READ_PHONE_STATE) ||
                !PermissionsUtil.hasPermission(this, Manifest.permission.READ_CONTACTS) ||
                !PermissionsUtil.hasPermission(this, Manifest.permission.CHANGE_WIFI_STATE)) {
            requestPermission(this, object : PermissionListener {
                override fun permissionGranted(permission: Array<out String>) {
                    recreate()
                }
                
                override fun permissionDenied(permission: Array<out String>) {
                    finish()
                }
            }, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest
                    .permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CHANGE_WIFI_STATE)
            return false
        } else {
            return true
        }
    }
    
    
    private fun handleAIUIResult(resultStr: String) {
        //        mBHeardSth = false
        gBHit = true   //init
        gBAction = true
        gLatePostLog = false
        gStrTts = ""
        gLogParams.clear()
        
        initSemanticResult(resultStr)
        var text = (parsedSemanticResult?.text ?: "")
        //        if (text.startsWith("小美")) text = text.replaceFirst("小美", "")
        if (text.startsWith("小美小美")) text = text.replaceFirst("小美小美", "")
        
        val service = parsedSemanticResult?.service ?: ""
        val semantic = parsedSemanticResult?.semantic
        val answer = parsedSemanticResult?.answer ?: ""
        val intent = semantic?.optString("intent") ?: ""
        
        fun getSlotValueByName(name: String): String? {
            val slots = semantic?.optJSONArray("slots") ?: return null
            
            for (i in 0 until slots.length()) {
                val slot = slots.optJSONObject(i)
                if (slot.optString("name") == name) {
                    val normValue = slot.optString("normValue")
                    return if (normValue.isNullOrEmpty()) slot.optString("value") else normValue
                }
            }
            return null
        }
        
        //        fun getSlotNormValueByName(name: String): String? {
        //            val slots = semantic?.optJSONArray("slots") ?: return null
        //
        //            for (i in 0 until slots.length()) {
        //                val slot = slots.optJSONObject(i)
        //                if (slot.optString("name") == name) {
        //                    return slot.optString("normValue")
        //                }
        //            }
        //            return null
        //        }
        
        
        Log.i("lyn-" + localClassName, "text:$text")
        Log.i("lyn-" + localClassName, "intent:$intent")
        
        //一个字也算没听清
        if (text.isNullOrEmpty() || text.length == 1) {
            speak(getString(R.string.not_heard))
            //            Handler().postDelayed({
            //                startActivity(Intent(applicationContext,
            //                        MainActivity::class.java))
            //            }, 1200)
            return
        }
        
        //        mBHeardSth = true
        //        mAsrTxt = text
        //        FlowerCollector.onEvent(this, "asr", text)       //记录asr
        //        Log.i("lyn----------" + localClassName, "service:" + service)
        
        when (service) {
            "LXY.map" -> {
                gBAction = false
                when (intent) {
                    "daohang" -> {
                        val dest = getSlotValueByName("destination")
                        val i1 = Intent()
                        // 驾车路线规划（百度）
                        i1.data = Uri.parse("baidumap://map/direction?destination=${dest}&mode=driving")
                        var callMapOk = false
                        try {
                            startActivity(i1)
                            callMapOk = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                            //高德
                            i1.data = Uri.parse("androidamap://poi?&keywords=${dest}&dev=0")
                            try {
                                startActivity(i1)
                                callMapOk = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        
                        if (callMapOk) sayOK()
                        else speak("主人，需要先安装百度地图或高德地图哦")
                    }
                    else -> saySorry()
                }
            }
            "weixin", "LXY.weixin" -> {
                gWxContact = getSlotValueByName("receiver") ?: getSlotValueByName("contact") ?: getSlotValueByName("fukuan") ?: ""
                
                val contactPinYin = PinyinHelper.convertToPinyinString(gWxContact, "", PinyinFormat
                        .WITHOUT_TONE)
                
                
                when (intent) {
                    "SEND", "send_msg" -> {
                        val contentType = getSlotValueByName("contentType") ?: ""
                        if (contentType == "voice") {  //语音
                            speak("发语音技能还在学习中，查找$gWxContact")
                            startActivity(Intent().setComponent(ComponentName("com.tencent.mm",
                                    "com.tencent.mm.ui.LauncherUI")).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            gAccessibilityService.wxContact()
                        } else {
                            if (arrayOf("saoyisao", "saoma", "erweima", "saomiaoerweima").contains
                                    (contactPinYin)) {
                                gWxContact = ""
                                return wxScan()
                            }
                            
                            gWxContent = getSlotValueByName("content") ?: ""
                            if (gWxContent.isNullOrEmpty()) {
                                speak("微信查找$gWxContact")
                            } else {
                                speak("发送${gWxContent}给$gWxContact")
                            }
                            startActivity(Intent().setComponent(ComponentName("com.tencent.mm",
                                    "com.tencent.mm.ui.LauncherUI")).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            gAccessibilityService.wxContact()
                        }
                    }
                    "send_redbag" -> {
                        speak("发红包技能还在学习中，查找$gWxContact")
                        //                        gWxContact = getSlotValueByName("contact") ?: ""
                        startActivity(Intent().setComponent(ComponentName("com.tencent.mm",
                                "com.tencent.mm.ui.LauncherUI")).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        gAccessibilityService.wxContact()
                    }
                    "scan_qrcode" -> {
                        wxScan()
                    }
                    else -> {
                        saySorry()
                    }
                }
            }
            "LXY.alipay" -> {
                when (intent) {
                    "scan" -> alipayScan()
                    "qrcode" -> alipayQRcode()
                }
            }
            "websearch" -> {
                val channel = getSlotValueByName("channel") ?: ""
                val word = getSlotValueByName("keyword") ?: ""
                if (channel == "taobao") {
                    speak("淘宝搜索$word")
                    val url = "https://s.m.taobao.com/h5?q=" + word
                    loadUrl(url, true)
                } else {
                    search(word)
                }
            }
            "weather" -> {
                speak(answer)
                //泛问天气才显示web
                if (text.contains("天气") && !text.contains("气温")) {
                    val url = "https://www.baidu.com/s?word=$text"
                    loadUrl(url)
                }
            }
            "scheduleX" -> {
                when (intent) {
                    "CREATE" -> {
                        speak((answer ?: "好的，我记住啦") + "。要取消的话，对我说取消提醒哦")
                        val suggestDatetime = JSONObject(getSlotValueByName("datetime")).optString("suggestDatetime")
                        val content = getSlotValueByName("content") ?: ""
                        ScheduleCreate("clock", suggestDatetime, null, content,
                                applicationContext).start()
                    }
                    "CANCEL" -> {
                        //                        val suggestDatetime = JSONObject(getSlotValueByName("datetime")).optString("suggestDatetime")
                        ScheduleCreate("clock", "", null, "",
                                applicationContext).cancelClock()
                    }
                }
            }
            "joke" -> {
                val result0 = parsedSemanticResult!!.data!!.optJSONArray("result")?.optJSONObject(0)
                speak(result0?.optString("content"))
            }
            "datetime" -> {
                if (answer.contains(":")) {
                    val timeArray = answer.split(" ")?.get(2).split(":")
                    val hour = timeArray[0].toInt()
                    val minute = timeArray[1].toInt()
                    var hourStr: String
                    var minuteStr: String
                    //                    if (BuildConfig.DEBUG) {
                    //                        //                       speak("12:30")
                    //                        hour = 24
                    //                        minute = 0
                    //                    }d
                    when (hour) {
                        0 -> hourStr = "12"
                        in 13..24 -> hourStr = "${hour - 12}"
                        else -> hourStr = hour.toString()
                    }
                    
                    when (minute) {
                        0 -> minuteStr = ""
                        2 -> minuteStr = "零二分"
                        in 1..9 -> minuteStr = "零${minute}分"
                        10 -> minuteStr = "十分"
                        30 -> minuteStr = "半"
                        else -> minuteStr = "$minute"
                    }
                    speak("${hourStr}点$minuteStr")
                } else {
                    speak(answer)
                }
            }
            "openQA", "calc", "poetry", "stock" -> {
                speak(answer.replace("[k3]", "").replace("[k0]", ""))
            }
            "LXY.taobao" -> {
                val goods = getSlotValueByName("goods") ?: ""
                speak("淘宝搜索${goods}")
                val url = "https://s.m.taobao.com/h5?q=" + goods
                loadUrl(url, true)
            }
            "LXY.app" -> {
                var appName = getSlotValueByName("any") ?: getSlotValueByName("app")
                ?: getSlotValueByName("app2") ?: "小美"
                when (intent) {
                    "launch_app" -> {
                        //                        val switch = getSlotValueByName("launch")
                        
                        //                        if (switch == "关闭") {
                        //
                        //                        } else {  //dakai or null
                        //                        speak("打开" + appName)
                        when (appName) {
                            "蓝牙" -> if (turnOnBluetooth()) speak("蓝牙已打开") else {
                                gBAction = false
                                speak("打开失败,请检查权限")
                                showMyAppDetailsActiviry()
                            }
                            "wifi" -> {
                                if (setWifiEnabled(true)) speak("wifi已打开") else {
                                    gBAction = false
                                    speak("打开失败,请检查权限")
                                    showMyAppDetailsActiviry()
                                }
                            }
                            "微信扫码" -> wxScan()
                            "手电筒" -> {
                                sayOK()
                                trunOnFlash()
                            }
                            "home键唤醒" -> {
                                speak("已打开长按home键唤醒")
                                turnOnHomeKeyWake()
                            }
                            "静音" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_MUTE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            else -> if (!openApp(appName, applicationContext)) {
                                speak("没有找到${appName}呢")
                                gBAction = false
                            }
                            //onAsrResult
                            // (text)
                        }
                        //                        }
                    }
                    "close_app" -> {
                        when (appName) {
                            "蓝牙" -> if (turnOffBluetooth()) speak("蓝牙已关闭") else {
                                gBAction = false
                                speak("关闭失败,请检查权限")
                                showMyAppDetailsActiviry()
                            }
                            "wifi" -> {
                                if (setWifiEnabled(false)) {
                                    Handler().postDelayed({ speak("wifi已关闭") }, 1000L)
                                } else {
                                    gBAction = false
                                    speak("关闭失败,请检查权限")
                                    showMyAppDetailsActiviry()
                                }
                            }
                            "手电筒" -> {
                                sayOK()
                                turnOffFlash()
                            }
                            "home键唤醒" -> {
                                speak("已关闭长按home键唤醒")
                                turnOffHomeKeyWake()
                            }
                            "静音" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_UNMUTE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            else -> saySorry()
                        }
                    }
                }
            }
            "telephone"/*, "LXY.tel"*/ -> {
                val contact = parsedSemanticResult!!.data?.optJSONArray("result")
                        ?.optJSONObject(0)
                val name = contact?.optString("name") ?: getSlotValueByName("name")
                val code = contact?.optString("phoneNumber") ?: getSlotValueByName("code")
                //                val name = getSlotValueByName("name")
                //                val code = getSlotValueByName("code")
                if (!name.isNullOrEmpty() || !code.isNullOrEmpty()) {
                    
                    //                    requestAllPower()
                    
                    gBAction = CallAction(name, code, applicationContext).start()
                } else {
                    //                    val name = getSlotValueByName("name")
                    gBAction = false
                    speak("没找到这个人呢")
                }
            }
            "radio" -> {
                val result0 = parsedSemanticResult!!.data!!.optJSONArray("result")?.optJSONObject(0)
                val url = result0?.optString("url")
                if (url.isNullOrEmpty()) {
                    gBAction = false
                    speak("未找到电台")
                    search(text)
                } else {
                    speak("找到${result0?.optString("aliasName")?.split(",")?.get(0)}")
                    loadUrl(url!!, true)
                }
            }
            "cmd", "musicX", "LXY.music" -> {
                when (intent) {
                    "PLAY", "default_intent" -> {
                        val numPattern = "(?<=\"wap_playFile\":\")(.*?)(?=\")".toRegex()
                        val song = getSlotValueByName("song") ?: ""
                        val artist = getSlotValueByName("artist") ?: ""
                        val genre = getSlotValueByName("genre") ?: ""
                        val tags = getSlotValueByName("tags") ?: ""
                        val content = getSlotValueByName("content") ?: ""  //LXY.music
                        
                        if (content != "音乐") {
                            speak("查找歌曲。")
                            gLatePostLog = true
                            thread {
                                val word = genre + artist + song + tags + content
                                val htmlContent = URL("https://www.baidu" +
                                        ".com/sf?pd=music_songmulti&openapi=1&dspName=iphone&from_sf=1&resource_id=4621" +
                                        "&word=$word" +
                                        "&lid=15992139653238374049&ms=1&frsrcid=8041&frorder=4").readText()
                                val url = numPattern.find(htmlContent)?.value?.replace("\\", "")
                                Log.i("lyn----------" + localClassName, "music url:" + url)
                                
                                runOnUiThread {
                                    if (url.isNullOrEmpty()) {
                                        speak("未找到歌曲")
                                        search(word, true, false)
                                        gLogParams["action"] = "0"
                                        
                                    } else {
                                        speak("找到歌曲")
                                        loadUrl(url!!, true)
                                        gLogParams["action"] = "1"
                                    }
                                    
                                    //late post log
                                    gLogParams["tts"] = gStrTts
                                    val request = JsonObjectRequest(
                                            Request.Method.POST, gLogUrl,
                                            JSONObject(gLogParams), { jsonObj -> }, { jsonObj -> })
                                    gVolleyQueue.add(request)
                                }
                            }
                        } else {
                            musicFM()
                        }
                    }
                    "RANDOM_SEARCH" -> {
                        musicFM()
                    }
                    "INSTRUCTION" -> {
                        val insType = getSlotValueByName("insType")
                        when (insType) {
                            "volume_minus" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            "volume_plus" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager
                                        .STREAM_MUSIC,
                                        AudioManager.ADJUST_RAISE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_RAISE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            "mute" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_MUTE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            "unmute" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_UNMUTE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            "pause" -> {
                                pauseMusic()
                            }
                            "replay" -> {
                                replayMusic()
                            }
                            "next" -> {
                                nextMusic()
                            }
                            "past" -> {
                                val starter = Intent(this, ExecCmdActivity::class.java)
                                starter.action = "past"
                                startActivity(starter)
                            }
                            else -> saySorry()
                        }
                    }
                    /*"cmd" -> {
                        val insType = getSlotValueByName("insType") ?: ""
                        when (insType) {
                            //                    "sleep" -> gTts?.stop()
                            "volume_minus" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager
                                        .STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            "volume_plus" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_RAISE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            "mute" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_MUTE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            "unmute" -> {
                                sayOK()
                                gAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_UNMUTE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP)
                            }
                            "sleep" -> { *//*闭嘴*//*
                            }
                            else -> saySorry()
                        }
                    }*/
                    //                    "" -> {
                    //                        val type = getSlotValueByName("")
                    //                    }
                    else -> {
                        saySorry()
                    }
                }
                
            }
            "" -> {     //aiui未命中
                onAsrResult(text)
            }
            else -> {
                saySorry()
            }
        }
        
        //        Handler().postDelayed({
        //            mIatDialog?.dismiss()
        //        }, 500)
        
        Log.i("lyn----------" + localClassName, "hit:" + gBHit)
        Log.i("lyn----------" + localClassName, "gBAction:" + gBAction)
        
        if (!text.isNullOrEmpty()) {
            //统计
            gLogParams["username"] = gDeviceId
            gLogParams["message"] = text
            gLogParams["hit"] = if (gBHit) "1" else "0"
            gLogParams["intent"] = resultStr
            gLogParams["service"] = service
            gLogParams["tts"] = gStrTts
            gLogParams["action"] = if (gBAction) "1" else "0"
            
            if (!gLatePostLog) {
                val request = JsonObjectRequest(
                        Request.Method.POST, gLogUrl,
                        JSONObject(gLogParams), { jsonObj -> }, { jsonObj -> })
                gVolleyQueue.add(request)
            }
        }
        
    }
    
    
    //   EventListener  回调方法
    @SuppressLint("MissingPermission")
    fun onAsrResult(asrResult: String) {
        Log.i("lyn----------" + localClassName, "onAsrResult:" + asrResult)
        //        if (asrResult.isNullOrEmpty() || asrResult.length == 1) {
        //            return
        //        }
        
        //        var quitAtOnce = true
        //        var shouldFinishSelf = true
        val starter = Intent()
        
        //        val resultPinYin = PinyinHelper.convertToPinyinString(asrResult, "", PinyinFormat
        //                .WITHOUT_TONE)
        
        when {
            asrResult.startsWith("搜索") -> {
                //        val searchAction = SearchAction(asrResult!!.substring(2), this@MainActivity)
                //        searchAction.Search()
                
                //                val url = "https://www.baidu.com/s?word=${asrResult!!.substring(2)}"
                search(asrResult!!.substring(2), shouldSpeak = true)
            }
            asrResult.startsWith("拨打") || asrResult.startsWith("打给")
                    || asrResult.startsWith("博达") || asrResult.startsWith("播打") -> {
                CallAction(asrResult!!.substring(2), "", applicationContext).start()
            }
            asrResult.startsWith("打电话给") -> {
                CallAction(asrResult!!.substring(4), "", applicationContext).start()
            }
            else -> when (PinyinHelper.convertToPinyinString(asrResult, "", PinyinFormat
                    .WITHOUT_TONE)) {
                "shezhi", "xiaomeishezhi", "shezhixiaomei" -> startActivity(Intent(this,
                        PreferencesActivity::class
                        .java))
                "quxiaotixing", "quxiaonaozhong" -> ScheduleCreate("clock", "", null, "",
                        applicationContext).cancelClock()
                "paizhao", "woyaopaizhao" -> {
                    sayOK()
                    starter.action = "android.media.action.STILL_IMAGE_CAMERA_SECURE"
                    startActivity(starter)
                }
                "bofang" -> replayMusic()
                "tianqi" -> {
                    //                shouldFinishSelf = false
                    loadUrl("https://www.baidu.com/s?word=天气")
                    //                mWebView.loadUrl(url,gUrlToLoad)
                }
                "waimai" -> {
                    //                shouldFinishSelf = false
                    val url = "https://h5.ele.me"
                    loadUrl(url)
                    //                mWebView.loadUrl(url,gUrlToLoad)
                }
                //                "wenyizhisheng" -> {
                //                    val url = "http://m.tingshouyinji.cn/play.php?id=10"
                //                    loadUrl(url, true)
                //                }
                
                
                //                "yinyue" -> {
                //                    starter.component = ComponentName("com.netease.cloudmusic", "com.netease" + ".cloudmusic.activity.RedirectActivity")
                //                    starter.action = Intent.ACTION_VIEW
                //                    starter.data = Uri.parse("orpheus://radio")
                //                    starter.flags = Intent.FLAG_RECEIVER_FOREGROUND
                //                    startActivity(starter)
                //                }
                
                "fukuanma", "zhifubaofukuanma" -> {
                    alipayQRcode()
                }
                //                "saoma", "saoyisao", "erweima" -> {
                //                    speak("微信扫码")
                //                    try {
                //                        intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
                //                        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                //                        startActivity(intent)
                //                    } catch (e: Exception) {
                //                        e.printStackTrace()
                //                    }
                //                }
                //                "朋友圈" -> {
                //                    intent = packageManager.getLaunchIntentForPackage("com.tencent.mm")
                //                    startActivity(intent)
                //                    Handler().postDelayed({ execShellCmd("input tap 688 1859") }, 500)
                //                    Handler().postDelayed({ execShellCmd("input tap 575 308") }, 1500)
                //                }
                else -> {
                    gBHit = false
                    
                    println("______appn:$asrResult")
                    
                    if (!openApp(asrResult!!, applicationContext, false)) {
                        gBAction = false
                        search(asrResult, false, true)
                    }
                }
            }
        }
    }
    
    
    private fun createAgent() {
        destroyAgent()
        
        if (null == gAIUIAgent) {
            Log.i(TAG, "create aiui agent")
            
            gAIUIAgent = AIUIAgent.createAgent(applicationContext, getAIUIParams(), mAIUIListener)
        }
        
        if (null == gAIUIAgent) {
            val strErrorTip = "创建AIUIAgent失败！"
            println(strErrorTip)
            
            //            mNlpText.setText(strErrorTip)
        } else {
            println("AIUIAgent已创建")
        }
    }
    
    
    private fun destroyAgent() {
        if (null != gAIUIAgent) {
            Log.i(TAG, "destroy aiui agent")
            
            gAIUIAgent!!.destroy()
            gAIUIAgent = null
            
            println("AIUIAgent已销毁")
        } else {
            println("AIUIAgent为空")
        }
    }
    
    //    private var VolumeView: ImageView? = null
    
    
    private fun showDialog() { //语音识别
        //        mBIsSleeped = false
        
        //        mAsrTxt = ""
        //        gBBackKeyPressed = false
        
        requestAudioFocus()
        //        gPlayer?.start()
        //        if (!BuildConfig.DEBUG) {  //debug环境不播放提示音
        //        }
        
        //        mVoicePop!!.showAtLocation(window.decorView, Gravity.CENTER, 0, 0)
        
        // 显示听写对话框
        Log.d("lyn----------" + localClassName, "mAIUIDialog?.show()")
        mAIUIDialog?.show()
        mAIUIDialog?.window?.setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT)
        
        Handler().postDelayed({ startVoiceNlp() }, 500L)
        //百度asr
        /*val params = LinkedHashMap<String, Any>()
        var event: String? = null
        event = SpeechConstant.ASR_START // 替换成测试的event
        
        //        if (enableOffline) {
        //            params[SpeechConstant.DECODER] = 2
        //        }
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        // params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        // params.put(SpeechConstant.PROP ,20000);
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号
        // 请先使用如‘在线识别’界面测试和生成识别参数。 params同ActivityRecog类中myRecognizer.start(params);
        var json: String? // 可以替换成自己的json
        json = JSONObject(params).toString() // 这里可以替换成你需要测试的json
        asr!!.send(event, json, null, 0, 0)*/
        
    }
    
    
    private fun execShellCmd(cmd: String) {
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            val process = Runtime.getRuntime().exec("su")
            // 获取输出流
            val outputStream = process.outputStream
            val dataOutputStream = DataOutputStream(
                    outputStream)
            dataOutputStream.writeBytes(cmd)
            dataOutputStream.flush()
            dataOutputStream.close()
            outputStream.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
    
    
    //    override fun onClick(view: View) { //语音识别过程
    //        showDialog()
    //    }
    
    
    /**
     * Check if Accessibility Service is enabled.
     */
    fun checkAccessibilitySetting(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        //your package /   accesibility service path/class
        val service = "com.beautylife.va/com.example.ddvoice.MyAccessibilityService"
        
        //        val accessibilityFound = false
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.applicationContext.contentResolver,
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED)
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: Settings.SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        
        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------")
            val settingValue = Settings.Secure.getString(
                    mContext.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                val splitter = mStringColonSplitter
                splitter.setString(settingValue)
                while (splitter.hasNext()) {
                    val accessabilityService = splitter.next()
                    
                    Log.v(TAG, "-------------- > accessabilityService :: $accessabilityService")
                    if (accessabilityService.equals(service, ignoreCase = true)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!")
                        return true
                    }
                }
            }
        }
        
        
        Log.v(TAG, "***ACCESSIBILIY IS DISABLED***")
        return false
    }
    
    
    //        private fun addToList(msg: String, isSiri: Boolean) {
    //            //
    //            list!!.add(SiriListItem(msg, isSiri))
    //            mAdapter!!.notifyDataSetChanged()
    //            mListView!!.setSelection(list!!.size - 1)
    //        }
    
    //    inner class SiriListItem(var message: String, var isSiri: Boolean)
    
    //    private fun showTip(str: String) {
    //        info!!.setText(str)
    //        info!!.show()
    //    }
    
}