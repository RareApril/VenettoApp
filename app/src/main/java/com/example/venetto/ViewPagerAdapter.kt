package com.example.venetto

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: MainActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InicioFragment()
            1 -> NosotrosFragment()
            2 -> BlogFragment()
            3 -> ContactanosFragment()
            4 -> TiendaFragment()
            else -> InicioFragment()
        }
    }
}
