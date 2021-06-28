package com.jeff.jframework.rxstealth;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jeff.jframework.core.Preconditions;


/**
 * 抽象影子类
 * <p>
 *
 * @author Jeff
 * @date 2020/7/3
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class AbstractStealth<F extends AbstractStealthFragment> {

    protected abstract String getHelperTag();

    @VisibleForTesting
    protected Lazy<F> mFragment;

    public AbstractStealth(@NonNull final Activity activity) {
        mFragment = getLazySingleton(activity.getFragmentManager());
    }

    public AbstractStealth(@NonNull final Fragment fragment) {
        mFragment = getLazySingleton(fragment.getChildFragmentManager());
    }

    protected abstract F createFragment();

    @NonNull
    private Lazy<F> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new Lazy<F>() {

            private F fragment;

            @Override
            public synchronized F get() {
                if (fragment == null) {
                    fragment = getStealthFragment(fragmentManager);
                }
                return fragment;
            }
        };
    }

    private F getStealthFragment(@NonNull final FragmentManager fragmentManager) {
        F fragment = findStealthFragment(fragmentManager);
        boolean isNewInstance = fragment == null;
        if (isNewInstance) {
            fragment = createFragment();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, getHelperTag())
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    private F findStealthFragment(@NonNull final FragmentManager fragmentManager) {
        return (F) fragmentManager.findFragmentByTag(getHelperTag());
    }

    public AbstractStealth setLogging(boolean logging) {
        getFragment().setLogging(logging);
        return this;
    }

    protected F getFragment() {
        if (mFragment == null) return null;
        return mFragment.get();
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isGranted(String permission) {
        return !isMarshmallow() ||getFragment().isGranted(permission);
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isRevoked(String permission) {
        return isMarshmallow() &&getFragment().isRevoked(permission);
    }

    public boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /******************************************************/
    /*                       跳转                          */
    /******************************************************/

    public void startActivityForResult(Intent intent, int requestCode) {
        getFragment().log("startActivityForResult: requestCode= " + requestCode + ", intent= " + intent);
        getFragment().startActivityForResult(intent, requestCode);
    }
    /**
     * 带回调接口的跳转
     *
     * @param intent
     * @param requestCode
     * @param callback
     */
    public void startActivityForResult(Intent intent, int requestCode, OnActivityResultCallback callback) {
        Preconditions.checkArgument(intent != null, "LiveLifecycle.request/startActivityForResult requires intent!=null");
        getFragment().log("startActivityForResult: requestCode= " + requestCode + ", intent= " + intent);
        getFragment().startActivityForResult(intent, requestCode, callback);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getFragment().onActivityResult(requestCode, resultCode, data);
    }

    /******************************************************/
    /*                    生命周期                          */
    /******************************************************/

    public AbstractStealth<F> watchActivity(IActivityLifecycle lifecycle) {
        getFragment().watchActivity(lifecycle);
        return this;
    }
}
