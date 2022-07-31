package me.qboi.texteditor

import javax.swing.JTree
import javax.swing.tree.TreeModel

class ReadOnlyTree(apply: TreeModel) : JTree(apply) {
    override fun isEditable(): Boolean {
        return false
    }
}
