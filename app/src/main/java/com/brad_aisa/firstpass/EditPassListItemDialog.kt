package com.brad_aisa.firstpass

import androidx.appcompat.app.AppCompatActivity
import android.app.*
import android.content.*
import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.brad_aissa.firstpass.R
import android.widget.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.widget.ArrayAdapter
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_main.*

class EditPassListItemDialog  {
    private var dialog: AlertDialog? = null
    private var siteEditText: EditText? = null
    private var passwordEditText: EditText? = null

    var site: String = ""
    var password: String = ""

    /**
     * Called if the user presses Ok
     *
     * @param it this instance of the dialog
     */
    var onOkButtonHandler_: ((EditPassListItemDialog) -> Unit)? = null

    fun setOnOkButtonHandler(handler: (EditPassListItemDialog) -> Unit) {
        onOkButtonHandler_ = handler
    }

    fun create(parentActivity: AppCompatActivity) {
        val builder: AlertDialog.Builder? = parentActivity.let {
            AlertDialog.Builder(it)
        }
        if (builder == null)
            throw Exception("could not create builder")
        // Get the layout inflater
        val inflater = LayoutInflater.from(parentActivity)//requireActivity().layoutInflater;

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_edit_pass_item, null))

        builder.setTitle("Edit the pass Item")
            .setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, id ->
                    onOkButtonClick()
                })
            .setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, id ->
                    onCancelButtonClick()
                })

        dialog = builder.create()
        dialog?.setOnShowListener {
            siteEditText = dialog?.findViewById<EditText>(R.id.siteEditText)
            passwordEditText = dialog?.findViewById<EditText>(R.id.passwordEditText)
            siteEditText?.setText(site)
            passwordEditText?.setText(password)
        }
    }


    private fun onOkButtonClick() {
        site = siteEditText?.text.toString()
        password = passwordEditText?.text.toString()
        if (onOkButtonHandler_ != null)
            onOkButtonHandler_?.invoke(this)
    }

    private fun onCancelButtonClick() {

    }

    fun show() {
        dialog?.show()
    }
}