package com.example.venetto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.venetto.databinding.FragmentCrudBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CrudFragment : Fragment(),
    CrudAdapter.OnItemClickListener,
    ProductoDialogFragment.OnProductoGuardadoListener {

    private var _binding: FragmentCrudBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CrudAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnAtras.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            val mainActivity = activity as? MainActivity
            mainActivity?.findViewById<View>(R.id.fragment_container)?.visibility = View.GONE
            mainActivity?.findViewById<View>(R.id.mainContent)?.visibility = View.VISIBLE
        }

        val query: Query = db.collection("productos").orderBy("nombre", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Producto>()
            .setQuery(query, Producto::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        adapter = CrudAdapter(options, this)

        binding.recyclerCrud.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CrudFragment.adapter
        }

        binding.btnAgregarProducto.setOnClickListener {
            mostrarFormularioAgregar()
        }
    }

    private fun mostrarFormularioAgregar() {
        val dialog = ProductoDialogFragment()
        dialog.listener = this
        dialog.show(childFragmentManager, "AgregarProducto")
    }

    override fun onEditarClick(producto: Producto, id: String) {
        val dialog = ProductoDialogFragment(producto, id)
        dialog.listener = this
        dialog.show(childFragmentManager, "EditarProducto")
    }

    override fun onEliminarClick(id: String) {
        db.collection("productos").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al eliminar producto", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onProductoGuardado() {
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
