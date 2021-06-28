package com.jeff.jframework.rxstealth;

import com.jeff.jframework.core.CannotCreateException;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * 权限处理Helper类
 * <p>
 *
 * @author Jeff
 * @date 2020/9/2
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class PermissionHelper {
    PermissionHelper(){
        throw new CannotCreateException(this.getClass());
    }

    public static Permission combine(List<Permission> permissions) {
        return new Permission(combineName(permissions), combineGranted(permissions), combineShouldShowRequestPermissionRationale(permissions));
    }

    private static String combineName(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .map(new Function<Permission, String>() {
                    @Override
                    public String apply(Permission permission) throws Exception {
                        return permission.name;
                    }
                }).collectInto(new StringBuilder(), new BiConsumer<StringBuilder, String>() {
                    @Override
                    public void accept(StringBuilder s, String s2) throws Exception {
                        if (s.length() == 0) {
                            s.append(s2);
                        } else {
                            s.append(", ").append(s2);
                        }
                    }
                }).blockingGet().toString();
    }

    private static Boolean combineGranted(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .all(new Predicate<Permission>() {
                    @Override
                    public boolean test(Permission permission) throws Exception {
                        return permission.granted;
                    }
                }).blockingGet();
    }

    private static Boolean combineShouldShowRequestPermissionRationale(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .any(new Predicate<Permission>() {
                    @Override
                    public boolean test(Permission permission) throws Exception {
                        return permission.shouldShowRequestPermissionRationale;
                    }
                }).blockingGet();
    }
}
