package me.qboi.texteditor

import me.qboi.texteditor.dialog.font.FontChooserDialog
import me.qboi.texteditor.dialog.settings.SettingsDialog
import me.qboi.texteditor.intellijthemes.IJThemesPanel
import me.qboi.texteditor.util.SearchEngine
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.print.PrinterJob
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.Charset
import javax.imageio.ImageIO
import javax.swing.*

// Java Program to create a text editor using java
internal class Editor(file: File?) {
    private var fileMenu: JMenu
    private var newFileItem: JMenuItem
    private var openFileItem: JMenuItem
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
    private var settingsItem: JMenuItem

    private var lastFile: File? = null
    private var lastSavedText: String = ""
    private val unsavedChanges: Boolean
        get() {
            return lastSavedText != textArea.text
        }

    // Text component
    private var textArea: JTextArea = JTextArea()

    // Frame
    private var frame: JFrame = JFrame("Text Editor")

    private val desktop = Desktop.getDesktop()

//    private val themeSettings = ThemeSettings.getInstance()

    // Constructor
    init {
//        themeSettings.isSystemPreferencesEnabled = true
//        themeSettings.isThemeFollowsSystem = true
//        themeSettings.isAccentColorFollowsSystem = true
//        themeSettings.isFontSizeFollowsSystem = true
//        themeSettings.isSelectionColorFollowsSystem = true

        themesPanel = IJThemesPanel()

        frame.iconImage = ImageIO.read(javaClass.getResource("/me/qboi/texteditor/icons/icon.png"))

        // Create a menu-bar
        val menuBar = JMenuBar()

        // Frame window listener.
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                close()
            }
        })

        // Create 'file' menu items
        fileMenu = JMenu("File")
        newFileItem =
            JMenuItem(action("New") { newFile() }).also { it.accelerator = KeyStroke.getKeyStroke("control N") }
        openFileItem =
            JMenuItem(action("Open") { open() }).also { it.accelerator = KeyStroke.getKeyStroke("control O") }
        saveFileItem =
            JMenuItem(action("Save") { save() }).also { it.accelerator = KeyStroke.getKeyStroke("control S") }
        saveAsFileItem = JMenuItem(action("Save As") { saveAs() }).also {
            it.accelerator = KeyStroke.getKeyStroke("control shift S")
        }
        pageSetupFileItem = JMenuItem(action("Page Setup") { pageSetup() })
        printFileItem =
            JMenuItem(action("Print") { printFile() }).also { it.accelerator = KeyStroke.getKeyStroke("control P") }
        closeItem = JMenuItem(action("Close") { close() }).also { it.accelerator = KeyStroke.getKeyStroke("alt F4") }

        // Create 'edit' menu items
        editMenu = JMenu("Edit")
        cutItem =
            JMenuItem(action("Cut") { textArea.cut() }).also { it.accelerator = KeyStroke.getKeyStroke("control X") }
        copyItem =
            JMenuItem(action("Copy") { textArea.copy() }).also { it.accelerator = KeyStroke.getKeyStroke("control C") }
        pasteItem = JMenuItem(action("Paste") { textArea.paste() }).also {
            it.accelerator = KeyStroke.getKeyStroke("control V")
        }
        deleteItem = JMenuItem(action("Delete") { textArea.replaceSelection("") }).also {
            it.accelerator = KeyStroke.getKeyStroke("DELETE")
        }
        selectAllItem = JMenuItem(action("Select All") { textArea.selectAll() }).also {
            it.accelerator = KeyStroke.getKeyStroke("control A")
        }

        // Create 'open in' menu items
        openInMenu = JMenu("Open in...")
        openInGoogleItem =
            JMenuItem(action("Google") { textArea.selectedText?.let { openIn(SearchEngine.GOOGLE, it) } })
        openInBingItem = JMenuItem(action("Bing") { textArea.selectedText?.let { openIn(SearchEngine.BING, it) } })
        openInYahooItem = JMenuItem(action("Yahoo") { textArea.selectedText?.let { openIn(SearchEngine.YAHOO, it) } })
        openInYandexItem =
            JMenuItem(action("Yandex") { textArea.selectedText?.let { openIn(SearchEngine.YANDEX, it) } })
        openInDuckDuckGoItem =
            JMenuItem(action("DuckDuckGo") { textArea.selectedText?.let { openIn(SearchEngine.DUCKDUCKGO, it) } })
        openInYouTubeItem =
            JMenuItem(action("YouTube") { textArea.selectedText?.let { openIn(SearchEngine.YOUTUBE, it) } })

        // Create 'view' menu items
        viewMenu = JMenu("View")
        wordWrapItem = JCheckBoxMenuItem(action("Word Wrap") { toggleWordWrap() })
        fontItem = JMenuItem(action("Font") { configureFont() })
        settingsItem = JMenuItem(action("Settings") { configureTheme() })

        // Add items to their corresponding menu.
        fileMenu.add(newFileItem)
        fileMenu.add(openFileItem)
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
        viewMenu.add(settingsItem)

        // Add menus to menu bar
        menuBar.add(fileMenu)
        menuBar.add(editMenu)
        menuBar.add(viewMenu)

        // Scrollable text area.
        val scrollPane =
            JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        textArea.isEditable = true
        textArea.wrapStyleWord = true
        textArea.border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
        scrollPane.border = BorderFactory.createEmptyBorder()

//        Settings.theme = themeSettings.exportConfiguration()
//        Settings.theme = "FlatLaf IntelliJ"
//        Settings.font = textArea.font
//        Settings.init()

//        themeSettings.setConfiguration(Settings.theme)
//        themeSettings.apply()

        wordWrapItem.state = Settings.wordWrap
        textArea.lineWrap = Settings.wordWrap
        textArea.font = Settings.font

        // Frame properties.
        frame.jMenuBar = menuBar
        frame.add(scrollPane)
        frame.setSize(500, 500)

        frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE

        frame.setLocation(
            (Toolkit.getDefaultToolkit().screenSize.width - frame.width) / 2,
            (Toolkit.getDefaultToolkit().screenSize.height - frame.height) / 2
        )

        if (file != null) {
            openFile(file)
        }

        frame.isVisible = true
    }

    private fun configureTheme() {
//        ThemeSettings.showSettingsDialog(frame, Dialog.ModalityType.APPLICATION_MODAL)
//        val config = themeSettings.exportConfiguration()
//        if (Settings.theme != config) {
//            Settings.theme = config
//            Settings.save()
//        }

        SettingsDialog(frame, "Settings", true).apply {
            setLocationRelativeTo(frame)
            isVisible = true
        }
    }

    private fun configureFont() {
        val fontChooser = FontChooserDialog(frame, modal = true, font = textArea.font)
        fontChooser.isVisible = true
        if (!fontChooser.isCancelled) {
            textArea.font = fontChooser.selectedFont
            Settings.font = textArea.font
        }
    }

    private fun toggleWordWrap() {
        textArea.lineWrap = !textArea.lineWrap
        wordWrapItem.state = textArea.lineWrap
        Settings.wordWrap = textArea.lineWrap
    }

    private fun close() {
        if (checkForUnsavedChanges()) return
        frame.dispose()
    }

    private fun printFile() {
        textArea.print()
    }

    private fun pageSetup() {
        val printJob = PrinterJob.getPrinterJob()
        printJob.pageDialog(printJob.defaultPage())
    }

    fun open() {
        val fileChooser = JFileChooser()
        fileChooser.showOpenDialog(frame)
        openFile(fileChooser.selectedFile)
    }

    fun openFile(file: File?) {
        if (checkForUnsavedChanges()) return
        if (file != null) {
            textArea.text = try {
                file.readText()
            } catch (e: IOException) {
                JOptionPane.showMessageDialog(frame, "Error opening file.\n${e.localizedMessage}", "Error",
                    JOptionPane.ERROR_MESSAGE)
                return
            }
            lastFile = file
            lastSavedText = textArea.text
        }
    }

    private fun newFile() {
        if (checkForUnsavedChanges()) return
        textArea.text = ""
        lastSavedText = textArea.text
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

    private fun action(name: String, function: () -> Unit): Action {
        return object : AbstractAction(name) {
            override fun actionPerformed(e: ActionEvent) {
                function()
            }
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
        fi: File, text: String = textArea.text, encoding: Charset = Charset.defaultCharset(),
        saveAsCopy: Boolean = false
    ): Boolean {
        try {
            // Create a file writer
            val wr = FileWriter(fi, encoding, false)

            // Create buffered writer to write
            val w = BufferedWriter(wr)

            // Write
            w.write(text)
            w.flush()
            w.close()

            if (!saveAsCopy) {
                lastSavedText = text
                lastFile = fi
            }
            return !unsavedChanges
        } catch (evt: Exception) {
            JOptionPane.showMessageDialog(frame, evt.message)
        }
        return false
    }

    private fun checkForUnsavedChanges(): Boolean {
        if (unsavedChanges) {
            val dialog = JOptionPane.showConfirmDialog(frame, "Do you want to save changes?", "Save",
                JOptionPane.YES_NO_CANCEL_OPTION)
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

//    // If a button is pressed
//    override fun actionPerformed(e: ActionEvent) {
//        val s = e.actionCommand
//        when (s) {
//            "Cut" -> {
//                textArea.cut()
//            }
//            "Copy" -> {
//                textArea.copy()
//            }
//            "Paste" -> {
//                textArea.paste()
//            }
//            "Select All" -> {
//                textArea.selectAll()
//            }
//            "Save" -> {
//                save()
//            }
//            "Print" -> {
//                try {
//                    // print the file
//                    textArea.print()
//                } catch (evt: Exception) {
//                    JOptionPane.showMessageDialog(frame, evt.message)
//                }
//            }
//            "Open" -> {
//                // Create an object of JFileChooser class
//                val j = JFileChooser("f:")
//
//                // Invoke the showsOpenDialog function to show the save dialog
//                val r = j.showOpenDialog(null)
//
//                // If the user selects a file
//                if (r == JFileChooser.APPROVE_OPTION) {
//                    // Set the label to the path of the selected directory
//                    val fi = File(j.selectedFile.absolutePath)
//                    try {
//                        // String
//                        var s1: String
//                        var sl: String
//
//                        // File reader
//                        val fr = FileReader(fi)
//
//                        // Buffered reader
//                        val br = BufferedReader(fr)
//
//                        // Initialize sl
//                        sl = br.readLine()
//
//                        // Take the input from the file
//                        while (br.readLine().also { s1 = it } != null) {
//                            sl = """
//                                $sl
//                                $s1
//                                """.trimIndent()
//                        }
//
//                        // Set the text
//                        textArea.text = sl
//                    } catch (evt: Exception) {
//                        JOptionPane.showMessageDialog(frame, evt.message)
//                    }
//                } else JOptionPane.showMessageDialog(frame, "the user cancelled the operation")
//            }
//            "New" -> {
//                textArea.text = ""
//            }
//            "Close" -> {
//                frame.isVisible = false
//            }
//            "Word Wrap" -> {
//                textArea.lineWrap = !textArea.lineWrap
//                wordWrapItem.state = textArea.lineWrap
//            }
//        }
//    }

    companion object {
        lateinit var themesPanel: IJThemesPanel
            private set
    }
}