package com.example.venetto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot

class CrudAdapter(
    options: FirestoreRecyclerOptions<Producto>,
    private val listener: OnItemClickListener
) : FirestoreRecyclerAdapter<Producto, CrudAdapter.ProductoViewHolder>(options) {

    interface OnItemClickListener {
        fun onEditarClick(producto: Producto, id: String)
        fun onEliminarClick(id: String)
    }

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionProducto)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminar)

        fun bind(producto: Producto, id: String) {
            tvNombre.text = producto.nombre
            tvDescripcion.text = producto.descripcion

            val context = itemView.context
            val resId = context.resources.getIdentifier(producto.imagen, "drawable", context.packageName)
            Glide.with(context)
                .load(resId)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(imgProducto)

            btnEditar.setOnClickListener {
                listener.onEditarClick(producto, id)
            }

            btnEliminar.setOnClickListener {
                listener.onEliminarClick(id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crud_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int, model: Producto) {
        val snapshot: DocumentSnapshot = snapshots.getSnapshot(position)
        val id = snapshot.id
        holder.bind(model, id)
    }
}