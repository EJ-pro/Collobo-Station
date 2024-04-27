package com.example.collobo_station.Fragment.Tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.collobo_station.R
import com.google.firebase.firestore.DocumentSnapshot

class Fragment_Contest_Detail : Fragment() {
    private lateinit var contestSnapshot: DocumentSnapshot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contest_detail, container, false)

        // 전달된 데이터 받기
        val contestName = requireArguments().getString("contestName") ?: ""
        val contestField = requireArguments().getString("contestField") ?: ""
        val contestPeriod = requireArguments().getString("contestPeriod") ?: ""
        val contestCount = requireArguments().getString("contestCount") ?: ""

        // 상세 페이지에 데이터 표시
        val contestNameTextView = view.findViewById<TextView>(R.id.contestNameTextView)
        val contestFieldTextView = view.findViewById<TextView>(R.id.contestFieldTextView)
        val contestPeriodTextView = view.findViewById<TextView>(R.id.contestPeriodTextView)
        val contestCountTextView = view.findViewById<TextView>(R.id.contestCountTextView)

        contestNameTextView.text = contestName
        contestFieldTextView.text = "분야: $contestField"
        contestPeriodTextView.text = "접수기간: $contestPeriod"
        contestCountTextView.text = "D-day: $contestCount"

        return view
    }
}
