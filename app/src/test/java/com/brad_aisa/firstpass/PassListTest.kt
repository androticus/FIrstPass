package com.brad_aisa.firstpass

import org.junit.Test

import org.junit.Assert.*

import java.io.*
import java.util.UUID
/**
 * Tests PassList
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PassListTest {
    private fun createSampleList() : PassList {
        val passList = PassList()
        passList.add(PassListItem("site2", "pass2"))
        passList.add(PassListItem("site1", "pass1"))
        passList.add(PassListItem("site3", "pass3ЖЕЗ")) // contains Unicode, to test streaming
        passList.add(PassListItem("site4", "")) // empty password to test streaming
        return passList
   }
    @Test
    fun addItem_Adds() {
        val passList = PassList()
        assertEquals("empty count", 0, passList.items.size)
        val newItem = PassListItem(site="dummy", password="pass")
        passList.add(newItem)
        assertEquals("new item added count", 1, passList.items.size)

    }

    @Test
    fun clear_Clears() {
        val passList = PassList()
        // confirm empty doesn't cause an exception
        passList.clear()

        val newItem = PassListItem(site="dummy", password="pass")
        passList.add(newItem)
        assertEquals("new item added count", 1, passList.items.size)
        passList.clear()
        assertEquals("count after clear", 0, passList.items.size)
    }

    @Test
    fun removeByKnownId_Removes() {
        val passList = PassList()
        var newItem = PassListItem(site="dummy", password="pass")
        passList.add(newItem)
        assertEquals("new item added count", 1, passList.items.size)
        // now delete, we expect it to be deleted, and for count to be zero
        assertTrue("item is deleted", passList.removeById(newItem.id))
        assertEquals("item count after deletion", 0, passList.items.size)
    }

    @Test
    fun removeByUnknownId_Removes() {
        val passList = PassList()
        var newItem = PassListItem(site="dummy", password="pass")
        passList.add(newItem)
        val unknownId = UUID.randomUUID()
        // now delete, we expect it to be deleted, and for count to be zero
        assertFalse("no item is deleted", passList.removeById(unknownId))
        assertEquals("item count after no deletion", 1, passList.items.size)
    }

    @Test
    fun canSaveAndLoad() {
        val passList = createSampleList()
        // create an array buffer to save
        val baos = ByteArrayOutputStream()
        passList.save(baos)
        val oa = baos.toByteArray()
        assertTrue("output stream byte array non-empty", oa.isNotEmpty())

        // now, restore into a new list, and compare
        val bais = ByteArrayInputStream(oa)
        val passList2 = PassList()
        passList2.load(bais)
        assertTrue("loaded list is same as saved list", passList.valueEquals(passList2))
    }

}
