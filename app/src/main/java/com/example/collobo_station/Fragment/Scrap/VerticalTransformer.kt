package com.example.collobo_station.Fragment.Scrap

import android.view.View
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class VerticalTransformer(private val offscreenPageLimit: Int) : ViewPager2.PageTransformer {

    companion object {
        private const val DEFAULT_TRANSLATION_Y = .0f
        private const val DEFAULT_TRANSLATION_FACTOR = 1.1f
        // 상자들이 겹치는 정도를 제어합니다.
        // 값이 작을수록 상자들이 더 많이 겹칩니다.
        // 예: 0.5f로 설정하면 겹치는 정도가 더 커집니다.

        private const val SCALE_FACTOR = 0f
        // 페이지 스케일(크기)을 줄이는 정도를 제어합니다.
        // 값이 커질수록 페이지가 작아집니다.
        // 예: 0.2f로 설정하면 페이지 크기가 더 작아집니다.

        private const val DEFAULT_SCALE_X = 1f   // 페이지의 기본 너비 크기를 결정합니다.
        // 예: 1f로 설정하면 페이지가 원래 크기로 표시됩니다.
        // 예: 0.8f로 설정하면 페이지가 기본적으로 80% 너비로 표시됩니다.

        private const val DEFAULT_SCALE_Y = 0.8f   // 페이지의 기본 높이 크기를 결정합니다.
        // 예: 1f로 설정하면 페이지가 원래 크기로 표시됩니다.
        // 예: 0.9f로 설정하면 페이지가 기본적으로 90% 높이로 표시됩니다.

        private const val ALPHA_FACTOR = 0.1f
        // 페이지 투명도 변화를 제어합니다.
        // 값이 커질수록 페이지가 더 투명해집니다.
        // 예: 0.5f로 설정하면 페이지의 투명도가 더 크게 변합니다.

        private const val DEFAULT_ALPHA = 1f
        // 페이지의 기본 투명도를 결정합니다.
        // 예: 1f로 설정하면 페이지가 완전히 투명하지 않은 상태로 표시됩니다.
    }

    override fun transformPage(page: View, position: Float) {
        page.apply {
            // 페이지 쌓임 순서를 결정합니다.
            ViewCompat.setElevation(page, -abs(position))

            // 페이지의 크기 및 투명도 변화를 계산합니다.
            val scaleYFactor = -SCALE_FACTOR * position + DEFAULT_SCALE_Y
            val alphaFactor = -ALPHA_FACTOR * position + DEFAULT_ALPHA

            when {
                position <= 0f -> {
                    // 현재 페이지의 경우
                    translationY = DEFAULT_TRANSLATION_Y
                    scaleX = DEFAULT_SCALE_X
                    scaleY = DEFAULT_SCALE_Y
                    alpha = DEFAULT_ALPHA + position
                }
                position <= offscreenPageLimit - 1 -> {
                    // 첫 번째 오프스크린 페이지의 경우
                    scaleY = scaleYFactor
                    translationY = -(height / DEFAULT_TRANSLATION_FACTOR) * position
                    alpha = alphaFactor
                }
                else -> {
                    // 그 외의 페이지
                    translationY = DEFAULT_TRANSLATION_Y
                    scaleX = DEFAULT_SCALE_X
                    scaleY = DEFAULT_SCALE_Y
                    alpha = DEFAULT_ALPHA
                }
            }
        }
    }
}