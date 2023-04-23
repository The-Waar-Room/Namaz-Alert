package com.sudoajay.triumph_path.util

import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter("app:setVisibility")
fun setVisibility(view: View, v: Boolean?) {
    view.show(v == true)
}