package com.example.venetto

import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var mainContent: LinearLayout
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var iconoPerfil: ImageView
    private lateinit var tvCerrarSesion: TextView
    private lateinit var topBar: LinearLayout

    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainContent = findViewById(R.id.mainContent)
        fragmentContainer = findViewById(R.id.fragment_container)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        iconoPerfil = findViewById(R.id.iconoPerfil)
        tvCerrarSesion = findViewById(R.id.tvCerrarSesion)
        topBar = findViewById(R.id.topBar)

        tvCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            showLogin()
        }

        iconoPerfil.setOnClickListener { view ->
            showProfileMenu(view)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (fragmentContainer.visibility == View.VISIBLE) {

                topBar.visibility = View.GONE
                mainContent.visibility = View.GONE
            } else {
                topBar.visibility = View.VISIBLE
                mainContent.visibility = View.VISIBLE
            }
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            showLogin()
        } else {
            fetchUserRole()
            showMainContent()
        }
    }

    private fun showLogin() {
        mainContent.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
        topBar.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun showMainContent() {
        mainContent.visibility = View.VISIBLE
        fragmentContainer.visibility = View.GONE
        topBar.visibility = View.VISIBLE

        adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Inicio"
                1 -> tab.text = "Nosotros"
                2 -> tab.text = "Blog"
                3 -> tab.text = "Contáctanos"
                4 -> tab.text = "Tienda"
            }
        }.attach()
    }

    private fun fetchUserRole() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                userRole = if (document != null && document.exists()) {
                    document.getString("rol")
                } else {
                    "usuario"
                }
            }
            .addOnFailureListener {
                userRole = "usuario"
            }
    }

    private fun showProfileMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_perfil, popup.menu)


        if (userRole != "admin") {
            popup.menu.findItem(R.id.action_crud)?.isVisible = false
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_editar_perfil -> {
                    showEditarPerfil()
                    true
                }
                R.id.action_crud -> {
                    showCrud()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showCrud() {
        mainContent.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
        topBar.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CrudFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showEditarPerfil() {
        mainContent.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
        topBar.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EditarPerfilFragment())
            .addToBackStack(null)
            .commit()
    }
    fun irAPestania(indice: Int) {
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.currentItem = indice
    }
}