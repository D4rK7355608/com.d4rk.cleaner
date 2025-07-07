package com.blinkit.droiddexexample.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.view.updatePadding
import com.blinkit.droiddex.constants.PerformanceLevel
import com.blinkit.droiddexexample.R
import com.blinkit.droiddexexample.databinding.LayoutItemBinding
import com.blinkit.droiddexexample.utils.dpToPx
import com.blinkit.droiddexexample.utils.getColor
import kotlin.math.abs

class ItemView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0,
): ConstraintLayout(context, attrs, defStyleAttr) {

	private val binding: LayoutItemBinding by lazy { LayoutItemBinding.inflate(LayoutInflater.from(context), this) }

	private var currentProgress = 0

	private var animation: ObjectAnimator? = null
		set(value) {
			field?.cancel()
			field = value
		}

	init {
		layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
			updatePadding(bottom = 8.dpToPx().toInt())
		}

		background = GradientDrawable().apply {
			shape = GradientDrawable.RECTANGLE
			setColor(context.getColor(android.R.color.white))
			cornerRadius = 12.dpToPx()
		}
	}

	fun set(level: PerformanceLevel, classText: String) {
		binding.progressBar.progressDrawable = getProgressBackground(level)

		val newProgress = level.level * 25
		animation = ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress, newProgress).apply {
			duration = abs(newProgress - currentProgress) * 5L
			doOnCancel { currentProgress = newProgress }
			doOnEnd {
				currentProgress = newProgress
				binding.progressBar.progress = newProgress
			}
			start()
		}

		binding.level.text = level.name.lowercase().replaceFirstChar { it.uppercase() }
		binding.level.setTextAppearance(R.style.TextAppearanceLight)

		binding.className.text = classText
		binding.className.setTextAppearance(R.style.TextAppearanceMedium)
	}

	private fun getProgressBackground(level: PerformanceLevel) = GradientDrawable().apply {
		shape = GradientDrawable.RING
		setColor(level.getColor(context))
	}
}
