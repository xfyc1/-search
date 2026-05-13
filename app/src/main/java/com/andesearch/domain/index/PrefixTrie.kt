package com.andesearch.domain.index

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefixTrie @Inject constructor() {

    private val root = TrieNode()

    fun insert(name: String, fileId: Long) {
        var node = root
        for (ch in name.lowercase()) {
            node = node.children.getOrPut(ch) { TrieNode() }
        }
        node.isEnd = true
        node.fileIds.add(fileId)
    }

    fun search(prefix: String, limit: Int = 20): List<Long> {
        var node: TrieNode? = root
        for (ch in prefix.lowercase()) {
            node = node?.children?.get(ch) ?: return emptyList()
        }
        if (node == null) return emptyList()
        val result = mutableListOf<Long>()
        collectIds(node, result, limit)
        return result
    }

    fun remove(name: String, fileId: Long) {
        var node: TrieNode? = root
        for (ch in name.lowercase()) {
            node = node?.children?.get(ch) ?: return
        }
        node?.fileIds?.remove(fileId)
    }

    fun clear() {
        root.children.clear()
    }

    val size: Int get() = countNodes(root)

    private fun countNodes(node: TrieNode): Int {
        return 1 + node.children.values.sumOf { countNodes(it) }
    }

    private fun collectIds(node: TrieNode, result: MutableList<Long>, limit: Int) {
        if (result.size >= limit) return
        if (node.isEnd) {
            result.addAll(node.fileIds.take(limit - result.size))
        }
        for (child in node.children.values) {
            if (result.size >= limit) break
            collectIds(child, result, limit)
        }
    }

    private class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        val fileIds = mutableListOf<Long>()
        var isEnd = false
    }
}
