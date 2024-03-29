package com.ultreon.notepadimproved.main

import com.ultreon.notepadimproved.action
import com.ultreon.notepadimproved.dialog.font.FontChooserDialog
import com.ultreon.notepadimproved.intellijthemes.IJThemesPanel
import com.ultreon.notepadimproved.lang.Language
import com.ultreon.notepadimproved.util.SearchEngine
import com.ultreon.notepadimproved.util.Translatable
import java.awt.Desktop
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.print.PrinterJob
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.event.InternalFrameAdapter
import javax.swing.event.InternalFrameEvent

/**
 * Java Program to create a text editor using java
 */
internal class Editor(file: File?, private val mainFrame: MainFrame) : JInternalFrame("Text Editor"), Translatable {
    private val baseTitle = "Editor Instance"
    private var fileMenu: JMenu
    private var newFileItem: JMenuItem
    private var openFileItem: JMenuItem
    private var recentFilesMenu: JMenu
    private var saveFileItem: JMenuItem
    private var saveAsFileItem: JMenuItem
    private var pageSetupFileItem: JMenuItem
    private var printFileItem: JMenuItem
    private var closeItem: JMenuItem
    private var editMenu: JMenu
    private var cutItem: JMenuItem
    private var copyItem: JMenuItem
    private var pasteItem: JMenuItem
    private var deleteItem: JMenuItem
    private var selectAllItem: JMenuItem
    private var openInMenu: JMenuItem
    private var openInGoogleItem: JMenuItem
    private var openInBingItem: JMenuItem
    private var openInYahooItem: JMenuItem
    private var openInYandexItem: JMenuItem
    private var openInDuckDuckGoItem: JMenuItem
    private var openInYouTubeItem: JMenuItem
    private var viewMenu: JMenu
    private var wordWrapItem: JCheckBoxMenuItem
    private var fontItem: JMenuItem

    private var lastFile: File? = null
    private var lastSavedText: String = ""
    val unsavedChanges: Boolean
        get() {
            return lastSavedText != editor.text
        }

    // Text component
    private var editor: JTextArea = JTextArea()

    // Frame
    private val desktop = Desktop.getDesktop()

    // Constructor
    init {
        themesPanel = IJThemesPanel()

        val read = ImageIO.read(javaClass.getResource("/icons/app-16x.png"))
        this.frameIcon = ImageIcon(read)
        this.iconable = true
        this.isDoubleBuffered = true
        this.isRequestFocusEnabled = true

        // Create a menu-bar
        val menuBar = JMenuBar()

        // Frame window listener.
        this.addInternalFrameListener(object : InternalFrameAdapter() {
            override fun internalFrameClosing(e: InternalFrameEvent) {
                close()
            }
        })

        // Create 'file' menu items
        fileMenu = JMenu(action("editor.menu_bar.file"))
        newFileItem = JMenuItem(action("editor.menu_bar.file.new") { newFile() }).also {
            it.accelerator = KeyStroke.getKeyStroke("control shift N")
        }
        openFileItem =
            JMenuItem(action("editor.menu_bar.file.open") { open() }).also { it.accelerator =
                KeyStroke.getKeyStroke("control shift O")
            }
        recentFilesMenu = JMenu(action("editor.menu_bar.file.recent"))
        saveFileItem =
            JMenuItem(action("editor.menu_bar.file.save") { save() }).also { it.accelerator =
                KeyStroke.getKeyStroke("control S")
            }
        saveAsFileItem = JMenuItem(action("editor.menu_bar.file.save.as") { saveAs() }).also {
            it.accelerator = KeyStroke.getKeyStroke("control shift S")
        }
        pageSetupFileItem = JMenuItem(action("editor.menu_bar.file.print.page_setup") { pageSetup() })
        printFileItem =
            JMenuItem(action("editor.menu_bar.file.print") { printFile() }).also { it.accelerator =
                KeyStroke.getKeyStroke("control P")
            }
        closeItem = JMenuItem(action("editor.menu_bar.file.close") { close() }).also { it.accelerator =
            KeyStroke.getKeyStroke("control W")
        }

        // Create 'edit' menu items
        editMenu = JMenu(action("editor.menu_bar.edit"))
        cutItem =
            JMenuItem(action("editor.menu_bar.edit.cut") { editor.cut() }).also { it.accelerator =
                KeyStroke.getKeyStroke("control X")
            }
        copyItem =
            JMenuItem(action("editor.menu_bar.edit.copy") { editor.copy() }).also { it.accelerator =
                KeyStroke.getKeyStroke("control C")
            }
        pasteItem =
            JMenuItem(action("editor.menu_bar.edit.paste") { editor.paste() }).also { it.accelerator =
                KeyStroke.getKeyStroke("control V")
            }
        deleteItem = JMenuItem(action("editor.menu_bar.edit.delete") { editor.replaceSelection("") }).also {
            it.accelerator = KeyStroke.getKeyStroke("DELETE")
        }
        selectAllItem = JMenuItem(action("editor.menu_bar.edit.select_all") { editor.selectAll() }).also {
            it.accelerator = KeyStroke.getKeyStroke("control A")
        }

        // Create 'open in' menu items
        openInMenu = JMenu(action("editor.menu_bar.edit.open_in"))
        openInGoogleItem = JMenuItem(action("Google") {
            editor.selectedText?.let {
                openIn(
                    SearchEngine.GOOGLE,
                    it
                )
            }
        })
        openInBingItem =
            JMenuItem(action("Bing") { editor.selectedText?.let { openIn(SearchEngine.BING, it) } })
        openInYahooItem = JMenuItem(action("Yahoo") {
            editor.selectedText?.let {
                openIn(
                    SearchEngine.YAHOO,
                    it
                )
            }
        })
        openInYandexItem = JMenuItem(action("Yandex") {
            editor.selectedText?.let {
                openIn(
                    SearchEngine.YANDEX,
                    it
                )
            }
        })
        openInDuckDuckGoItem =
            JMenuItem(action("DuckDuckGo") {
                editor.selectedText?.let {
                    openIn(
                        SearchEngine.DUCKDUCKGO,
                        it
                    )
                }
            })
        openInYouTubeItem =
            JMenuItem(action("YouTube") {
                editor.selectedText?.let {
                    openIn(
                        SearchEngine.YOUTUBE,
                        it
                    )
                }
            })
        openInYouTubeItem =
            JMenuItem(action("Wikipedia") {
                editor.selectedText?.let {
                    openIn(
                        SearchEngine.WIKIPEDIA,
                        it
                    )
                }
            })
        openInYouTubeItem = JMenuItem(action("GitHub") {
            editor.selectedText?.let {
                openIn(
                    SearchEngine.GITHUB,
                    it
                )
            }
        })

        // Create 'view' menu items
        viewMenu = JMenu(action("editor.menu_bar.view"))
        wordWrapItem = JCheckBoxMenuItem(action("editor.menu_bar.view.word_wrap") { toggleWordWrap() })
        fontItem = JMenuItem(action("editor.menu_bar.view.font") { configureFont() })

        // Add items to their corresponding menu.
        fileMenu.add(newFileItem)
        fileMenu.add(openFileItem)
        fileMenu.add(recentFilesMenu)
        fileMenu.add(saveFileItem)
        fileMenu.add(saveAsFileItem)
        fileMenu.addSeparator()
        fileMenu.add(pageSetupFileItem)
        fileMenu.add(printFileItem)
        fileMenu.addSeparator()
        fileMenu.add(closeItem)
        openInMenu.add(openInGoogleItem)
        openInMenu.add(openInBingItem)
        openInMenu.add(openInYahooItem)
        openInMenu.add(openInYandexItem)
        openInMenu.add(openInDuckDuckGoItem)
        openInMenu.add(openInYouTubeItem)
        editMenu.add(cutItem)
        editMenu.add(copyItem)
        editMenu.add(pasteItem)
        editMenu.add(deleteItem)
        editMenu.addSeparator()
        editMenu.add(selectAllItem)
        editMenu.addSeparator()
        editMenu.add(openInMenu)
        viewMenu.add(wordWrapItem)
        viewMenu.add(fontItem)

        // Add menus to menu bar
        menuBar.add(fileMenu)
        menuBar.add(editMenu)
        menuBar.add(viewMenu)

        // Scrollable text area.
        val scrollPane =
            JScrollPane(editor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        editor.isEditable = true
        editor.wrapStyleWord = true
        editor.border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
        scrollPane.border = BorderFactory.createEmptyBorder()

        Settings.recentFiles.forEach { recentFilesMenu.add(JMenuItem(action(it.name) { openFile(it) })) }

        wordWrapItem.state = Settings.wordWrap
        editor.lineWrap = Settings.wordWrap
        editor.font = Settings.font
        editor.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                updateTitle()
            }

            override fun keyReleased(e: KeyEvent?) {
                updateTitle()
            }
        })

        // Frame properties.
        this.jMenuBar = menuBar
        this.add(scrollPane)
        this.setSize(500, 500)

        this.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE

        this.maximizable = true
        this.resizable = true
        this.isClosable = true
        this.isIconifiable = true

        this.setLocation((mainFrame.width - this.width) / 2, (mainFrame.height - this.height) / 2)

        if (file != null) {
            openFile(file)
        }

        updateTitle()

        this.isVisible = true
    }

    private fun addToRecentFiles(file: File) {
        val recentFiles = Settings.recentFiles.toMutableList()
        recentFiles.remove(file)
        recentFiles.add(0, file)
        if (recentFiles.size > 10) {
            recentFiles.removeAt(10)
        }
        recentFilesMenu.removeAll()
        recentFiles.forEach { recentFilesMenu.add(JMenuItem(action(it.name) { openFile(it) })) }
        Settings.recentFiles = recentFiles.toList()

        this.`hello world this is with spaces`()
    }

    fun `hello world this is with spaces`() {

    }

    private fun configureFont() {
        val fontChooser = FontChooserDialog(mainFrame, modal = true, font = editor.font)
        fontChooser.isVisible = true
        if (!fontChooser.isCancelled) {
            editor.font = fontChooser.selectedFont
            Settings.font = editor.font
        }
    }

    private fun toggleWordWrap() {
        editor.lineWrap = !editor.lineWrap
        wordWrapItem.state = editor.lineWrap
        Settings.wordWrap = editor.lineWrap
    }

    private fun close() {
        if (checkForUnsavedChanges()) return
        this.dispose()
    }

    private fun printFile() {
        editor.print()
    }

    private fun pageSetup() {
        val printJob = PrinterJob.getPrinterJob()
        printJob.pageDialog(printJob.defaultPage())
    }

    fun open() {
        val fileChooser = JFileChooser()
        fileChooser.showOpenDialog(this)
        openFile(fileChooser.selectedFile)
    }

    fun openFile(file: File?) {
        if (checkForUnsavedChanges()) return
        if (file != null) {
            editor.text = try {
                file.readText()
            } catch (e: IOException) {
                JOptionPane.showMessageDialog(
                    this, "Error opening file.\n${e.localizedMessage}", "Error",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
            updateTitle()
            editor.caretPosition = 0
            addToRecentFiles(file)
            lastFile = file
            lastSavedText = editor.text
        }
    }

    private fun updateTitle() {
        val indicator = if (unsavedChanges) " (Modified)" else ""

        lastFile?.let {
            this.title = "$baseTitle - ${it.name}$indicator"
        } ?: run {
            this.title = "$baseTitle - Untitled$indicator"
        }
    }

    private fun newFile() {
        if (checkForUnsavedChanges()) return
        editor.text = ""
        lastSavedText = editor.text
        lastFile = null
    }

    private fun openIn(searchEngine: SearchEngine, selectedText: String) {
        val search = searchEngine.search(selectedText)
        try {
            desktop.browse(search)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun save(): Boolean {
        return lastFile?.let {
            return@save saveTo(it)
        } ?: run {
            return@save saveAs()
        }
    }

    fun saveAs(): Boolean {
        // Create an object of JFileChooser class
        val j = JFileChooser()

        // Invoke the showsSaveDialog function to show the save dialog
        val r = j.showSaveDialog(null)
        return if (r == JFileChooser.APPROVE_OPTION) {
            // Set the label to the path of the selected directory
            val fi = File(j.selectedFile.absolutePath)
            saveTo(fi)
        } else {
            false
        }
    }

    private fun saveTo(
        file: File, text: String = editor.text, saveAsCopy: Boolean = false
    ): Boolean {
        try {
            // Create a file writer
            val wr = FileWriter(file, false)

            // Create buffered writer to write
            val w = BufferedWriter(wr)

            // Write
            w.write(text)
            w.flush()
            w.close()

            if (!saveAsCopy) {
                lastSavedText = text
                lastFile = file
                addToRecentFiles(file)
                updateTitle()
            }
            return !unsavedChanges
        } catch (evt: Exception) {
            JOptionPane.showMessageDialog(this, evt.message)
        }
        return false
    }

    private fun checkForUnsavedChanges(): Boolean {
        if (unsavedChanges) {
            val dialog = JOptionPane.showConfirmDialog(
                this, "Do you want to save changes?", "Save",
                JOptionPane.YES_NO_CANCEL_OPTION
            )
            if (dialog == JOptionPane.YES_OPTION) {
                if (!save()) {
                    return true
                }
            } else if (dialog == JOptionPane.CANCEL_OPTION) {
                return true
            }
        }
        return false
    }

    companion object {
        lateinit var themesPanel: IJThemesPanel
            private set
    }

    override fun onLanguageChanged(language: Language) {
        println("Language changed in editor.")
    }
}