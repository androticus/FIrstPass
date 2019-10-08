package com.brad_aisa.firstpass

import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.widget.TextView
import com.brad_aissa.firstpass.R
import android.widget.GridView
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import java.io.*
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import android.os.Environment


private const val FILENAME = "FirstPassList.txt"; //TODO: encrypted version uses different name

class MainActivity : AppCompatActivity() {

    private val passList: PassList = PassList()
    private var listItems = ArrayList<PassListItem>()
    private var adapter: ArrayAdapter<PassListItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passListListView.setSelector(R.color.list_selector_background)
        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            listItems)

        passListListView.adapter = adapter

        // attach button click handlers
        buttonNew.setOnClickListener {
            onNewButtonClick()
        }
        buttonCopyClipboard.setOnClickListener {
            onCopyButtonClick()
        }
        buttonEdit.setOnClickListener {
            onEditButtonClick()
        }
        buttonDelete.setOnClickListener {
            onDeleteButtonClick()
        }

        loadPassList()
        refreshPassListView()
    }

    /**
     * Get the currently selected item
     *
     * @return the currently selected item; null if empty or none selected
     */
    fun getSelectedPassListItem(): PassListItem? {
        //TODO just a dummy for now
        if (passList.items.isEmpty()) {
            return null
        } else {
            return passList.items[2]; //TEMP
        }
    }

    private fun onNewButtonClick() {
        val dialog = EditPassListItemDialog()
        dialog.create(this)
        dialog.site = "example.com"
        dialog.password = "pass123"
        dialog.setOnOkButtonHandler {
            // create and add a new item from dialog
            val item = PassListItem(it.site, it.password)
            passList.add(item)
            saveRefreshPassListView()
        }
        dialog.show()
    }

    private fun onCopyButtonClick() {
        val pli = getSelectedPassListItem() ?: return

        // Gets a handle to the clipboard service.
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Password from FirstPass", pli.password)
        clipboard.primaryClip = clipData
    }

    private fun onEditButtonClick() {
        val pli = getSelectedPassListItem() ?: return

        val dialog = EditPassListItemDialog()
        dialog.create(this)
        dialog.site = pli.site
        dialog.password = pli.password
        dialog.setOnOkButtonHandler {
            // update the item from dialog
            pli.site = it.site
            pli.password = it.password
            saveRefreshPassListView()
        }
        dialog.show()
    }

    private fun onDeleteButtonClick() {
        val pli = getSelectedPassListItem() ?: return

        confirmDeleteItem(pli)
    }

    private fun confirmDeleteItem(passListItem: PassListItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm delete")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton(
                "Yes", DialogInterface.OnClickListener { dialog, which ->
                    doDeleteItem(passListItem)
                })
            .setNegativeButton(
                "No", DialogInterface.OnClickListener { dialog, id ->
                    //nothing
                })

        builder.show()
    }

    private fun doDeleteItem(passListItem: PassListItem) {
        passList.removeById(passListItem.id)
        saveRefreshPassListView()
    }


    fun saveRefreshPassListView() {
        savePassList()
        refreshPassListView()
        adapter?.notifyDataSetChanged()
    }

    fun refreshPassListView() {
        listItems.clear()
        for (pli in passList.items) {
            listItems.add(pli)
        }

        //passListTextView
        /*val passListTextView: TextView = findViewById((R.id.passListTextView))
        //TEMP
        var passListText = ""
        for (pli in passList.items) {
            passListText += (pli.site + '\n');
        }
        passListTextView.text = passListText*/
    }

    /**
     * Load the PassList from storage
     *
     * Usually only called once, on startup
     */
    private fun loadPassList() {
        passList.clear()
        val passListFile = getPassListFile()
        if (passListFile.exists()) {
            val passListFile = getPassListFile()
            val stream = FileInputStream(passListFile)
            //TODO put in an exception handler, warn user or try to recover
            passList.load(stream)
        } else {
            //TEMP
            passList.add(PassListItem("somesite.com", "ApassForSome@"))
            passList.add(PassListItem("acme.com", "acme1234"))
            for (i in 0..9)
                passList.add(PassListItem("site$i.com", "Pass1234@$i"))
        }

        //TODO: order, probably sort

    }
    /**
     * Save the PassList to storage
     *
     * Called when user has made changes
     */
    private fun savePassList() {
        val passListFile = getPassListFile()
        if (!passListFile.exists()) {
            passListFile.createNewFile()
        }
        val stream = FileOutputStream(passListFile)
        //TODO put in an exception handler, warn user or try to recover
        passList.save(stream)
    }

    private fun getPassListFile(): File {
        val filesDir = applicationContext.filesDir//Environment.getDataDirectory()
        return File(filesDir, FILENAME)
    }
}
