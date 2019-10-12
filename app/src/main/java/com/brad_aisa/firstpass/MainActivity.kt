package com.brad_aisa.firstpass

import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import com.brad_aissa.firstpass.R
import android.view.View
import android.widget.*
import java.io.*
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_main.*
import android.content.DialogInterface
import androidx.security.crypto.*


private const val FILENAME = "FirstPassList.enc"; // an encrypted text file

class MainActivity : AppCompatActivity() {

    private var selectedIndex = -1
    private val passList: PassList = PassList()
    private var listItems = ArrayList<PassListItem>()
    private var adapter: ArrayAdapter<PassListItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            listItems)

        passListListView.adapter = adapter
        passListListView.setSelector(R.color.list_selector_background)
        passListListView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                handleItemSelecting(position)
            }
        }

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
     * Handles an item being selected, or forces an item to be selected
     *
     * NOTE: forceSelection can currently only be used to force unselection
     *
     * @param itemIndex the item being selected or to select; -ve value for none
     * @param forceSelection set false (default) when handling from UI; set true to force selection from code
     */
    private fun handleItemSelecting(itemIndex: Int, forceSelection: Boolean = false) {
        selectedIndex = itemIndex

        val itemIsSelected = (selectedIndex >= 0)
        buttonCopyClipboard.isEnabled = itemIsSelected
        buttonEdit.isEnabled = itemIsSelected
        buttonDelete.isEnabled = itemIsSelected

        if (forceSelection) {
            if (itemIsSelected) {
                //NOTE: no effective way of doing this was found
            } else {
                // note: this is an effective way to force unselect
                passListListView.adapter = adapter
            }
        }
    }

    /**
     * Get the currently selected item
     *
     * @return the currently selected item; null if empty or none selected
     */
    fun getSelectedPassListItem(): PassListItem? {
        if (selectedIndex < 0)
            return null

        var selItem = passListListView.adapter.getItem(selectedIndex) as PassListItem
        return selItem
    }

    private fun onNewButtonClick() {
        val dialog = EditPassListItemDialog()
        dialog.create(this)
        dialog.site = "example.com"
        dialog.password = "pass123"
        dialog.setOnOkButtonHandler {
            // create and add a new item from dialog, but must have a site
            val site = it.site.trim()
            if (!site.isEmpty()) {
                val item = PassListItem(it.site, it.password)
                passList.add(item)
                saveRefreshPassListView()
                // unselect items, so previous item is not selected
                handleItemSelecting(-1, forceSelection = true)
            }
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
            // update the item from dialog -- user not allowed to set blank
            val site = it.site.trim()
            if (!site.isEmpty()) {
                pli.site = site
                pli.password = it.password
                saveRefreshPassListView()
            }
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
        handleItemSelecting(-1, forceSelection = true)
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
    }

    /**
     * Load the PassList from encrypted storage
     *
     * Usually only called once, on startup
     */
    private fun loadPassList() {
        passList.clear()

        val passListFile = getPassListFile()
        if (passListFile.exists()) {
            val encryptedFile = getEncryptedPassListFile(passListFile)
            val stream = encryptedFile.openFileInput()
            //TODO put in an exception handler, warn user or try to recover
            passList.load(stream)
        }
    }

    /**
     * Save the PassList to encrypted storage
     *
     * Called when user has made changes
     */
    private fun savePassList() {
        try {
            // we have to delete it if it already exists
            // TODO: we might want to copy to a backup file, to restore in case something goes wrong
            val file = getPassListFile()
            if (file.exists()) {
                file.delete()
            }
            val encryptedFile = getEncryptedPassListFile(file)
            val stream = encryptedFile.openFileOutput()
            passList.save(stream)
        }
        catch (e: Exception) {
            e.toString()
            //TODO maybe try to recover, or warn user
        }
    }

    private fun getPassListFile(): File {
        val filesDir = applicationContext.filesDir
        return File(filesDir, FILENAME)
    }

    private fun getEncryptedPassListFile(file: File): EncryptedFile {
        // see: https://developer.android.com/guide/topics/security/cryptography
        // Google-recommended values
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val encryptedFile = EncryptedFile.Builder(
            file,
            applicationContext,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        return encryptedFile
    }

}
