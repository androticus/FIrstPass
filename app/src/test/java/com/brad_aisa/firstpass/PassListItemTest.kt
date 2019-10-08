package com.brad_aisa.firstpass

import org.junit.Test

import org.junit.Assert.*

class PassListItemTest {

    @Test
    fun distinctButEqualItems_AreEqual() {
        val item1 = PassListItem("site1", "pass1")
        val item2 = PassListItem("site1", "pass1")

        assertTrue("items are equal", (item1 == item2))
    }

    @Test
    fun distinctUnequalItems_AreNotEqual() {
        val item1 = PassListItem("site1", "pass1")
        val item2 = PassListItem("site2", "pass2")

        assertFalse("items are not equal", (item1 == item2))
    }

    @Test
    fun sameItem_IsEqual() {
        val item1 = PassListItem("site1", "pass1")

        assertTrue("same item is equal", (item1 == item1))
    }
}