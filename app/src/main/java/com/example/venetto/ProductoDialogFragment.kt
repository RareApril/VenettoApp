package com.example.venetto

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.venetto.databinding.DialogProductoBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProductoDialogFragment(
    private val productoExistente: Producto? = null,
    private val productoId: String? = null
) : DialogFragment() {

    private var _binding: DialogProductoBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    interface OnProductoGuardadoListener {
        fun onProductoGuardado()
    }

    var listener: OnProductoGuardadoListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogProductoBinding.inflate(LayoutInflater.from(context))

        productoExistente?.let {
            binding.etNombreProducto.setText(it.nombre)
            binding.etDescripcionProducto.setText(it.descripcion)
            binding.etCategoriaProducto.setText(it.categoria)
            binding.etImagenProducto.setText(it.imagen)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (productoExistente != null) "Editar Producto" else "Nuevo Producto")
            .setView(binding.root)
            .setNegativeButton("Cancelar") { _, _ -> }
            .setPositiveButton("Guardar") { _, _ ->
                guardarProducto()
            }
            .create()
    }

    private fun guardarProducto() {
        val nombre = binding.etNombreProducto.text.toString().trim()
        val descripcion = binding.etDescripcionProducto.text.toString().trim()
        val categoria = binding.etCategoriaProducto.text.toString().trim()
        val imagen = binding.etImagenProducto.text.toString().trim()

        val producto = Producto(nombre, descripcion, imagen, categoria)

        if (productoExistente != null && productoId != null) {
            db.collection("productos").document(productoId).set(producto)
                .addOnSuccessListener {
                    listener?.onProductoGuardado()
                }
        } else {
            db.collection("productos").add(producto)
                .addOnSuccessListener {
                    listener?.onProductoGuardado()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}