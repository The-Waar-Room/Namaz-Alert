package com.sudoajay.triumph_path.ui.splashScreen

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.databinding.ActivitySplashScreenBinding
import com.sudoajay.triumph_path.ui.BaseActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.WindowInsetsControllerCompat
import com.sudoajay.triumph_path.ui.mainActivity.MainActivity

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private var isFirstAnimation = false

    private var isDarkTheme: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isDarkTheme = isSystemDefaultOn(resources)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    true
            }
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        val logoIcon = BitmapFactory.decodeResource(resources, R.drawable.app_icon)

        /*Simple hold animation to hold ImageView in the centre of the screen at a slightly larger
      scale than the ImageView's original one.*/
        val hold = AnimationUtils.loadAnimation(this, R.anim.hold_splash)

        /* Translates ImageView from current position to its original position, scales it from
        larger scale to its original scale.*/
        val translateScale = AnimationUtils.loadAnimation(this, R.anim.translate_scale_splash)
        val translateText = AnimationUtils.loadAnimation(this, R.anim.annimation_text)


        binding.imageView.startAnimation(hold)

        hold.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                binding.imageView.clearAnimation()
                binding.imageView.startAnimation(translateScale)

            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        translateScale.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {

                    binding.textView.clearAnimation()
                    binding.textView.startAnimation(translateText)
                    binding.textView.visibility = View.VISIBLE



            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        translateText.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (!isFirstAnimation) {
                    binding.textView.clearAnimation()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                isFirstAnimation = true
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}