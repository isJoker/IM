package com.wjc.im.modul;

import android.graphics.Bitmap;

/**
 * Created by ${万嘉诚} on 2016/11/7.
 * WeChat：wjc398556712
 * Function：
 */

public class LoginEvent {

    private Bitmap bitmap;

    public LoginEvent(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
