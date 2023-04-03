package com.sudoajay.namaz_alert.ui.rateUs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.fragment.app.DialogFragment
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.LayoutRateUsBinding
import com.sudoajay.namaz_alert.model.MessageType
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.messageType
import com.sudoajay.namaz_alert.ui.feedbackAndHelp.SendFeedbackAndHelp


class RateUsDialog : DialogFragment() {
    private lateinit var binding: LayoutRateUsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LayoutRateUsBinding.inflate(inflater, container, false)
        binding.dialog = this

        mainFun()

        return binding.root
    }

    private fun mainFun() { // Reference Object


        binding.rating.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (rating >= 4.5f) {
                    googlePlayStoreRating()
                } else {
                    below5StarRating()
                }
            }


    }

    private fun below5StarRating() {
        binding.headingTextView.visibility = View.GONE
        binding.belowStarHeadingTextView.visibility = View.GONE
        binding.view.visibility = View.GONE
        binding.rightHandSideMaterialButton.visibility = View.VISIBLE
        binding.belowStarHeading2TextView.visibility = View.VISIBLE
        binding.belowStarHeading2TextView.text = getString(R.string.lets_use_know_text)
        binding.leftHandSideTextView.text = getString(R.string.cancel_text)
        binding.rightHandSideMaterialButton.text = getString(R.string.ok_text)
        binding.rightHandSideMaterialButton.setOnClickListener {
            sendFeedback()
        }

    }

    private fun sendFeedback() {
        dismiss()
        startActivity(
            Intent(
                requireContext(),
                SendFeedbackAndHelp::class.java
            ).putExtra(messageType, MessageType.FeedBack.toString())
        )
    }


    private fun googlePlayStoreRating() {
        binding.headingTextView.visibility = View.GONE
        binding.belowStarHeadingTextView.visibility = View.VISIBLE
        binding.belowStarHeading2TextView.visibility = View.VISIBLE
        binding.view.visibility = View.VISIBLE
        binding.rightHandSideMaterialButton.visibility = View.VISIBLE

        binding.belowStarHeadingTextView.text = getString(R.string.thanks_so_much_text)
        binding.belowStarHeading2TextView.text = getString(R.string.would_you_like_text)
        binding.leftHandSideTextView.text = getString(R.string.no_thanks_text)
        binding.rightHandSideMaterialButton.text = getString(R.string.rate_text)

        binding.rightHandSideMaterialButton.setOnClickListener {
            openPlayStoreForRateUs()
        }


    }

    private fun openPlayStoreForRateUs() {
        val link = getString(R.string.rating_link_text)
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
        dismiss()
    }


    override fun onStart() { // This MUST be called first! Otherwise the view tweaking will not be present in the displayed Dialog (most likely overriden)
        super.onStart()
        forceWrapContent(this.view)
    }

    private fun forceWrapContent(v: View?) { // Start with the provided view
        var current = v
        val dm = requireContext().resources.displayMetrics
        val width = dm.widthPixels
        // Travel up the tree until fail, modifying the LayoutParams
        do { // Get the parent
            val parent = current!!.parent
            // Check if the parent exists
            if (parent != null) { // Get the view
                current = try {
                    parent as View
                } catch (e: ClassCastException) { // This will happen when at the top view, it cannot be cast to a View
                    break
                }
                // Modify the layout
                current!!.layoutParams.width = width - 15 * width / 100
            }
        } while (current!!.parent != null)
        // Request a layout to be re-done
        current!!.requestLayout()
    }


}