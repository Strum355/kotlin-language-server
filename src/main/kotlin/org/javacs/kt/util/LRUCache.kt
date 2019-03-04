package org.javacs.kt.util

import java.util.LinkedHashMap
import kotlin.collections.MutableMap

class LRUCache<K, V>(private val maxSize: Int): LinkedHashMap<K, V>(16, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean {
        return size >= maxSize
    }
}
