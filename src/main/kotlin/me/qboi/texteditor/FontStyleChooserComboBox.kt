@file:Suppress("unused")

package me.qboi.texteditor

import java.awt.*
import java.util.*
import javax.swing.*
import javax.swing.plaf.basic.BasicComboBoxEditor
import javax.swing.text.AttributeSet
import javax.swing.text.BadLocationException
import javax.swing.text.PlainDocument
import kotlin.math.max
import kotlin.math.min

/**
 * Combobox which lists all installed fonts, sorted alphabetically. In the
 * dropdown, each font name is shown in the default font together with some
 * characters in its own font, which can be customized calling the
 * `setPreviewString` method.
 *
 * In the main text field, the default font is used to display the font name. It
 * is editable and supports auto-completion.
 *
 * The last `n` selected fonts can be shown on the top by calling
 * `setRecentFontsCount(n)`.
 *
 * This file is public domain. However, if you improve it, please share your
 * work with andi@xenoage.com. Thanks!
 *
 * @author Andreas Wenger
 */
@Suppress("KDocUnresolvedReference")
class FontStyleChooserComboBox : JComboBox<Any?>() {
    private var previewFontSize: Int
    /**
     * Gets the preview characters, or null.
     */
    /**
     * Sets the preview characters, or the empty string or null to display no
     * preview but only the font names.
     */
    var previewString: String? = "AaBbCc"
        set(previewString) {
            field = if (previewString != null
                && previewString.isNotEmpty()
            ) previewString else null
            updateList(selectedFontName)
        }

    /**
     * Gets the number of recently selected fonts, or 0.
     */
    val recentFontsCount = 5
    private val fontNames: List<String>
    private val itemsCache = HashMap<String, Item>()

    /**
     * Creates a new [FontStyleChooserComboBox] with the given preview string.
     */
    init {
        // load available font names
        val fontNames = arrayOf(
            "Plain", "Bold", "Italic",
            "Bold Italic"
        )
        Arrays.sort(fontNames)
        this.fontNames = listOf(*fontNames)

        // fill combo box
        val label = JLabel()
        previewFontSize = label.font.size
        updateList(null)

        // set editor and item components
        setEditable(true)
        setEditor(FontChooserComboBoxEditor())
        setRenderer(FontChooserComboBoxRenderer())
    }

    /**
     * Gets the font size of the preview characters.
     */
    fun getPreviewFontSize(): Int {
        return previewFontSize
    }

    /**
     * Sets the font size of the preview characters.
     */
    fun setPreviewFontSize(previewFontSize: Int) {
        this.previewFontSize = previewFontSize
        updateList(selectedFontName)
    }

    private fun updateList(selectedFontName: String?) {
        // list items
        removeAllItems()
        itemsCache.clear()

        // regular items
        for (fontName in fontNames) {
            val item = Item(fontName)
            addItem(item)
            itemsCache[fontName] = item
        }
        // reselect item
        selectedFontName?.let { setSelectedItem(it) }
    }

    /**
     * Gets the selected font name, or null.
     */
    val selectedFontName: String?
        get() = if (this.selectedItem != null) (this.selectedItem as Item).font.fontName else null

    override fun getPreferredSize(): Dimension {
        // default height: like a normal combo box
        return Dimension(
            super.getPreferredSize().width,
            JComboBox<Any?>().preferredSize.height
        )
    }

    /**
     * Sets the selected font by the given name. If it does not exist, nothing
     * happens.
     */
    fun setSelectedItem(fontName: String) {
        val item = itemsCache[fontName] // then in regular items
        if (item != null) selectedItem = item
    }

    /**
     * The editor component of the list. This is an editable text area which
     * supports auto-completion.
     *
     * @author Andreas Wenger
     */
    internal inner class FontChooserComboBoxEditor internal constructor() : BasicComboBoxEditor() {
        /**
         * Plain text document for the text area. Needed for text selection.
         *
         * Inspired by http://www.java2s.com/Code/Java/Swing-Components/
         * AutocompleteComboBox.htm
         *
         * @author Andreas Wenger
         */
        internal inner class AutoCompletionDocument : PlainDocument() {
            private val textField = this@FontChooserComboBoxEditor.editor

            @Throws(BadLocationException::class)
            override fun replace(
                i: Int, j: Int, s: String,
                attributeset: AttributeSet?
            ) {
                super.remove(i, j)
                insertString(i, s, attributeset)
            }

            @Throws(BadLocationException::class)
            override fun insertString(i: Int, s: String, attributeset: AttributeSet?) {
                if ("" != s) {
                    val s1 = getText(0, i)
                    var s2 = getMatch(s1 + s)
                    var j = i + s.length - 1
                    if (s2 == null) {
                        s2 = getMatch(s1)
                        j--
                    }
                    if (s2 != null) this@FontStyleChooserComboBox.setSelectedItem(s2)
                    super.remove(0, length)
                    super.insertString(0, s2, attributeset)
                    textField.selectionStart = j + 1
                    textField.selectionEnd = length
                }
            }

            @Throws(BadLocationException::class)
            override fun remove(i: Int, j: Int) {
                var k = textField.selectionStart
                if (k > 0) k--
                val s = getMatch(getText(0, k))
                super.remove(0, length)
                super.insertString(0, s, null)
                if (s != null) this@FontStyleChooserComboBox.setSelectedItem(s)
                try {
                    textField.selectionStart = k
                    textField.selectionEnd = length
                } catch (_: Exception) {
                }
            }
        }

        init {
            editor.document = AutoCompletionDocument()
            if (fontNames.isNotEmpty()) editor.text = fontNames[0]
        }

        private fun getMatch(input: String): String? {
            for (fontName in fontNames) {
                if (fontName.lowercase(Locale.getDefault())
                        .startsWith(input.lowercase(Locale.getDefault()))
                ) return fontName
            }
            return null
        }

        fun replaceSelection(s: String) {
            val doc = editor
                .document as AutoCompletionDocument
            try {
                val caret = editor.caret
                val i = min(caret.dot, caret.mark)
                val j = max(caret.dot, caret.mark)
                doc.replace(i, j - i, s, null)
            } catch (_: BadLocationException) {
            }
        }
    }

    /**
     * The renderer for a list item.
     *
     * @author Andreas Wenger
     */
    internal inner class FontChooserComboBoxRenderer : ListCellRenderer<Any?> {
        override fun getListCellRendererComponent(
            list: JList<*>, value: Any?,
            index: Int, isSelected: Boolean, cellHasFocus: Boolean
        ): Component {
            // extract the component from the item's value
            val item = value as Item
            val s = isSelected && !item.isSeparator
            item.background = if (s) list.selectionBackground else list
                .background
            item.foreground = if (s) list.selectionForeground else list
                .foreground
            return item
        }
    }

    fun changeStyle(fontName: String): Font? {
        var font: Font? = null
        if (fontName.lowercase(Locale.getDefault()) == "plain") {
            font = JLabel().font.deriveFont(Font.PLAIN)
        } else if (fontName.lowercase(Locale.getDefault()) == "bold") {
            font = JLabel().font.deriveFont(Font.BOLD)
        } else if (fontName.lowercase(Locale.getDefault()) == "italic") {
            font = JLabel().font.deriveFont(Font.ITALIC)
        } else if (fontName.lowercase(Locale.getDefault()) == "bold italic") {
            font = JLabel().font.deriveFont(
                Font.BOLD or Font.ITALIC
            )
        }
        return font
    }

    /**
     * The component for a list item.
     *
     * @author Andreas Wenger
     */
    internal inner class Item internal constructor(fontName: String?) : JPanel() {
        private var font1: Font? = null
        internal var isSeparator = false

        init {
            if (fontName != null) {
                font1 = changeStyle(fontName)
                isSeparator = false
            } else {
                this.font1 = null
                isSeparator = true
            }
            this.isOpaque = true
            if (!isSeparator) {
                this.layout = FlowLayout(FlowLayout.LEFT)
                val labelFont = JLabel(fontName)
                labelFont.font = font1
                this.add(labelFont)
            } else {
                // separator
                this.layout = BorderLayout()
                this.add(
                    JSeparator(JSeparator.HORIZONTAL),
                    BorderLayout.CENTER
                )
            }
        }

        override fun toString(): String {
            return font1?.family ?: ""
        }
    }
}