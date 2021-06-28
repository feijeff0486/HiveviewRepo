package com.jeff.jframework.rxstealth;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * #startActivityForResult返回的结果
 * <p>
 * @author Jeff
 * @date 2019/12/19 17:44
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public class ActivityResult implements Parcelable {
    private int requestCode;
    private int resultCode;
    private Intent data;

    public ActivityResult() {
    }

    public ActivityResult(int requestCode) {
        this.requestCode = requestCode;
    }

    public ActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.requestCode);
        dest.writeInt(this.resultCode);
        dest.writeParcelable(this.data, flags);
    }

    protected ActivityResult(Parcel in) {
        this.requestCode = in.readInt();
        this.resultCode = in.readInt();
        this.data = in.readParcelable(Intent.class.getClassLoader());
    }

    public static final Creator<ActivityResult> CREATOR = new Creator<ActivityResult>() {
        @Override
        public ActivityResult createFromParcel(Parcel source) {
            return new ActivityResult(source);
        }

        @Override
        public ActivityResult[] newArray(int size) {
            return new ActivityResult[size];
        }
    };

    @Override
    public String toString() {
        return "ActivityResult{" +
                "requestCode=" + requestCode +
                ", resultCode=" + resultCode +
                ", data=" + data +
                '}';
    }
}
