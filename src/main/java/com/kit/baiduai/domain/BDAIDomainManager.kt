package com.kit.baiduai.domain

import android.content.pm.PackageInfo
import com.kit.baiduai.enmus.BDAIDomain
import com.kit.utils.*
import com.kit.utils.log.Zog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

/**
 * 语义理解分发类
 *
 * Created by Zhao on 2017/12/23.
 */
object BDAIDomainManager {


    /**
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
            val res0 = if (results[0] == null) null else (results[0] as JSONObject)

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

            var bdaiDomainEntity: BDAIDomainEntity? = null
            try {
                bdaiDomainEntity = GsonUtils.getObj(res0.toString(), BDAIDomainEntity::class.java)
            } catch (e: Exception) {
            }

            if (bdaiDomainEntity == null) {
                onDomainDispatch?.onDomainNone(nluResult)
                return
            }

            when (bdaiDomainEntity.domain.toUpperCase()) {
                BDAIDomain.WEATHER.name -> {
                    onDomainDispatch?.onDomainWeather(bdaiDomainEntity)
                }

                else -> {
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

        if (interruptApp(rawText, onDomainDispatch)) {
            return true
        }

        return false
    }


    /**
     * 本地拦截 app 类型
     */
    private fun interruptApp(rawText: String, onDomainDispatch: OnDomainDispatch?): Boolean {
        if (rawText.startsWith("打开") || rawText.startsWith("启动")) {
            var appName = rawText.substring(2)
            Zog.i("appName:$appName")


            val disposableObserver = object : DisposableObserver<PackageInfo>() {
                override fun onNext(packageInfo: PackageInfo) {
                    if (!this.isDisposed) {
                        Zog.i("onNext")
                        val map = hashMapOf("_appname" to appName, "appname" to appName, "packageName" to packageInfo.packageName)
                        val bdaiDomainEntity = BDAIDomainEntity("app", "open", map, 1.00)
                        onDomainDispatch?.onDomainApp(bdaiDomainEntity)
                    }
                }

                override fun onError(e: Throwable) {
                    if (!this.isDisposed) {
                        Zog.i("onError")
                    }
                }

                override fun onComplete() {
                    if (!this.isDisposed) {
                        Zog.i("onComplete")
                    }
                }
            }

            Observable.create<PackageInfo> { e ->
                val packageInfos = AppUtils.getPackageNamesByAppName(ResWrapper.getInstance().applicationContext, appName)
                var packageInfo: PackageInfo? = null

                if (packageInfos == null) {
                    packageInfo = PackageInfo()
                } else {
                    for (pkinfo in packageInfos) {
                        val thisAppName = pkinfo.applicationInfo.loadLabel(ResWrapper.getInstance().applicationContext.packageManager).toString()
                        if (thisAppName == appName) {
                            packageInfo = pkinfo
                            break
                        }
                    }
                    if (packageInfo == null)
                        packageInfo = packageInfos[0]
                }

                e.onNext(packageInfo ?: PackageInfo())
                e.onComplete()
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(disposableObserver)

            disposables.add(disposableObserver)


            return true
        }

        return false
    }

    interface OnDomainDispatch {
        fun onDomainWeather(baiduDomainEntity: BDAIDomainEntity)
        fun onDomainApp(baiduDomainEntity: BDAIDomainEntity)

        fun onDomainNone(string: String?)

    }


    fun destory() {
        disposables.clear()
    }


    val disposables = CompositeDisposable()


}
