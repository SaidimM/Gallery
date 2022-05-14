package com.example.gallery.base.ui.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.*;
import androidx.navigation.NavDestination.ClassType;
import androidx.navigation.Navigator.Name;
import com.example.gallery.R;

@Name("dialog")
public final class DialogFragmentNavigator extends Navigator<DialogFragmentNavigator.Destination> {
    private static final String TAG = "DialogFragmentNavigator";
    private static final String KEY_DIALOG_COUNT = "androidx-nav-dialogfragment:navigator:count";
    private static final String DIALOG_TAG = "androidx-nav-fragment:navigator:dialog:";
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private int mDialogCount = 0;
    private LifecycleEventObserver mObserver = new LifecycleEventObserver() {
        public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Event event) {
            if (event == Event.ON_STOP) {
                DialogFragment dialogFragment = (DialogFragment)source;
                if (!dialogFragment.requireDialog().isShowing()) {
                    NavHostFragment.findNavController(dialogFragment).popBackStack();
                }
            }

        }
    };

    public DialogFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager) {
        this.mContext = context;
        this.mFragmentManager = manager;
    }

    public boolean popBackStack() {
        if (this.mDialogCount == 0) {
            return false;
        } else if (this.mFragmentManager.isStateSaved()) {
            Log.i("DialogFragmentNavigator", "Ignoring popBackStack() call: FragmentManager has already saved its state");
            return false;
        } else {
            Fragment existingFragment = this.mFragmentManager.findFragmentByTag("androidx-nav-fragment:navigator:dialog:" + --this.mDialogCount);
            if (existingFragment != null) {
                existingFragment.getLifecycle().removeObserver(this.mObserver);
                ((DialogFragment)existingFragment).dismiss();
            }

            return true;
        }
    }

    @NonNull
    public Destination createDestination() {
        return new Destination(this);
    }

    @Nullable
    public NavDestination navigate(@NonNull final Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Extras navigatorExtras) {
        if (this.mFragmentManager.isStateSaved()) {
            Log.i("DialogFragmentNavigator", "Ignoring navigate() call: FragmentManager has already saved its state");
            return null;
        } else {
            String className = destination.getClassName();
            if (className.charAt(0) == '.') {
                className = this.mContext.getPackageName() + className;
            }

            Fragment frag = this.mFragmentManager.getFragmentFactory().instantiate(this.mContext.getClassLoader(), className);
            if (!DialogFragment.class.isAssignableFrom(frag.getClass())) {
                throw new IllegalArgumentException("Dialog destination " + destination.getClassName() + " is not an instance of DialogFragment");
            } else {
                DialogFragment dialogFragment = (DialogFragment)frag;
                dialogFragment.setArguments(args);
                dialogFragment.getLifecycle().addObserver(this.mObserver);
                dialogFragment.show(this.mFragmentManager, "androidx-nav-fragment:navigator:dialog:" + this.mDialogCount++);
                return destination;
            }
        }
    }

    @Nullable
    public Bundle onSaveState() {
        if (this.mDialogCount == 0) {
            return null;
        } else {
            Bundle b = new Bundle();
            b.putInt("androidx-nav-dialogfragment:navigator:count", this.mDialogCount);
            return b;
        }
    }

    public void onRestoreState(@Nullable Bundle savedState) {
        if (savedState != null) {
            this.mDialogCount = savedState.getInt("androidx-nav-dialogfragment:navigator:count", 0);

            for(int index = 0; index < this.mDialogCount; ++index) {
                DialogFragment fragment = (DialogFragment)this.mFragmentManager.findFragmentByTag("androidx-nav-fragment:navigator:dialog:" + index);
                if (fragment == null) {
                    throw new IllegalStateException("DialogFragment " + index + " doesn't exist in the FragmentManager");
                }

                fragment.getLifecycle().addObserver(this.mObserver);
            }
        }

    }

    @ClassType(DialogFragment.class)
    public static class Destination extends NavDestination implements FloatingWindow {
        private String mClassName;

        public Destination(@NonNull NavigatorProvider navigatorProvider) {
            this(navigatorProvider.getNavigator(DialogFragmentNavigator.class));
        }

        public Destination(@NonNull Navigator<? extends Destination> fragmentNavigator) {
            super(fragmentNavigator);
        }

        @CallSuper
        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.DialogFragmentNavigator);
            String className = a.getString(R.styleable.DialogFragmentNavigator_android_name);
            if (className != null) {
                this.setClassName(className);
            }

            a.recycle();
        }

        @NonNull
        public final Destination setClassName(@NonNull String className) {
            this.mClassName = className;
            return this;
        }

        @NonNull
        public final String getClassName() {
            if (this.mClassName == null) {
                throw new IllegalStateException("DialogFragment class was not set");
            } else {
                return this.mClassName;
            }
        }
    }
}
