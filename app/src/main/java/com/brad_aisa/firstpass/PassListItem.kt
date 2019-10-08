package com.brad_aisa.firstpass

import java.util.UUID


class PassListItem(
    var site: String = "",
    var password: String = ""
){
    val id: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        return when (other is PassListItem) {
            true -> equals(other)
            else -> false
        }
    }

    fun equals(other: PassListItem): Boolean {
        return (site == other.site && password == other.password)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return site
    }
}