package com.candra.kirana_improvement_ide

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textview.MaterialTextView

object Help {


    fun helpDialog(context: Context, inputanPertama: String, inputanKedua: String){
        val builder = AlertDialog.Builder(context,R.style.AlertDialogTheme)
        val view = LayoutInflater.from(context).inflate(
            R.layout.help_message_dialog,
            null
        )
        builder.setView(view)
        view.findViewById<MaterialTextView>(R.id.isiTeks).text = inputanPertama
        view.findViewById<MaterialTextView>(R.id.isiTeksPeringatan).text = inputanKedua

        val alertDialog = builder.create()

        view.findViewById<ImageButton>(R.id.closeBtn).setOnClickListener {
            alertDialog.dismiss()
        }

        if (alertDialog.window != null){
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        alertDialog.show()
    }

    fun showToast(message: String,context: Context){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }

}