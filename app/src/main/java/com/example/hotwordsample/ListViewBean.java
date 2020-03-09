package com.example.hotwordsample;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

class ListViewBean {
    private Drawable drawable;
    private String BeanName;

    public ListViewBean(Drawable drawable, String beanName) {
        this.drawable = drawable;
        BeanName = beanName;
    }

    public String getBeanName() {
        return BeanName;
    }

    public void setBeanName(String beanName) {
        BeanName = beanName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
