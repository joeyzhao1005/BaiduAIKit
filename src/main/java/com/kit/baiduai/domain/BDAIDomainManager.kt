package com.kit.baiduai.domain

import android.content.pm.PackageInfo
import com.kit.app.application.AppMaster
import com.kit.baiduai.enmus.BDAIDomain
import com.kit.utils.*
import com.kit.utils.log.Zog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * 语义理解分发类
 *
 * Created by Zhao on 2017/12/23.
 */
object BDAIDomainManager {


    /**
     *
     * 由语意理解，得到 domain， 然后做分发
     *
     * 分发 domain
     */
    fun dispatch(nluResult: String?, onDomainDispatch: OnDomainDispatch?) {

        val nulObj = JSONObject(nluResult ?: "{}")
        val mergedRes = nulObj.optJSONObject("merged_res")
        val semanticForm = mergedRes?.optJSONObject("semantic_form")
        var rawText = semanticForm?.opt("raw_text")
        Zog.i("rawText:" + rawText)

        val results = semanticForm?.optJSONArray("results")
        if (results == null || results.length() <= 0) {
            Zog.i("没有解析出什么鸟")
            onDomainDispatch?.onDomainNone(nluResult)
        } else {
            val res0 = when {
                results[0] is JSONObject -> {
                    results[0] as JSONObject
                }

                else -> {
                    null
                }
            }

            if (res0 == null) {
                onDomainDispatch?.onDomainNone(nluResult)
                return
            }

            val domain = res0.opt("domain")
            if (domain == null) {
                onDomainDispatch?.onDomainNone(nluResult)
                return
            }


            Zog.i("dispatch:" + domain)
            Zog.i("res0:" + res0)

            var bdaiDomainEntity: BDAIDomainEntity? = null
            try {
                bdaiDomainEntity = GsonUtils.getObj(res0.toString(), BDAIDomainEntity::class.java)
            } catch (e: Exception) {
                Zog.showException(e)
            }

            Zog.i("bdaiDomainEntity:" + bdaiDomainEntity)
            if (bdaiDomainEntity == null || bdaiDomainEntity.domain == null) {
                onDomainDispatch?.onDomainNone(nluResult)
                Zog.i("onDomainWeather:onDomainNone")
                return
            }

            when (bdaiDomainEntity.domain?.toUpperCase()) {
                BDAIDomain.WEATHER.name -> {
                    Zog.i("onDomainWeather:" + bdaiDomainEntity)
                    onDomainDispatch?.onDomainWeather(bdaiDomainEntity)
                }

                else -> {
                    Zog.i("dispatch nluResult:" + nluResult)
                    onDomainDispatch?.onDomainNone(nluResult)
                }
            }
        }
    }


    /**
     * 本地拦截
     */
    fun interrupt(rawText: String?, onDomainDispatch: OnDomainDispatch?): Boolean {
        if (rawText == null || StringUtils.isEmptyOrNullStr(rawText))
            return false

        if (localInterruptApp(rawText, onDomainDispatch)) {
            return true
        } else if (localInterruptIdeaMode(rawText, onDomainDispatch)) {
            return true
        }

        return false
    }


    /**
     * 本地拦截 切换到闪念胶囊模式
     */
    private fun localInterruptIdeaMode(rawText: String, onDomainDispatch: OnDomainDispatch?): Boolean {
        if (rawText in listOf<String>("闪念胶囊", "闪念模式", "胶囊模式")) {

            onDomainDispatch?.onToIdeaMode()

            return true
        }

        return false
    }

    /**
     * 本地拦截 app 类型
     */
    private fun localInterruptApp(rawText: String, onDomainDispatch: OnDomainDispatch?): Boolean {
        if (rawText.startsWith("打开") || rawText.startsWith("启动")) {
            GlobalScope.launch(Dispatchers.IO) {
                var appName = rawText.substring(2)
                Zog.i("appName:$appName")
                val packageInfos = AppUtils.getPackageNamesByAppName(AppMaster.getInstance().appContext, appName)
                var packageInfo: PackageInfo? = null

                if (packageInfos == null) {
                    packageInfo = PackageInfo()
                } else {
                    for (pkinfo in packageInfos) {
                        val thisAppName = pkinfo.applicationInfo.loadLabel(AppMaster.getInstance().appContext.packageManager).toString()
                        if (thisAppName == appName) {
                            packageInfo = pkinfo
                            break
                        }
                    }
                    if (packageInfo == null && !packageInfos.isNullOrEmpty()) {
                        packageInfo = packageInfos[0]
                    }
                }

                if (packageInfo == null) {
                    packageInfo = PackageInfo()
                }

                withContext(Dispatchers.Main) {
                    val map = hashMapOf("_appname" to appName, "appname" to appName, "packageName" to packageInfo.packageName)
                    val bdAiDomainEntity = BDAIDomainEntity("app", "open", map, 1.00)
                    onDomainDispatch?.onDomainApp(bdAiDomainEntity)
                }
            }

            return true
        }

        return false
    }

    interface OnDomainDispatch {
        fun onDomainWeather(baiduDomainEntity: BDAIDomainEntity)
        fun onDomainApp(baiduDomainEntity: BDAIDomainEntity)
        fun onToIdeaMode()

        fun onDomainNone(text: String?)

    }


    fun destory() {
    }


}

