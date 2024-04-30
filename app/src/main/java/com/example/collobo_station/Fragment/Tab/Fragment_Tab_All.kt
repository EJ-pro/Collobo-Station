package com.example.collobo_station.Fragment.Tab

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.TabAllAdapter
import com.example.collobo_station.ContestDetailActivity
import com.example.collobo_station.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class Fragment_Tab_All : Fragment(), TabAllAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabAllAdapter: TabAllAdapter
    private var contestList = mutableListOf<DocumentSnapshot>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_all, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tabAllAdapter = TabAllAdapter(contestList)
        recyclerView.adapter = tabAllAdapter
        loadDataFromFirestore()
        tabAllAdapter.setOnItemClickListener(this)
        return view
    }

    override fun onItemClick(position: Int) {
        // 클릭된 아이템의 위치(position)을 통해 원하는 동작을 수행
        val clickedItem = contestList[position]
        val contestName = clickedItem.getString("대회명") ?: ""
        val contestField = clickedItem.getString("분야") ?: ""
        val contestImage = clickedItem.getString("이미지") ?: ""
        val contestPeriodStart = clickedItem.getString("접수시작") ?: ""
        val contestPeriod = clickedItem.getTimestamp("접수마감")?.toDate() ?: Date()
        val contestCount = calculateDDay(contestPeriod)
        val contestRegion = clickedItem.getString("대회지역") ?: ""
        val contestAward = clickedItem.getString("시상") ?: ""
        val contestUrl = clickedItem.getString("접수url") ?: ""
        val contestOrganizer = clickedItem.getString("주관") ?: ""
        val contestHost = clickedItem.getString("주최") ?: ""
        val contestEligibility = clickedItem.getString("참가자격") ?: ""
        val contestHomepageUrl = clickedItem.getString("홈페이지url") ?: ""
        val contestprecautions = clickedItem.getString("주의사항") ?: ""

        // 데이터를 담을 Intent 생성
        val intent = Intent(requireContext(), ContestDetailActivity::class.java).apply {
            putExtra("contestName", contestName)
            putExtra("contestField", contestField)
            putExtra("contestImage", contestImage)
            putExtra("contestPeriodStart", contestPeriodStart)
            putExtra("contestPeriod", SimpleDateFormat("yyyy-MM-dd").format(contestPeriod))
            putExtra("contestCount", contestCount)
            putExtra("contestRegion", contestRegion)
            putExtra("contestAward", contestAward)
            putExtra("contestUrl", contestUrl)
            putExtra("contestOrganizer", contestOrganizer)
            putExtra("contestHost", contestHost)
            putExtra("contestEligibility", contestEligibility)
            putExtra("contestHomepageUrl", contestHomepageUrl)
            putExtra("contestprecautions", contestprecautions)
        }

        // Activity 시작
        startActivity(intent)
    }

    private fun loadDataFromFirestore() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = getContestDataFromFirestore()
                for (document in querySnapshot.documents) {
                    contestList.add(document)
                }
                tabAllAdapter.setItems(contestList)
                tabAllAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getContestDataFromFirestore(): QuerySnapshot {
        val db = FirebaseFirestore.getInstance()
        val contestCollection = db.collection("Contest")
        return contestCollection.get().await()
    }

    private fun calculateDDay(eventDate: Date): String {
        val currentDate = Calendar.getInstance().time
        val diff = eventDate.time - currentDate.time
        val days = diff / (1000 * 60 * 60 * 24)
        return if (days >= 0) {
            "D-${days + 1}"
        } else {
            "D+${-days}"
        }
    }
}
