package com.brad_aisa.firstpass

/**
 * Extension function to do value equality compare of PassList objects
 */
fun PassList.valueEquals(other: PassList): Boolean {
    if (this.items.size != other.items.size)
        return false

    for (i in this.items.indices) {
        if (this.items[i] != other.items[i])
            return false
    }
    return true
}