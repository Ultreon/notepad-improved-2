@file:Suppress("LeakingThis")

package me.qboi.texteditor

import me.qboi.texteditor.dialog.AboutDialog
import me.qboi.texteditor.dialog.settings.SettingsDialog
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.beans.PropertyVetoException
import java.io.File
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.*
import kotlin.system.exitProcess

/*
 * InternalFrameDemo.java requires:
 *   MyInternalFrame.java
 */
open class MainFrame : JFrame("Notepad Improved") {
    var desktop: JDesktopPane

    init {
        // Set instance
        instance = this

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        this.setSize(1200, 700)

        this.iconImage = ImageIO.read(javaClass.getResource("/me/qboi/texteditor/icons/icon-16x.png"))

        this.setLocation(
            (screenSize.width - this.width) / 2,
            (screenSize.height - this.height) / 2
        )

        defaultCloseOperation = DO_NOTHING_ON_CLOSE

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                quit()
            }
        })

        //Set up the GUI.
        desktop = JDesktopPane() //a specialized layered pane
        desktop.autoscrolls = true
        desktop.dragMode = JDesktopPane.LIVE_DRAG_MODE
        desktop.updateUI()
        newWindow() //create first "window"
        contentPane = desktop
        jMenuBar = createMenuBar()

        //Make dragging a little faster but perhaps uglier.
        desktop.dragMode = JDesktopPane.OUTLINE_DRAG_MODE
    }

    protected fun createMenuBar(): JMenuBar {
        val menuBar = JMenuBar()

        //Set up the lone menu.
        val windowMenu = JMenu("Window")
        windowMenu.mnemonic = KeyEvent.VK_W
        menuBar.add(windowMenu)

        //Set up the first menu item.
        val newWindowItem = JMenuItem("New Window")
        newWindowItem.mnemonic = KeyEvent.VK_N
        newWindowItem.accelerator = KeyStroke.getKeyStroke("control N")
        newWindowItem.action = action("New") { newWindow() }
        windowMenu.add(newWindowItem)

        //Set up the first menu item.
        val openItem = JMenuItem("Open...")
        openItem.mnemonic = KeyEvent.VK_O
        openItem.accelerator = KeyStroke.getKeyStroke("control O")
        openItem.action = action("Open") { openFile() }
        windowMenu.add(openItem)

        //Set up the first menu item.
        val settingsItem = JMenuItem("Open...")
        settingsItem.mnemonic = KeyEvent.VK_O
        settingsItem.accelerator = KeyStroke.getKeyStroke("control O")
        settingsItem.action = action("Settings") { configureTheme() }
        windowMenu.add(settingsItem)

        //Set up the second menu item.
        val quitItem = JMenuItem("Quit")
        quitItem.mnemonic = KeyEvent.VK_Q
        quitItem.accelerator = KeyStroke.getKeyStroke("alt F4")
        quitItem.action = action("Quit") { quit() }
        windowMenu.add(quitItem)

        val helpMenu = JMenu("Help")
        windowMenu.mnemonic = KeyEvent.VK_H
        menuBar.add(helpMenu)

        //Set up the first menu item.
        val aboutItem = JMenuItem("About")
        aboutItem.mnemonic = KeyEvent.VK_A
        aboutItem.accelerator = KeyStroke.getKeyStroke("F1")
        aboutItem.action = action("About") { showAbout() }
        helpMenu.add(aboutItem)

        //Set up the first menu item.
        val newIssueItem = JMenuItem("New Issue")
        newIssueItem.mnemonic = KeyEvent.VK_I
        newIssueItem.accelerator = KeyStroke.getKeyStroke("F8")
        newIssueItem.action = action("New Issue") { openNewIssuePage() }
        helpMenu.add(newIssueItem)

        //Set up the first menu item.
        val issueTrackerItem = JMenuItem("Issue Tracker")
        issueTrackerItem.mnemonic = KeyEvent.VK_S
        issueTrackerItem.accelerator = KeyStroke.getKeyStroke("control F8")
        issueTrackerItem.action = action("Issue Tracker") { openIssueTracker() }
        helpMenu.add(issueTrackerItem)

        return menuBar
    }

    /**
     * Opens the issues tracker page in the default browser.
     */
    private fun openIssueTracker() {
        Desktop.getDesktop().browse(URI(References.ISSUES_URL))
    }

    private fun openNewIssuePage() {
        Desktop.getDesktop().browse(URI(References.NEW_ISSUE_URL))
    }

    private fun showAbout() {
        AboutDialog(this, "About Notepad Improved", true).apply {
            setLocationRelativeTo(this@MainFrame)
            isVisible = true
        }
    }

    private fun configureTheme() {
        SettingsDialog(this, "Settings", true).apply {
            setLocationRelativeTo(this@MainFrame)
            isVisible = true
        }
    }

    protected fun openFile() {
        val fileChooser = JFileChooser()
        fileChooser.showOpenDialog(this)
        val selectedFile = fileChooser.selectedFile
        selectedFile?.let {
            openFile(it)
        }
    }

    private fun openFile(selectedFile: File) {
        newWindow(selectedFile)
    }

    private fun newWindow(selectedFile: File? = null) {
        val editor = Editor(selectedFile, this)
        editor.isVisible = true //necessary as of 1.3
        desktop.add(editor)
        try {
            editor.isSelected = true
        } catch (_: PropertyVetoException) {
        }
    }

    //Quit the application.
    protected fun quit() {
        val allUnsavedChanges = desktop.allFrames.filter { it is Editor && it.unsavedChanges }
        if (allUnsavedChanges.isNotEmpty()) {
            val answer = JOptionPane.showConfirmDialog(this, "There are unsaved changes. Quit anyway?", "Quit",
                JOptionPane.YES_NO_OPTION)
            if (answer == JOptionPane.NO_OPTION) {
                return
            }
        }
        exitProcess(0)
    }

    companion object {
        lateinit var instance: MainFrame
            private set

        /**
         * Create the GUI and show it.  For thread safety,
         * this method should be invoked from the
         * event-dispatching thread.
         */
        fun start() {
            //Make sure we have nice window decorations.
            setDefaultLookAndFeelDecorated(true)

            //Create and set up the window.
            val frame = MainFrame()

            //Display the window.
            frame.isVisible = true
        }
    }
}