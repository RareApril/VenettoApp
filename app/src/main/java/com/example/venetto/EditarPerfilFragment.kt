package com.example.venetto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditarPerfilFragment : Fragment() {

    private lateinit var etNombre: EditText
    private lateinit var etDNI: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etDireccion2: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnGuardarCambios: Button
    private lateinit var btnAtras: Button

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editar_perfil, container, false)


        etNombre = view.findViewById(R.id.etNombre)
        etDNI = view.findViewById(R.id.etDNI)
        etDireccion = view.findViewById(R.id.etDireccion)
        etDireccion2 = view.findViewById(R.id.etDireccion2)
        etTelefono = view.findViewById(R.id.etTelefono)
        etEmail = view.findViewById(R.id.etEmail)
        btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios)
        btnAtras = view.findViewById(R.id.btnAtras)

        cargarDatosUsuario()

        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }

        btnAtras.setOnClickListener {

            requireActivity().supportFragmentManager.popBackStack()
            val mainActivity = activity as? MainActivity
            mainActivity?.findViewById<View>(R.id.fragment_container)?.visibility = View.GONE
            mainActivity?.findViewById<View>(R.id.mainContent)?.visibility = View.VISIBLE
        }

        return view
    }

    private fun cargarDatosUsuario() {
        val uid = user?.uid ?: return

        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etNombre.setText(document.getString("nombre") ?: "")
                    etDNI.setText(document.getString("dni") ?: "")
                    etDireccion.setText(document.getString("direccion") ?: "")
                    etDireccion2.setText(document.getString("direccion2") ?: "")
                    etTelefono.setText(document.getString("telefono") ?: "")
                    etEmail.setText(document.getString("email") ?: "")
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarCambios() {
        val uid = user?.uid ?: return

        val nuevosDatos = mapOf(
            "nombre" to etNombre.text.toString(),
            "dni" to etDNI.text.toString(),
            "direccion" to etDireccion.text.toString(),
            "direccion2" to etDireccion2.text.toString(),
            "telefono" to etTelefono.text.toString()
        )

        db.collection("usuarios").document(uid).update(nuevosDatos)
            .addOnSuccessListener {
                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
            }
    }
}