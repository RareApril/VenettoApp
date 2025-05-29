package com.example.venetto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView

class ProductoAdapter(private var listaProductos: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreProducto = itemView.findViewById<TextView>(R.id.nombreProducto)
        private val precioProducto = itemView.findViewById<TextView>(R.id.precioProducto)
        private val imagenProducto = itemView.findViewById<ImageView>(R.id.imagenProducto)

        fun bind(producto: Producto) {
            nombreProducto.text = producto.nombre
            precioProducto.text = "S/. ${producto.precio}"

            val context = itemView.context
            val resId = context.resources.getIdentifier(
                producto.imagen,
                "drawable",
                context.packageName
            )

            if (resId != 0) {
                imagenProducto.setImageResource(resId)
            } else {
                imagenProducto.setImageResource(R.drawable.costura1_veneto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(listaProductos[position])
    }

    override fun getItemCount(): Int = listaProductos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        listaProductos = nuevaLista
        notifyDataSetChanged()
    }
}
