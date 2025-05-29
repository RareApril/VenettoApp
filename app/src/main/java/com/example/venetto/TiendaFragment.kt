package com.example.venetto

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class TiendaFragment : Fragment(R.layout.fragment_tienda) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var textPagina: TextView
    private lateinit var editTextBuscar: EditText
    private lateinit var iconoFiltro: ImageView

    private lateinit var adapter: ProductoAdapter
    private var listaCompleta = listOf<Producto>()
    private var listaFiltrada = listOf<Producto>()
    private var categoriaSeleccionada = "Todos"

    private var paginaActual = 0
    private val productosPorPagina = 6

    private val conteoCategorias = mutableMapOf<String, Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerProductos)
        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnSiguiente = view.findViewById(R.id.btnSiguiente)
        textPagina = view.findViewById(R.id.textPagina)
        editTextBuscar = view.findViewById(R.id.editTextBuscar)
        iconoFiltro = view.findViewById(R.id.iconoFiltro)

        adapter = ProductoAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        btnAnterior.setOnClickListener {
            if (paginaActual > 0) {
                paginaActual--
                actualizarListaPaginada()
            }
        }

        btnSiguiente.setOnClickListener {
            val totalPaginas = (listaFiltrada.size + productosPorPagina - 1) / productosPorPagina
            if (paginaActual < totalPaginas - 1) {
                paginaActual++
                actualizarListaPaginada()
            }
        }

        editTextBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filtrarProductos()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        iconoFiltro.setOnClickListener {
            mostrarPopupCategorias()
        }

        cargarProductosDesdeFirebase()
    }

    private fun cargarProductosDesdeFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                val lista = mutableListOf<Producto>()
                conteoCategorias.clear()
                for (document in result) {
                    val producto = document.toObject(Producto::class.java)
                    lista.add(producto)

                    val categoria = producto.categoria
                    conteoCategorias[categoria] = (conteoCategorias[categoria] ?: 0) + 1
                }

                conteoCategorias["Todos"] = lista.size

                listaCompleta = lista
                listaFiltrada = listaCompleta
                paginaActual = 0
                actualizarListaPaginada()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarListaPaginada() {
        val inicio = paginaActual * productosPorPagina
        val fin = minOf(inicio + productosPorPagina, listaFiltrada.size)
        val sublista = if (inicio < listaFiltrada.size) listaFiltrada.subList(inicio, fin) else emptyList()

        adapter.actualizarLista(sublista)
        textPagina.text = "Página ${paginaActual + 1}"
    }

    private fun filtrarProductos() {
        val texto = editTextBuscar.text.toString().trim().lowercase()

        listaFiltrada = listaCompleta.filter { producto ->
            val coincideBusqueda = producto.nombre.lowercase().contains(texto)
            val coincideCategoria = categoriaSeleccionada == "Todos" || producto.categoria == categoriaSeleccionada
            coincideBusqueda && coincideCategoria
        }

        paginaActual = 0
        actualizarListaPaginada()
    }

    private fun mostrarPopupCategorias() {
        val popupMenu = PopupMenu(requireContext(), iconoFiltro)

        val categoriasBase = listOf(
            "Todos",
            "Accesorios de costura",
            "Accesorios de planchas",
            "Accesorios de joyeria",
            "Accesorios para tejer en crochet",
            "Liquido desmanchador",
            "Maquina de coser",
            "Mesas de planchado",
            "Tijeras",
            "Vaporizador"
        )

        for (categoria in categoriasBase) {
            val cantidad = conteoCategorias[categoria] ?: 0
            popupMenu.menu.add("$categoria ($cantidad)")
        }

        popupMenu.setOnMenuItemClickListener { item ->
            val texto = item.title.toString()
            val nombreCategoria = texto.substringBefore(" (").trim()
            categoriaSeleccionada = nombreCategoria
            paginaActual = 0
            filtrarProductos()
            true
        }

        popupMenu.show()
    }
}
