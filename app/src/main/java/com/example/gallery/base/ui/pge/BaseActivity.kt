package com.example.gallery.base.ui.pge

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.AdaptScreenUtils
import com.blankj.utilcode.util.ScreenUtils
import com.example.gallery.base.BaseApplication
import com.example.gallery.base.bindings.DataBindingActivity
import com.example.gallery.base.response.manager.NetworkStateManager

abstract class BaseActivity : DataBindingActivity() {
    private var activityProvider: ViewModelProvider? = null
        get() {
            if (field == null) {
                field = ViewModelProvider(this)
            }
            return field
        }
    private var applicationProvider: ViewModelProvider? = null
        get() {
            if (field == null) {
                field = ViewModelProvider(
                    this.applicationContext as BaseApplication,
                    getAppFactory(this)
                )
            }
            return field
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(NetworkStateManager.instance)
        initPermission()
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory(application)
    }

    private fun checkApplication(activity: Activity) = activity.application ?: throw IllegalStateException(
        "Your activity/fragment is not yet attached to "
                + "Application. You can't request ViewModel before onCreate call."
    )

    protected fun <T : ViewModel> getActiityScopeViewModel(modelClass: Class<T>) = activityProvider!!.get(modelClass)

    protected fun <T : ViewModel> getApplicationScopeViewModel(modelClass: Class<T>) = applicationProvider!![modelClass]

    fun getResorces() =
        if (ScreenUtils.isPortrait()) AdaptScreenUtils.adaptWidth(super.getResources(), 375)
        else AdaptScreenUtils.adaptHeight(super.getResources(), 640)

    protected fun toggleSoftInput() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    protected fun openUrlInBrowser(url: String?) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun showLongToast(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    private fun showShortToast(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    protected fun showLongToast(stringRes: Int) {
        showLongToast(applicationContext.getString(stringRes))
    }

    protected fun showShortToast(stringRes: Int) {
        showShortToast(applicationContext.getString(stringRes))
    }

    //??????EditText???????????????????????????
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isSoftShowing() && isShouldHideInput(v, ev)) {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v!!.windowToken, 0)
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //?????????????????????????????????
    private fun isSoftShowing(): Boolean {
        //?????????????????????????????????
        val screenHeight = window.decorView.height
        //??????View???????????????bottom
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        return screenHeight - rect.bottom !== 0
    }

    /**
     * android 6.0 ??????????????????????????????
     */
    protected fun initPermission() {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val toApplyList = ArrayList<String>()
        for (perm in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    this,
                    perm
                )
            ) {
                toApplyList.add(perm)
                // ?????????????????????????????????.
            }
        }
        val tmpList = arrayOfNulls<String>(toApplyList.size)
        if (toApplyList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            for (i in permissions.indices) {
                Log.i("MainActivity", "?????????????????????" + permissions[i] + ",???????????????" + grantResults[i])
            }
        }
    }

    //????????????????????????
    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //????????????????????????location??????
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }
}