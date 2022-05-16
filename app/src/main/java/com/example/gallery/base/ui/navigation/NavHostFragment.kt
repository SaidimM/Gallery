package com.example.gallery.base.ui.navigation

import android.content.Context
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavController
import androidx.annotation.CallSuper
import android.os.Bundle
import androidx.navigation.Navigator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import com.example.gallery.R
import androidx.navigation.Navigation
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import kotlin.jvm.JvmOverloads
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import java.lang.IllegalStateException

class NavHostFragment : Fragment(), NavHost {
    private var mNavController: NavHostController? = null
    private var mIsPrimaryBeforeOnCreate: Boolean? = null
    private var mGraphId = 0
    private var mDefaultNavHost = false
    override fun getNavController(): NavController {
        return if (mNavController == null) {
            throw IllegalStateException("NavController is not available before onCreate()")
        } else {
            mNavController!!
        }
    }

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mDefaultNavHost) {
            this.parentFragmentManager.beginTransaction().setPrimaryNavigationFragment(this).commit()
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = requireContext()
        mNavController = NavHostController(context)
        mNavController!!.setLifecycleOwner(this)
        mNavController!!.setOnBackPressedDispatcher(requireActivity().onBackPressedDispatcher)
        mNavController!!.enableOnBackPressed(mIsPrimaryBeforeOnCreate != null && mIsPrimaryBeforeOnCreate!!)
        mIsPrimaryBeforeOnCreate = null
        mNavController!!.setViewModelStore(this.viewModelStore)
        onCreateNavController(mNavController!!)
        var navState: Bundle? = null
        if (savedInstanceState != null) {
            navState = savedInstanceState.getBundle("android-support-nav:fragment:navControllerState")
            if (savedInstanceState.getBoolean("android-support-nav:fragment:defaultHost", false)) {
                mDefaultNavHost = true
                this.parentFragmentManager.beginTransaction().setPrimaryNavigationFragment(this).commit()
            }
            mGraphId = savedInstanceState.getInt("android-support-nav:fragment:graphId")
        }
        if (navState != null) {
            mNavController!!.restoreState(navState)
        }
        if (mGraphId != 0) {
            mNavController!!.setGraph(mGraphId)
        } else {
            val args = this.arguments
            val graphId = args?.getInt("android-support-nav:fragment:graphId") ?: 0
            val startDestinationArgs = args?.getBundle("android-support-nav:fragment:startDestinationArgs")
            if (graphId != 0) {
                mNavController!!.setGraph(graphId, startDestinationArgs)
            }
        }
    }

    @CallSuper
    protected fun onCreateNavController(navController: NavController) {
        navController.navigatorProvider.addNavigator(
            DialogFragmentNavigator(
                requireContext(), this.childFragmentManager
            )
        )
        navController.navigatorProvider.addNavigator(createFragmentNavigator())
    }

    @CallSuper
    override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
        if (mNavController != null) {
            mNavController!!.enableOnBackPressed(isPrimaryNavigationFragment)
        } else {
            mIsPrimaryBeforeOnCreate = isPrimaryNavigationFragment
        }
    }

    @Deprecated("")
    protected fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination?> {
        return FragmentNavigator(requireContext(), this.childFragmentManager, containerId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val containerView = FragmentContainerView(inflater.context)
        containerView.id = containerId
        return containerView
    }

    private val containerId: Int
        private get() {
            val id = this.id
            return if (id != 0 && id != -1) id else R.id.nav_host_fragment_container
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        check(view is ViewGroup) { "created host view $view is not a ViewGroup" }
        Navigation.setViewNavController(view, mNavController)
        if (view.getParent() != null) {
            val rootView = view.getParent() as View
            if (rootView.id == this.id) {
                Navigation.setViewNavController(rootView, mNavController)
            }
        }
    }

    @CallSuper
    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val navHost = context.obtainStyledAttributes(attrs, R.styleable.NavHost)
        val graphId = navHost.getResourceId(R.styleable.NavHost_navGraph, 0)
        if (graphId != 0) {
            mGraphId = graphId
        }
        navHost.recycle()
        val a = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment)
        val defaultHost = a.getBoolean(R.styleable.NavHostFragment_defaultNavHost, false)
        if (defaultHost) {
            mDefaultNavHost = true
        }
        a.recycle()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val navState = mNavController!!.saveState()
        if (navState != null) {
            outState.putBundle("android-support-nav:fragment:navControllerState", navState)
        }
        if (mDefaultNavHost) {
            outState.putBoolean("android-support-nav:fragment:defaultHost", true)
        }
        if (mGraphId != 0) {
            outState.putInt("android-support-nav:fragment:graphId", mGraphId)
        }
    }

    companion object {
        private const val KEY_GRAPH_ID = "android-support-nav:fragment:graphId"
        private const val KEY_START_DESTINATION_ARGS = "android-support-nav:fragment:startDestinationArgs"
        private const val KEY_NAV_CONTROLLER_STATE = "android-support-nav:fragment:navControllerState"
        private const val KEY_DEFAULT_NAV_HOST = "android-support-nav:fragment:defaultHost"
        fun findNavController(fragment: Fragment): NavController {
            var findFragment: Fragment? = fragment
            while (findFragment != null) {
                if (findFragment is NavHostFragment) {
                    return findFragment.navController
                }
                val primaryNavFragment = findFragment.parentFragmentManager.primaryNavigationFragment
                if (primaryNavFragment is NavHostFragment) {
                    return primaryNavFragment.navController
                }
                findFragment = findFragment.parentFragment
            }
            val view = fragment.view
            return if (view != null) {
                Navigation.findNavController(view)
            } else {
                throw IllegalStateException("Fragment $fragment does not have a NavController set")
            }
        }

        @JvmOverloads
        fun create(@NavigationRes graphResId: Int, startDestinationArgs: Bundle? = null): NavHostFragment {
            var b: Bundle? = null
            if (graphResId != 0) {
                b = Bundle()
                b.putInt("android-support-nav:fragment:graphId", graphResId)
            }
            if (startDestinationArgs != null) {
                if (b == null) {
                    b = Bundle()
                }
                b.putBundle("android-support-nav:fragment:startDestinationArgs", startDestinationArgs)
            }
            val result = NavHostFragment()
            if (b != null) {
                result.arguments = b
            }
            return result
        }
    }
}