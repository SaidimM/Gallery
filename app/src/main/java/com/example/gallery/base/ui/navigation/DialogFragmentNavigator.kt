package com.example.gallery.base.ui.navigation

import android.content.Context
import androidx.navigation.Navigator
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import android.os.Bundle
import androidx.navigation.NavOptions
import androidx.navigation.NavDestination
import androidx.navigation.FloatingWindow
import androidx.navigation.NavigatorProvider
import androidx.annotation.CallSuper
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.example.gallery.R
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

@Navigator.Name("dialog")
class DialogFragmentNavigator(private val mContext: Context, private val mFragmentManager: FragmentManager) :
    Navigator<DialogFragmentNavigator.Destination>() {
    private var mDialogCount = 0
    private val mObserver = LifecycleEventObserver { source, event ->
        if (event == Lifecycle.Event.ON_STOP) {
            val dialogFragment = source as DialogFragment
            if (!dialogFragment.requireDialog().isShowing) {
                NavHostFragment.findNavController(dialogFragment).popBackStack()
            }
        }
    }

    override fun popBackStack(): Boolean {
        return if (mDialogCount == 0) {
            false
        } else if (mFragmentManager.isStateSaved) {
            Log.i(
                "DialogFragmentNavigator",
                "Ignoring popBackStack() call: FragmentManager has already saved its state"
            )
            false
        } else {
            val existingFragment =
                mFragmentManager.findFragmentByTag("androidx-nav-fragment:navigator:dialog:" + --mDialogCount)
            if (existingFragment != null) {
                existingFragment.lifecycle.removeObserver(mObserver)
                (existingFragment as DialogFragment).dismiss()
            }
            true
        }
    }

    override fun createDestination(): Destination {
        return Destination(this)
    }

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        return if (mFragmentManager.isStateSaved) {
            Log.i(
                "DialogFragmentNavigator",
                "Ignoring navigate() call: FragmentManager has already saved its state"
            )
            null
        } else {
            var className = destination.className
            if (className[0] == '.') {
                className = mContext.packageName + className
            }
            val frag = mFragmentManager.fragmentFactory.instantiate(mContext.classLoader, className)
            if (!DialogFragment::class.java.isAssignableFrom(frag.javaClass)) {
                throw IllegalArgumentException("Dialog destination " + destination.className + " is not an instance of DialogFragment")
            } else {
                val dialogFragment = frag as DialogFragment
                dialogFragment.arguments = args
                dialogFragment.lifecycle.addObserver(mObserver)
                dialogFragment.show(mFragmentManager, "androidx-nav-fragment:navigator:dialog:" + mDialogCount++)
                destination
            }
        }
    }

    override fun onSaveState(): Bundle? {
        return if (mDialogCount == 0) {
            null
        } else {
            val b = Bundle()
            b.putInt("androidx-nav-dialogfragment:navigator:count", mDialogCount)
            b
        }
    }

    override fun onRestoreState(savedState: Bundle) {
        mDialogCount = savedState.getInt("androidx-nav-dialogfragment:navigator:count", 0)
        for (index in 0 until mDialogCount) {
            val fragment =
                mFragmentManager.findFragmentByTag("androidx-nav-fragment:navigator:dialog:$index") as DialogFragment?
                    ?: throw IllegalStateException("DialogFragment $index doesn't exist in the FragmentManager")
            fragment.lifecycle.addObserver(mObserver)
        }
    }

    @NavDestination.ClassType(DialogFragment::class)
    class Destination(fragmentNavigator: Navigator<out Destination?>) : NavDestination(fragmentNavigator),
        FloatingWindow {
        private var mClassName: String? = null

        constructor(navigatorProvider: NavigatorProvider) : this(
            navigatorProvider.getNavigator<DialogFragmentNavigator>(
                DialogFragmentNavigator::class.java
            )
        ) {
        }

        @CallSuper
        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)
            val a = context.resources.obtainAttributes(attrs, R.styleable.DialogFragmentNavigator)
            val className = a.getString(R.styleable.DialogFragmentNavigator_android_name)
            if (className != null) {
                setClassName(className)
            }
            a.recycle()
        }

        fun setClassName(className: String): Destination {
            mClassName = className
            return this
        }

        val className: String
            get() = if (mClassName == null) {
                throw IllegalStateException("DialogFragment class was not set")
            } else {
                mClassName!!
            }
    }

    companion object {
        private const val TAG = "DialogFragmentNavigator"
        private const val KEY_DIALOG_COUNT = "androidx-nav-dialogfragment:navigator:count"
        private const val DIALOG_TAG = "androidx-nav-fragment:navigator:dialog:"
    }
}