package com.example.gallery.base.ui.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDestination.ClassType;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.Navigator.Name;
import androidx.navigation.NavigatorProvider;
import com.example.gallery.R;

import java.util.*;
import java.util.Map.Entry;

@Name("fragment")
public class FragmentNavigator extends Navigator<FragmentNavigator.Destination> {
    private static final String TAG = "FragmentNavigator";
    private static final String KEY_BACK_STACK_IDS = "androidx-nav-fragment:navigator:backStackIds";
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final int mContainerId;
    private ArrayDeque<Integer> mBackStack = new ArrayDeque();

    public FragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        this.mContext = context;
        this.mFragmentManager = manager;
        this.mContainerId = containerId;
    }

    public boolean popBackStack() {
        if (this.mBackStack.isEmpty()) {
            return false;
        } else if (this.mFragmentManager.isStateSaved()) {
            Log.i("FragmentNavigator", "Ignoring popBackStack() call: FragmentManager has already saved its state");
            return false;
        } else {
            this.mFragmentManager.popBackStack(this.generateBackStackName(this.mBackStack.size(), (Integer)this.mBackStack.peekLast()), 1);
            int removeIndex = this.mBackStack.size() - 1;
            if (removeIndex >= this.mFragmentManager.getFragments().size()) {
                removeIndex = this.mFragmentManager.getFragments().size() - 1;
            }

            this.mFragmentManager.getFragments().remove(removeIndex);
            this.mBackStack.removeLast();
            return true;
        }
    }

    @NonNull
    public Destination createDestination() {
        return new Destination(this);
    }

    /** @deprecated */
    @Deprecated
    @NonNull
    public Fragment instantiateFragment(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull String className, @Nullable Bundle args) {
        return fragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), className);
    }

    @Nullable
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        if (this.mFragmentManager.isStateSaved()) {
            Log.i("FragmentNavigator", "Ignoring navigate() call: FragmentManager has already saved its state");
            return null;
        } else {
            String className = destination.getClassName();
            if (className.charAt(0) == '.') {
                className = this.mContext.getPackageName() + className;
            }

            Fragment frag = this.instantiateFragment(this.mContext, this.mFragmentManager, className, args);
            frag.setArguments(args);
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
            int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
            int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
            int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
            if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
                enterAnim = enterAnim != -1 ? enterAnim : 0;
                exitAnim = exitAnim != -1 ? exitAnim : 0;
                popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
                popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
                ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
            }

            Log.d("Nav", " --mBackStack.size:" + this.mBackStack.size() + " getFragments().size():" + this.mFragmentManager.getFragments().size());
            if (this.mBackStack.size() > 0 && this.mFragmentManager.getFragments().size() > 0) {
                Log.d("Nav", " --Hide --Add");
                ft.hide((Fragment)this.mFragmentManager.getFragments().get(this.mBackStack.size() - 1));
                ft.add(this.mContainerId, frag);
            } else {
                Log.d("Nav", " --Replace");
                ft.replace(this.mContainerId, frag);
            }

            ft.setPrimaryNavigationFragment(frag);
            int destId = destination.getId();
            boolean initialNavigation = this.mBackStack.isEmpty();
            boolean isSingleTopReplacement = navOptions != null && !initialNavigation && navOptions.shouldLaunchSingleTop() && (Integer)this.mBackStack.peekLast() == destId;
            boolean isAdded;
            if (initialNavigation) {
                isAdded = true;
            } else if (isSingleTopReplacement) {
                if (this.mBackStack.size() > 1) {
                    this.mFragmentManager.popBackStack(this.generateBackStackName(this.mBackStack.size(), (Integer)this.mBackStack.peekLast()), 1);
                    ft.addToBackStack(this.generateBackStackName(this.mBackStack.size(), destId));
                }

                isAdded = false;
            } else {
                ft.addToBackStack(this.generateBackStackName(this.mBackStack.size() + 1, destId));
                isAdded = true;
            }

            if (navigatorExtras instanceof Extras) {
                Extras extras = (Extras)navigatorExtras;
                Iterator var17 = extras.getSharedElements().entrySet().iterator();

                while(var17.hasNext()) {
                    Entry<View, String> sharedElement = (Entry)var17.next();
                    ft.addSharedElement((View)sharedElement.getKey(), (String)sharedElement.getValue());
                }
            }

            ft.setReorderingAllowed(true);
            ft.commit();
            if (isAdded) {
                this.mBackStack.add(destId);
                return destination;
            } else {
                return null;
            }
        }
    }

    @Nullable
    public Bundle onSaveState() {
        Bundle b = new Bundle();
        int[] backStack = new int[this.mBackStack.size()];
        int index = 0;

        Integer id;
        for(Iterator var4 = this.mBackStack.iterator(); var4.hasNext(); backStack[index++] = id) {
            id = (Integer)var4.next();
        }

        b.putIntArray("androidx-nav-fragment:navigator:backStackIds", backStack);
        return b;
    }

    public void onRestoreState(@Nullable Bundle savedState) {
        if (savedState != null) {
            int[] backStack = savedState.getIntArray("androidx-nav-fragment:navigator:backStackIds");
            if (backStack != null) {
                this.mBackStack.clear();
                int[] var3 = backStack;
                int var4 = backStack.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    int destId = var3[var5];
                    this.mBackStack.add(destId);
                }
            }
        }

    }

    @NonNull
    private String generateBackStackName(int backStackIndex, int destId) {
        return backStackIndex + "-" + destId;
    }

    private int getDestId(@Nullable String backStackName) {
        String[] split = backStackName != null ? backStackName.split("-") : new String[0];
        if (split.length != 2) {
            throw new IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.");
        } else {
            try {
                Integer.parseInt(split[0]);
                return Integer.parseInt(split[1]);
            } catch (NumberFormatException var4) {
                throw new IllegalStateException("Invalid back stack entry on the NavHostFragment's back stack - use getChildFragmentManager() if you need to do custom FragmentTransactions from within Fragments created via your navigation graph.");
            }
        }
    }

    public static final class Extras implements Navigator.Extras {
        private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap();

        Extras(Map<View, String> sharedElements) {
            this.mSharedElements.putAll(sharedElements);
        }

        @NonNull
        public Map<View, String> getSharedElements() {
            return Collections.unmodifiableMap(this.mSharedElements);
        }

        public static final class Builder {
            private final LinkedHashMap<View, String> mSharedElements = new LinkedHashMap();

            public Builder() {
            }

            @NonNull
            public Builder addSharedElements(@NonNull Map<View, String> sharedElements) {
                Iterator var2 = sharedElements.entrySet().iterator();

                while(var2.hasNext()) {
                    Entry<View, String> sharedElement = (Entry)var2.next();
                    View view = (View)sharedElement.getKey();
                    String name = (String)sharedElement.getValue();
                    if (view != null && name != null) {
                        this.addSharedElement(view, name);
                    }
                }

                return this;
            }

            @NonNull
            public Builder addSharedElement(@NonNull View sharedElement, @NonNull String name) {
                this.mSharedElements.put(sharedElement, name);
                return this;
            }

            @NonNull
            public Extras build() {
                return new Extras(this.mSharedElements);
            }
        }
    }

    @ClassType(Fragment.class)
    public static class Destination extends NavDestination {
        private String mClassName;

        public Destination(@NonNull NavigatorProvider navigatorProvider) {
            this(navigatorProvider.getNavigator(FragmentNavigator.class));
        }

        public Destination(@NonNull Navigator<? extends Destination> fragmentNavigator) {
            super(fragmentNavigator);
        }

        @CallSuper
        public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.FragmentNavigator);
            String className = a.getString(R.styleable.FragmentNavigator_android_name);
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
                throw new IllegalStateException("Fragment class was not set");
            } else {
                return this.mClassName;
            }
        }

        @NonNull
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append(" class=");
            if (this.mClassName == null) {
                sb.append("null");
            } else {
                sb.append(this.mClassName);
            }

            return sb.toString();
        }
    }
}
