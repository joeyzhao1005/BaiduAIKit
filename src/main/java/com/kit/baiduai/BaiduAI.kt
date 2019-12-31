package com.kit.baiduai

import com.baidu.speech.asr.SpeechConstant
import com.kit.app.application.AppMaster
import com.kit.baiduai.control.BDAIRecognizer
import com.kit.baiduai.recognization.IRecogListener
import com.kit.baiduai.recognization.PidBuilder
import com.kit.utils.FileUtils
import com.kit.utils.RandomUtils
import com.kit.utils.StringUtils
import com.kit.utils.log.Zog
import java.io.File

/**
 * 语音听写
 */
class ASR {

    fun cancel() {
        bdaiRecognizer?.cancel()
    }

    fun start() {
        bdaiRecognizer?.start(params)
    }

    fun destory() {
        params = null
        recogListener = null
        bdaiRecognizer?.release()
        bdaiRecognizer = null
    }


    fun create(rl: IRecogListener): ASR {
        Zog.d("ASR created")
        if (bdaiRecognizer != null) {
            return this
        }
        this.recogListener = rl
        //val statusRecogListener = StatusRecogListener()
        bdaiRecognizer = (BDAIRecognizer(AppMaster.getInstance().appContext, recogListener))
        return this
    }


    fun init(outputPath: String): ASR {
        val pid = PidBuilder.create()
                .model(PidBuilder.INPUT) //如识别短句，不需要需要逗号，将PidBuilder.INPUT改为搜索模型PidBuilder.SEARCH
                .toPId()

        if (!StringUtils.isEmptyOrNullStr(outputPath) && !FileUtils.isExists(outputPath)) {
            FileUtils.mkDir(outputPath.substring(0, outputPath.lastIndexOf(File.separator)))
        }

        params = hashMapOf(SpeechConstant.ACCEPT_AUDIO_DATA to true
                , SpeechConstant.OUT_FILE to outputPath
                , SpeechConstant.PID to pid
                , SpeechConstant.DISABLE_PUNCTUATION to false
                , SpeechConstant.ACCEPT_AUDIO_VOLUME to false
        )
        return this
    }

    private lateinit var outputPath: String//samplePath + "/outfile.pcm"
    private var recogListener: IRecogListener? = null
    private var params: Map<String, Any>? = null

    companion object {
        fun get(): ASR? {
            return INSTANCE
        }

        private var bdaiRecognizer: BDAIRecognizer? = null

        val INSTANCE: ASR by lazy { ASR() }
    }
}

/**
 * 语义理解
 */
class NLU {

    fun cancel() {
        bdaiRecognizer?.cancel()
    }

    fun stop() {
        bdaiRecognizer?.stop()
    }

    fun start() {
        bdaiRecognizer?.start(params)
    }

    fun destory() {
        params = null
        recogListener = null
        bdaiRecognizer?.release()
        bdaiRecognizer = null
    }


    fun create(rl: IRecogListener): NLU {
        Zog.d("NLU created")
        if (bdaiRecognizer != null) {
            return this
        }

        this.recogListener = rl
        initParams()
        //val statusRecogListener = StatusRecogListener()
        bdaiRecognizer = BDAIRecognizer(AppMaster.getInstance().appContext, recogListener)
        return this
    }


    private fun initParams() {

        val pid = PidBuilder.create()
                .supportNlu(true)
                .model(PidBuilder.INPUT) //如识别短句，不需要需要逗号，将PidBuilder.INPUT改为搜索模型PidBuilder.SEARCH
                .toPId()
        params = hashMapOf(SpeechConstant.ACCEPT_AUDIO_DATA to false
                , SpeechConstant.PID to pid
                , SpeechConstant.DISABLE_PUNCTUATION to false
                , SpeechConstant.ACCEPT_AUDIO_VOLUME to false
                , SpeechConstant.VAD_ENDPOINT_TIMEOUT to 50)
    }

    private var recogListener: IRecogListener? = null
    private var params: Map<String, Any>? = null

    companion object {
        fun get(): NLU? {
            return INSTANCE
        }

        private var bdaiRecognizer: BDAIRecognizer? = null

        val INSTANCE: NLU by lazy { NLU() }
    }
}


class BaiduAI private constructor() {


    companion object {

        fun listenNothing(): String {
            val default = listOf("呃...并没有听到你说什么...", "对不起，我没听清...", "emmmm...你说神马？？？", "你说话了么？可以大声一点点~")
            return default[RandomUtils.getRandomIntNum(0, default.size - 1)]
        }

        fun suggestion(): String {
//            val default = listOf("你可以对我说：今天天气怎么样？", "语音打开APP，请大声对我说：打开微信", "我有速记功能，请对我说：闪念胶囊！")
            val default = listOf("你可以对我说：今天天气怎么样？", "语音打开APP，请大声对我说：打开微信")
            return default[RandomUtils.getRandomIntNum(0, default.size - 1)]
        }

        fun defaultAnswer(): String {
            val default = listOf("我是笨笨，最聪明的笨笨~", "我没能理解你在说什么...", "智商低不是我的锅，微博@Joey赵 问他为什么把我做的辣么傻！")
            return default[RandomUtils.getRandomIntNum(0, default.size - 1)]
        }

        fun get(): BaiduAI? {
            return INSTANCE
        }

        val INSTANCE: BaiduAI by lazy { BaiduAI() }
    }

}