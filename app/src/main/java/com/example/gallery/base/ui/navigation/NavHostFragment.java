package com.example.gallery.base.ui.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.*;
import com.example.gallery.R;

public class NavHostFragment extends Fragment implements NavHost {
    private static final String KEY_GRAPH_ID = "android-support-nav:fragment:graphId";
    private static final String KEY_START_DESTINATION_ARGS = "android-support-nav:fragment:startDestinationArgs";
    private static final String KEY_NAV_CONTROLLER_STATE = "android-support-nav:fragment:navControllerState";
    private static final String KEY_DEFAULT_NAV_HOST = "android-support-nav:fragment:defaultHost";
    private NavHostController mNavController;
    private Boolean mIsPrimaryBeforeOnCreate = null;
    private int mGraphId;
    private boolean mDefaultNavHost;

    public NavHostFragment() {
    }

    @NonNull
    public static NavController findNavController(@NonNull Fragment fragment) {
        for (Fragment findFragment = fragment; findFragment != null; findFragment = findFragment.getParentFragment()) {
            if (findFragment instanceof NavHostFragment) {
                return ((NavHostFragment) findFragment).getNavController();
            }

            Fragment primaryNavFragment = findFragment.getParentFragmentManager().getPrimaryNavigationFragment();
            if (primaryNavFragment instanceof NavHostFragment) {
                return ((NavHostFragment) primaryNavFragment).getNavController();
            }
        }

        View view = fragment.getView();
        if (view != null) {
            return Navigation.findNavController(view);
        } else {
            throw new IllegalStateException("Fragment " + fragment + " does not have a NavController set");
        }
    }

    @NonNull
    public static NavHostFragment create(@NavigationRes int graphResId) {
        return create(graphResId, null);
    }

    @NonNull
    public static NavHostFragment create(@NavigationRes int graphResId, @Nullable Bundle startDestinationArgs) {
        Bundle b = null;
        if (graphResId != 0) {
            b = new Bundle();
            b.putInt("android-support-nav:fragment:graphId", graphResId);
        }

        if (startDestinationArgs != null) {
            if (b == null) {
                b = new Bundle();
            }

            b.putBundle("android-support-nav:fragment:startDestinationArgs", startDestinationArgs);
        }

        NavHostFragment result = new NavHostFragment();
        if (b != null) {
            result.setArguments(b);
        }

        return result;
    }

    @NonNull
    public final NavController getNavController() {
        if (this.mNavController == null) {
            throw new IllegalStateException("NavController is not available before onCreate()");
        } else {
            return this.mNavController;
        }
    }

    @CallSuper
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (this.mDefaultNavHost) {
            this.getParentFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
        }

    }

    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this.requireContext();
        this.mNavController = new NavHostController(context);
        this.mNavController.setLifecycleOwner(this);
        this.mNavController.setOnBackPressedDispatcher(this.requireActivity().getOnBackPressedDispatcher());
        this.mNavController.enableOnBackPressed(this.mIsPrimaryBeforeOnCreate != null && this.mIsPrimaryBeforeOnCreate);
        this.mIsPrimaryBeforeOnCreate = null;
        this.mNavController.setViewModelStore(this.getViewModelStore());
        this.onCreateNavController(this.mNavController);
        Bundle navState = null;
        if (savedInstanceState != null) {
            navState = savedInstanceState.getBundle("android-support-nav:fragment:navControllerState");
            if (savedInstanceState.getBoolean("android-support-nav:fragment:defaultHost", false)) {
                this.mDefaultNavHost = true;
                this.getParentFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
            }

            this.mGraphId = savedInstanceState.getInt("android-support-nav:fragment:graphId");
        }

        if (navState != null) {
            this.mNavController.restoreState(navState);
        }

        if (this.mGraphId != 0) {
            this.mNavController.setGraph(this.mGraphId);
        } else {
            Bundle args = this.getArguments();
            int graphId = args != null ? args.getInt("android-support-nav:fragment:graphId") : 0;
            Bundle startDestinationArgs = args != null ? args.getBundle("android-support-nav:fragment:startDestinationArgs") : null;
            if (graphId != 0) {
                this.mNavController.setGraph(graphId, startDestinationArgs);
            }
        }

    }

    @CallSuper
    protected void onCreateNavController(@NonNull NavController navController) {
        navController.getNavigatorProvider().addNavigator(new DialogFragmentNavigator(this.requireContext(), this.getChildFragmentManager()));
        navController.getNavigatorProvider().addNavigator(this.createFragmentNavigator());
    }

    @CallSuper
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        if (this.mNavController != null) {
            this.mNavController.enableOnBackPressed(isPrimaryNavigationFragment);
        } else {
            this.mIsPrimaryBeforeOnCreate = isPrimaryNavigationFragment;
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    @NonNull
    protected Navigator<? extends FragmentNavigator.Destination> createFragmentNavigator() {
        return new FragmentNavigator(this.requireContext(), this.getChildFragmentManager(), this.getContainerId());
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentContainerView containerView = new FragmentContainerView(inflater.getContext());
        containerView.setId(this.getContainerId());
        return containerView;
    }

    private int getContainerId() {
        int id = this.getId();
        return id != 0 && id != -1 ? id : R.id.nav_host_fragment_container;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!(view instanceof ViewGroup)) {
            throw new IllegalStateException("created host view " + view + " is not a ViewGroup");
        } else {
            Navigation.setViewNavController(view, this.mNavController);
            if (view.getParent() != null) {
                View rootView = (View) view.getParent();
                if (rootView.getId() == this.getId()) {
                    Navigation.setViewNavController(rootView, this.mNavController);
                }
            }

        }
    }

    @CallSuper
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray navHost = context.obtainStyledAttributes(attrs, R.styleable.NavHost);
        int graphId = navHost.getResourceId(R.styleable.NavHost_navGraph, 0);
        if (graphId != 0) {
            this.mGraphId = graphId;
        }

        navHost.recycle();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment);
        boolean defaultHost = a.getBoolean(R.styleable.NavHostFragment_defaultNavHost, false);
        if (defaultHost) {
            this.mDefaultNavHost = true;
        }

        a.recycle();
    }

    @CallSuper
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle navState = this.mNavController.saveState();
        if (navState != null) {
            outState.putBundle("android-support-nav:fragment:navControllerState", navState);
        }

        if (this.mDefaultNavHost) {
            outState.putBoolean("android-support-nav:fragment:defaultHost", true);
        }

        if (this.mGraphId != 0) {
            outState.putInt("android-support-nav:fragment:graphId", this.mGraphId);
        }

    }
}
