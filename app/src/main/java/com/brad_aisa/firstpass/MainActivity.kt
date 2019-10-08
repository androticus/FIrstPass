package com.brad_aisa.firstpass

import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
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
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_main.*



//import sun.jvm.hotspot.utilities.IntArray



//import sun.jvm.hotspot.utilities.IntArray


class MainActivity : AppCompatActivity() {

    val passList: PassList = PassList()
    //var passListListView: GridView? = null
    var listItems = ArrayList<PassListItem>()
    var adapter: ArrayAdapter<PassListItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            listItems)

        passListListView.adapter = adapter

        buttonNew.setOnClickListener {
            onNewButtonClick()
        }
        buttonDelete.setOnClickListener {
            onDeleteButtonClick(it)
        }
        buttonEdit.setOnClickListener {
            onEditButtonClick()
        }

        loadPassList()
        refreshPassListView()
    }

    fun onDeleteButtonClick(view: View?) {
        // determine selected row
        // get the PassListItem
        val pli = passList.items[2]; //TEMP

        // delete
        passList.removeById(pli.id)
        refreshPassListView()
        adapter?.notifyDataSetChanged()

    }

    private fun onEditButtonClick() {
        //TODO onOk handler
        val dialog = EditPassListItemDialog()
        dialog.create(this)
        //TEMP
        dialog.site = "example.com"
        dialog.password = "pass123"
        dialog.show()
    }

    private fun onNewButtonClick() {
        val dialog = EditPassListItemDialog()
        dialog.create(this)
        dialog.site = "example.com"
        dialog.password = "pass123"
        dialog.setOnOkButtonHandler {
            val item = PassListItem(it.site, it.password)
            passList.add(item)
            refreshPassListView()
            adapter?.notifyDataSetChanged()
        }
        dialog.show()
    }

    /**
     * Load the PassList from storage
     *
     * Usually only called once, on startup
     */
    fun loadPassList() {
        //TEMP
        passList.add(PassListItem("somesite.com", "ApassForSome@"))
        passList.add(PassListItem("acme.com", "acme1234"))
        for (i in 0..9)
            passList.add(PassListItem("site$i.com", "Pass1234@$i"))

        //TODO: order, probably sort

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
     * Save the PassList to storage
     *
     * Called when user has made changes
     */
    fun savePassList() {

    }
}
