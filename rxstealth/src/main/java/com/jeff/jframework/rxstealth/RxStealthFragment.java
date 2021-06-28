package com.jeff.jframework.rxstealth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

/**
 * 使用一个不可见的Fragment实现：
 * 1.权限的请求
 * 2.startActivityForResult页面跳转，链式调用
 * 3.监听Activity或Fragment的生命周期
 * <p>
 * @author Jeff
 * @date 2020/04/01 17:53
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class RxStealthFragment extends AbstractStealthFragment {
    private static final String TAG = "RxStealthFragment";

    /**
     * Contains all the current permission requests.
     * Once granted or denied, they are removed from it.
     */
    private Map<String, PublishSubject<Permission>> mPermissionSubjects = new HashMap<>();

    private Map<Integer, PublishSubject<ActivityResult>> mResultSubjects = new HashMap<>();

    public RxStealthFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            // Find the corresponding subject
            PublishSubject<Permission> subject = mPermissionSubjects.get(permissions[i]);
            if (subject == null) {
                // No subject found
                Log.e(TAG, "RxFragment.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                return;
            }
            mPermissionSubjects.remove(permissions[i]);
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            subject.onNext(new Permission(permissions[i], granted, shouldShowRequestPermissionRationale(permissions[i])));
            subject.onComplete();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PublishSubject<ActivityResult> subject = mResultSubjects.get(requestCode);
        if (subject == null) {
            // No subject found
            Log.e(TAG, "RxFragment.onActivityResult invoked but didn't find the corresponding activity request.");
            return;
        }
        mResultSubjects.remove(requestCode);
        subject.onNext(new ActivityResult(requestCode, resultCode, data));
        subject.onComplete();
    }

    public PublishSubject<ActivityResult> getSubjectByRequestCode(@NonNull int requestCode) {
        return mResultSubjects.get(requestCode);
    }

    public boolean containsByRequestCode(@NonNull int requestCode) {
        return mResultSubjects.containsKey(requestCode);
    }

    public void setSubjectForResult(@NonNull int requestCode, @NonNull PublishSubject<ActivityResult> subject) {
        mResultSubjects.put(requestCode, subject);
    }

    public PublishSubject<Permission> getSubjectByPermission(@NonNull String permission) {
        return mPermissionSubjects.get(permission);
    }

    public boolean containsByPermission(@NonNull String permission) {
        return mPermissionSubjects.containsKey(permission);
    }

    public void setSubjectForPermission(@NonNull String permission, @NonNull PublishSubject<Permission> subject) {
        mPermissionSubjects.put(permission, subject);
    }
}
