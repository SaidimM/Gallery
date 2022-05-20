package com.example.gallery.base.ui.navigation

import android.content.Context
import androidx.navigation.Navigator
import android.os.Bundle
import androidx.navigation.NavOptions
import androidx.navigation.NavDestination
import androidx.navigation.NavigatorProvider
import androidx.annotation.CallSuper
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gallery.R
import java.lang.IllegalStateException
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.util.*

@Navigator.Name("fragment")
class FragmentNavigator(
    private val mContext: Context,
    private val mFragmentManager: FragmentManager,
    private val mContainerId: Int
) : Navigator<FragmentNavigator.Destination>() {
    private val mBackStack: ArrayDeque<Int> = ArrayDeque<Int>()
    override fun popBackStack(): Boolean {
        return if (mBackStack.isEmpty()) {
            false
        } else if (mFragmentManager.isStateSaved) {
            Log.i(
                "FragmentNavigator",
                "Ignoring popBackStack() call: FragmentManager has already saved its state"
            )
            false
        } else {
            mFragmentManager.popBackStack(
                generateBackStackName(
                    mBackStack.size,
                    mBackStack.peekLast()!!
                ), 1
            )
            var removeIndex = mBackStack.size - 1
            if (removeIndex >= mFragmentManager.fragments.size) {
                removeIndex = mFragmentManager.fragments.size - 1
            }
            mFragmentManager.fragments.removeAt(removeIndex)
            mBackStack.removeLast()
            true
        }
    }

    override fun createDestination(): Destination {
        return Destination(this)
    }

    @Deprecated("")
    fun instantiateFragment(
        context: Context,
        fragmentManager: FragmentManager,
        className: String,
        args: Bundle?
    ): Fragment {
        return fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
    }

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        return if (mFragmentManager.isStateSaved) {
            Log.i("FragmentNavigator", "Ignoring navigate() call: FragmentManager has already saved its state")
            null
        } else {
            var className = destination.className
            if (className[0] == '.') {
                className = mContext.packageName + className
            }
            val frag = instantiateFragment(mContext, mFragmentManager, className, args)
            frag.arguments = args
            val ft = mFragmentManager.beginTransaction()
            var enterAnim = navOptions?.enterAnim ?: -1
            var exitAnim = navOptions?.exitAnim ?: -1
            var popEnterAnim = navOptions?.popEnterAnim ?: -1
            var popExitAnim = navOptions?.popExitAnim ?: -1
            if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
                enterAnim = if (enterAnim != -1) enterAnim else 0
                exitAnim = if (exitAnim != -1) exitAnim else 0
                popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
                popExitAnim = if (popExitAnim != -1) popExitAnim else 0
                ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
            }
            Log.d(
                "Nav",
                " --mBackStack.size:" + mBackStack.size + " getFragments().size():" + mFragmentManager.fragments.size
            )
            if (mBackStack.size > 0 && mFragmentManager.fragments.size > 0) {
                Log.d("Nav", " --Hide --Add")
                ft.hide((mFragmentManager.fragments[mBackStack.size - 1] as Fragment))
                ft.add(mContainerId, frag)
            } else {
                Log.d("Nav", " --Replace")
                ft.replace(mContainerId, frag)
            }
            ft.setPrimaryNavigationFragment(frag)
            val destId = destination.id
            val initialNavigation = mBackStack.isEmpty()
            val isSingleTopReplacement =
                navOptions != null && !initialNavigation && navOptions.shouldLaunchSingleTop() && mBackStack.peekLast() == destId
            val isAdded: Boolean
            isAdded = if (initialNavigation) {
                true
            } else if (isSingleTopReplacement) {
                if (mBackStack.size > 1) {
                    mFragmentManager.popBackStack(
                        generateBackStackName(
                            mBackStack.size,
                            mBackStack.peekLast()!!
                        ), 1
                    )
                    ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
                }
                false
            } else {
                ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
                true
            }
            if (navigatorExtras is Extras) {
                val var17: Iterator<*> = navigatorExtras.sharedElements.entries.iterator()
                while (var17.hasNext()) {
                    val (key, value) = var17.next() as Map.Entry<*, *>
                    ft.addSharedElement((key as View), (value as String))
                }
            }
            ft.setReorderingAllowed(true)
            ft.commit()
            if (isAdded) {
                mBackStack.add(destId)
                destination
            } else {
                null
            }
        }
    }

    override fun onSaveState(): Bundle {
        val b = Bundle()
        val backStack = IntArray(mBackStack.size)
        var index = 0
        var id: Int
        val var4: Iterator<*> = mBackStack.iterator()
        while (var4.hasNext()) {
            id = var4.next() as Int
            backStack[index++] = id
        }
        b.putIntArray("androidx-nav-fragment:navigator:backStackIds", backStack)
        return b
    }

    override fun onRestoreState(savedState: Bundle) {
        val backStack = savedState.getIntArray("androidx-nav-fragment:navigator:backStackIds")
        if (backStack != null) {
            mBackStack.clear()
            val var3: IntArray = backStack
            val var4 = backStack.size
            for (var5 in 0 until var4) {
                val destId = var3[var5]
                mBackStack.add(destId)
            }
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }

    private fun getDestId(backStackName: String): Int {
        val split: Array<String> = backStackName.split("-").toTypedArray()
        return if (split.size != 2) {
            throw IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.")
        } else {
            try {
                split[0].toInt()
                split[1].toInt()
            } catch (var4: NumberFormatException) {
                throw IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.")
            }
        }
    }

    class Extras internal constructor(sharedElements: Map<View?, String?>?) : Navigator.Extras {
        private val mSharedElements: LinkedHashMap<View?, String?> = LinkedHashMap<View?, String?>()
        val sharedElements: Map<View?, String?>
            get() = Collections.unmodifiableMap(mSharedElements)

        class Builder {
            private val mSharedElements: LinkedHashMap<View?, String?> = LinkedHashMap<View?, String?>()
            fun addSharedElements(sharedElements: Map<View?, String?>): Builder {
                val var2: Iterator<*> = sharedElements.entries.iterator()
                while (var2.hasNext()) {
                    val (key, value) = var2.next() as Map.Entry<*, *>
                    val view = key as View
                    val name = value as String
                    addSharedElement(view, name)
                }
                return this
            }

            fun addSharedElement(sharedElement: View, name: String): Builder {
                mSharedElements[sharedElement] = name
                return this
            }

            fun build(): Extras {
                return Extras(mSharedElements)
            }
        }

        init {
            mSharedElements.putAll(sharedElements!!)
        }
    }

    @NavDestination.ClassType(Fragment::class)
    class Destination(fragmentNavigator: Navigator<out Destination?>) : NavDestination(fragmentNavigator) {
        private var mClassName: String? = null

        constructor(navigatorProvider: NavigatorProvider) : this(
            navigatorProvider.getNavigator<FragmentNavigator>(
                FragmentNavigator::class.java
            )
        ) {
        }

        @CallSuper
        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)
            val a = context.resources.obtainAttributes(attrs, R.styleable.FragmentNavigator)
            val className = a.getString(R.styleable.FragmentNavigator_android_name)
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
                throw IllegalStateException("Fragment class was not set")
            } else {
                mClassName!!
            }

        override fun toString(): String {
            val sb = StringBuilder()
            sb.append(super.toString())
            sb.append(" class=")
            if (mClassName == null) {
                sb.append("null")
            } else {
                sb.append(mClassName)
            }
            return sb.toString()
        }
    }

    companion object {
        private const val TAG = "FragmentNavigator"
        private const val KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds"
    }
}