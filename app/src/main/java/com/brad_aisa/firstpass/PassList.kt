package com.brad_aisa.firstpass

import java.io.*
import java.util.UUID

class PassList {
    private var nextId = 0
    private val items_ = mutableListOf<PassListItem>()

    val items: List<PassListItem>
        get() = items_

    /**
     * Add the item to the list
     *
     * Note that no attempt is made to determine if the item is a duplicate
     *
     * @param item the item to add
     * @return the item just added (for chaining)
     */
    fun add(item: PassListItem) : PassListItem {
        items_.add(item)
        return item
    }

    /**
     * Load the list from a stream -- existing contents are deleted
     *
     * @param stream the InputStream from which to load the list
     */
    fun load(stream: InputStream) {
        items_.clear()

        val sr = InputStreamReader(stream)
        sr.use {
            val bsr = BufferedReader(sr)
            bsr.use {
                var line = bsr.readLine()
                while (line != null) {
                    val splits = line.split('\t')
                    //TODO: probably can't assume splits[1] will exist, if empty
                    val item = PassListItem(splits[0], splits[1])
                    items_.add(item)

                    line = bsr.readLine()
                }
            }

        }
    }

    /**
     * Remove the item with the indicated id
     *
     * @param id the id of the item to remove
     * @return true if an item with the id was found and removed; false otherwise
     */
    fun removeById(id: UUID): Boolean {
        val item = items_.find { it.id == id }
        if (item == null)
            return false
        return items_.remove(item)
    }

    /**
     * Save the list to a stream -- the caller must close the stream
     *
     * @param stream the OutputStream to which to save the list
     */
    fun save(stream: OutputStream) {
        val ps = PrintStream(stream)
        ps.use {
            for (item in items_) {
                ps.println("${item.site}\t${item.password}")
            }
        }
    }
}