import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.collobo_station.Fragment.Fragment_Tab_All
import com.example.collobo_station.Fragment.Fragment_Tab_DeadLine
import com.example.collobo_station.Fragment.Fragment_Tab_Recent

class TabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return 3 // Number of tabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Fragment_Tab_All()
            1 -> Fragment_Tab_Recent()
            2 -> Fragment_Tab_DeadLine()
            else -> Fragment_Tab_All()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "전체보기"
            1 -> "최근등록순"
            2 -> "마감순"
            else -> "All"
        }
    }
}
