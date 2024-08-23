package com.unoth.composition.presentation


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.unoth.composition.R
import com.unoth.composition.domain.entity.GameResult

interface OnOptionClickListener {
    fun onOptionClick(option: Int)
}

@SuppressLint("StringFormatMatches")
@BindingAdapter("requiredAnswers")
fun bindRequiredAnswers(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_score),
        count
    )
}

@SuppressLint("StringFormatMatches")
@BindingAdapter("scoreAnswers")
fun bindScoreAnswers(textView: TextView, score: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.score_answers),
        score
    )
}

@SuppressLint("StringFormatMatches")
@BindingAdapter("requiredPercentage")
fun bindRequiredPercentage(textView: TextView, percent: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.required_percentage),
        percent
    )
}

@SuppressLint("StringFormatMatches")
@BindingAdapter("scorePercentage")
fun bindScorePercentage(textView: TextView, gameResult: GameResult) {
    textView.text = String.format(
        textView.context.getString(R.string.score_percentage),
        getPercentOfRightAnswers(gameResult)
    )
}

private fun getPercentOfRightAnswers(gameResult: GameResult) = with(gameResult) {
    if (countOfQuestions == 0) {
        0
    } else {
        ((countOfRightAnswer / countOfQuestions.toDouble()) * 100).toInt()
    }
}

@BindingAdapter("emojiResult")
fun bindEmojiResult(imageView: ImageView, winner: Boolean) {
    imageView.setImageResource(setSmileImgId(winner))
}

private fun setSmileImgId(winner: Boolean): Int {
    return if (winner) {
        R.drawable.ic_smile
    } else {
        R.drawable.ic_sad
    }
}

@BindingAdapter("enoughRightAnswers")
fun bindEnoughRightAnswers(textView: TextView, enough: Boolean) {
    textView.setTextColor(setColorForState(textView.context, enough))
}

private fun setColorForState(context: Context, goodState: Boolean): Int {
    val colorResId = if (goodState) {
        android.R.color.holo_green_dark
    } else {
        android.R.color.holo_red_dark
    }
    return ContextCompat.getColor(context, colorResId)
}

@BindingAdapter("enoughPercentAnswers")
fun bindEnoughPercentAnswers(progressBar: ProgressBar, enough: Boolean) {
    val color = setColorForState(progressBar.context, enough)
    progressBar.progressTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("numberAsText")
fun bindNumberAsText(textView: TextView, number: Int) {
    textView.text = number.toString()
}

@BindingAdapter("onOptionClickListener")
fun bindOnOptionClickListener(textView: TextView, clickListener: OnOptionClickListener) {
    textView.setOnClickListener {
        clickListener.onOptionClick(textView.text.toString().toInt())
    }
}