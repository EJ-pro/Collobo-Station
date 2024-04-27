package com.example.collobo_station.Fragment.Tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.TabAllAdapter
import com.example.collobo_station.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Fragment_Tab_DeadLine : Fragment() {
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
        return view
    }

    private fun loadDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val contestCollection = db.collection("Contest")

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val querySnapshot = contestCollection.orderBy("D-day", Query.Direction.ASCENDING).get().await()
                //val querySnapshot = contestCollection.orderBy("대회명", Query.Direction.DESCENDING).get().await()
                for (document in querySnapshot.documents) {
                    val contestName = document.getString("대회명") ?: ""
                    val contestField = document.getString("분야") ?: ""
                    val contestImage = document.getString("이미지") ?: ""
                    val contestPeriod = document.getString("접수기간") ?: ""
                    val contestCount = document.getString("D-day") ?: ""

                    val contestItem = "$contestName\n분야: $contestField\n접수기간: $contestPeriod\nD-day: $contestCount"
                    contestList.add(document)
                }
                tabAllAdapter.setItems(contestList)
                tabAllAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}