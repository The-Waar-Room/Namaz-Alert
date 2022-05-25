package com.sudoajay.namaz_alert.ui.language.repository

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.HolderSelectLanguageBinding
import javax.inject.Inject

class SelectLanguageAdapter @Inject constructor() :
    RecyclerView.Adapter<SelectLanguageAdapter.ViewHolder>() {

    var languageList = mutableListOf<String>()
    var languageValue = mutableListOf<String>()


    inner class ViewHolder(
        val binding: HolderSelectLanguageBinding? = null,
        val context: Context
    ) : RecyclerView.ViewHolder(binding?.root!!) {
        fun bind() {

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            binding = HolderSelectLanguageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            context = parent.context
        )

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val text = languageList[position]
        val value = languageValue[position]

        viewHolder.binding!!.selectLanguageSameTextView.text = value
        viewHolder.binding.selectLanguageDifferentTextView.text =
            viewHolder.context.getString(R.string.select_language_bracket, text)
        Log.e("SelectLanguageTAG", "  value - $value and text - $text")


        Log.e("SelectLanguageTAG", "  inside value - $value and text - $text")
        viewHolder.binding.tickImageView.visibility =
            if (value == "Arabic") View.VISIBLE else View.GONE


    }


    override fun getItemCount() = languageList.size


}