package com.jeff.jframework.rxstealth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeff.jframework.core.Preconditions;


/**
 * 抽象隐身Fragment，用于监听生命周期、请求权限、activity跳转等
 * <p>
 *
 * @author Jeff
 * @date 2020/7/3
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public abstract class AbstractStealthFragment extends Fragment {
    private static final String TAG = "StealthFragment";

    private static final int PERMISSIONS_REQUEST_CODE = 42;

    private OnActivityResultCallback onActivityResultCallback;
    private IActivityLifecycle iActivityLifecycle;

    private boolean mLogging;

    public AbstractStealthFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        log("onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        log("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log("onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log("onActivityCreated");
        if (iActivityLifecycle != null) {
            iActivityLifecycle.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        log("onStart");
        if (iActivityLifecycle != null) {
            iActivityLifecycle.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        log("onResume");
        if (iActivityLifecycle != null) {
            iActivityLifecycle.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        log("onPause");
        if (iActivityLifecycle != null) {
            iActivityLifecycle.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        log("onStop");
        if (iActivityLifecycle != null) {
            iActivityLifecycle.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        log("onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        if (iActivityLifecycle != null) {
            iActivityLifecycle.onDestroy();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        log("onDetach");
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(@NonNull String[] permissions) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSIONS_REQUEST_CODE) return;
        onRequestPermissionsResult(permissions, grantResults);
    }

    protected abstract void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults);

    @TargetApi(Build.VERSION_CODES.M)
    public boolean isGranted(String permission) {
        final Activity activity = getActivity();
        Preconditions.checkState(activity != null, "This fragment must be attached to an activity.");
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean isRevoked(String permission) {
        final Activity activity = getActivity();
        Preconditions.checkState(activity != null, "This fragment must be attached to an activity.");
        return activity.getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    public void startActivityForResult(Intent intent, int requestCode, OnActivityResultCallback callback) {
        onActivityResultCallback = callback;
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (onActivityResultCallback != null) {
            onActivityResultCallback.callback(requestCode, resultCode, data);
        }
    }

    public void watchActivity(IActivityLifecycle lifecycle) {
        iActivityLifecycle = lifecycle;
    }

    public void setLogging(boolean logging) {
        mLogging = logging;
    }

    public void log(String message) {
        if (mLogging) {
            Log.d(TAG, message);
        }
    }

}
