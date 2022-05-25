package com.sudoajay.namaz_alert.util

import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter("app:setVisibility")
fun setVisibility(view: View, v: Boolean?) {
    view.show(v == true)
}