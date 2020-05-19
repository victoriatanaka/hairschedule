package com.example.cronogramacapilar.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;

import androidx.annotation.ColorRes;
import androidx.core.graphics.drawable.DrawableCompat;

public final class MenuHelper {

    private MenuHelper() {

    }

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        item.setVisible(true);
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));
        item.setIcon(wrapDrawable);
    }
}
