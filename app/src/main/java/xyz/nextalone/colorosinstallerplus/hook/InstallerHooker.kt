package xyz.nextalone.colorosinstallerplus.hook

import android.content.pm.PackageManager
import android.os.Parcelable
import android.text.TextUtils
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerW
import xyz.nextalone.colorosinstallerplus.hook.HookConst.INSTALLER_PACKAGE_NAME

object InstallerHooker : YukiBaseHooker() {
    private const val ApkInfoViewClass = "$INSTALLER_PACKAGE_NAME.oplus.view.ApkInfoView"
    private const val ApkInfoClass = "$INSTALLER_PACKAGE_NAME.oplus.common.ApkInfo"
    override fun onHook() {
        ApkInfoViewClass.hook {
            injectMember {
                method {
                    name("loadApkInfo")
                }
                afterHook {
                    val appVersionTextView = field { name = "mAppVersion" }.get(instance).cast<TextView>()
                    val apkInfoView = appVersionTextView?.parent as? LinearLayout
                    apkInfoView?.orientation = LinearLayout.VERTICAL

                    val apkInfo = args().first().cast<Parcelable>()
                    val appPackageName = ApkInfoClass.clazz.field { name = "packageName" }.get(apkInfo).string()
                    val appVersionCode = ApkInfoClass.clazz.field { name = "versionCode" }.get(apkInfo).int()
                    val appVersion = ApkInfoClass.clazz.field { name = "versionName" }.get(apkInfo).string()

                    val oldPkgInfo = try {
                        apkInfoView?.context?.packageManager?.getPackageInfo(appPackageName, PackageManager.MATCH_UNINSTALLED_PACKAGES)
                    } catch (e: Exception) {
                        null
                    }
                    val oldVersionStr = """${oldPkgInfo?.versionName ?: "N/A"}(${oldPkgInfo?.longVersionCode}) → """

                    val apkPackageNameTextView = TextView(apkInfoView?.context)
                    apkPackageNameTextView.layoutParams = appVersionTextView?.layoutParams
                    "包名：$appPackageName".also { apkPackageNameTextView.text = it }
                    val newAppVersionTextView = TextView(apkInfoView?.context)
                    newAppVersionTextView.layoutParams = appVersionTextView?.layoutParams
                    if (oldPkgInfo == null)
                        "版本：$appVersion($appVersionCode)".also { newAppVersionTextView.text = it }
                    else
                        "版本：$oldVersionStr$appVersion($appVersionCode)".also { newAppVersionTextView.text = it }

                    appVersionTextView?.textSize?.let { apkPackageNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
                    appVersionTextView?.textSize?.let { newAppVersionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
                    apkInfoView?.removeView(appVersionTextView)
                    apkInfoView?.addView(apkPackageNameTextView, 0)
                    apkInfoView?.addView(newAppVersionTextView, 1)
                }
            }
        }
    }
}