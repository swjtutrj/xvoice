package com.example.ddvoice.util

import android.util.Log
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizerListener
import com.example.ddvoice.abandonAudioFocus
import com.example.ddvoice.gIsSpeaking
import com.example.ddvoice.requestAudioFocus

/**
 * SpeechSynthesizerListener 简单地实现，仅仅记录日志
 * Created by fujiayi on 2017/5/19.
 */

class TtsMessageListener : SpeechSynthesizerListener {
    
    //    static final int PRINT = 0;
    //    static final int UI_CHANGE_INPUT_TEXT_SELECTION = 1;
    //    static final int UI_CHANGE_SYNTHES_TEXT_SELECTION = 2;
    //
    //    static final int INIT_SUCCESS = 2;
    
    /**
     * 播放开始，每句播放开始都会回调
     *
     * @param utteranceId
     */
    override fun onSynthesizeStart(utteranceId: String) {
        sendMessage("准备开始合成,序列号:$utteranceId")
    }
    
    /**
     * 语音流 16K采样率 16bits编码 单声道 。
     *
     * @param utteranceId
     * @param bytes       二进制语音 ，注意可能有空data的情况，可以忽略
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法和合成到第几个字对应。
     */
    override fun onSynthesizeDataArrived(utteranceId: String, bytes: ByteArray, progress: Int) {
        //  Log.i(TAG, "合成进度回调, progress：" + progress + ";序列号:" + utteranceId );
        
//        thread {
//            val currentDir = "/sdcard"
//            val file = File(currentDir, "wozai.pcm")
//            file.createNewFile()
//            //追加方式写入字节或字符
//            file.appendBytes(bytes)
//            println("lyn--------------file len: " + file.length())
//        }
        
    }
    
    /**
     * 合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
     *
     * @param utteranceId
     */
    override fun onSynthesizeFinish(utteranceId: String) {
        sendMessage("合成结束回调, 序列号:$utteranceId")
    }
    
    override fun onSpeechStart(utteranceId: String) {
        gIsSpeaking = true
        
        sendMessage("播放开始回调, 序列号:$utteranceId")
        requestAudioFocus()
    }
    
    /**
     * 播放进度回调接口，分多次回调
     *
     * @param utteranceId
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法保证和合成到第几个字对应。
     */
    override fun onSpeechProgressChanged(utteranceId: String, progress: Int) {
        //  Log.i(TAG, "播放进度回调, progress：" + progress + ";序列号:" + utteranceId );
    }
    
    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    override fun onSpeechFinish(utteranceId: String) {
        gIsSpeaking = false
    
        sendMessage("播放结束回调, 序列号:$utteranceId")
        abandonAudioFocus()
    }
    
    /**
     * 当合成或者播放过程中出错时回调此接口
     *
     * @param utteranceId
     * @param speechError 包含错误码和错误信息
     */
    override fun onError(utteranceId: String, speechError: SpeechError) {
        gIsSpeaking = false
        
        sendErrorMessage("错误发生：" + speechError.description + "，错误编码："
                + speechError.code + "，序列号:" + utteranceId)
        abandonAudioFocus()
    }
    
    private fun sendErrorMessage(message: String) {
        sendMessage(message, true)
    }
    
    
    private fun sendMessage(message: String) {
        sendMessage(message, false)
    }
    
    protected fun sendMessage(message: String, isError: Boolean) {
        if (isError) {
            Log.e(TAG, message)
        } else {
            Log.i(TAG, message)
        }
        
    }
    
    companion object {
        private val TAG = "MessageListener"
    }
}
