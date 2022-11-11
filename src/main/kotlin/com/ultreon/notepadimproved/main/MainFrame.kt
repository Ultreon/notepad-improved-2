package com.ultreon.notepadimproved.main

import com.ultreon.notepadimproved.*
import com.ultreon.notepadimproved.dialog.AboutDialog
import com.ultreon.notepadimproved.dialog.settings.SettingsDialog
import com.ultreon.notepadimproved.lang.Language
import com.ultreon.notepadimproved.util.Translatable
import java.awt.Desktop
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Point
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.event.*
import java.beans.PropertyVetoException
import java.io.File
import java.net.URI
import javax.swing.*

/*
 * InternalFrameDemo.java requires:
 *   MyInternalFrame.java
 */
class MainFrame : JFrame(appName), Translatable {
    private lateinit var issueTrackerItem: JMenuItem
    private lateinit var newIssueItem: JMenuItem
    private lateinit var aboutItem: JMenuItem
    private lateinit var helpMenu: JMenu
    private lateinit var quitItem: JMenuItem
    private lateinit var settingsItem: JMenuItem
    private lateinit var openItem: JMenuItem
    private lateinit var newWindowItem: JMenuItem
    private lateinit var windowMenu: JMenu
    var desktop: JDesktopPane

    init {
        // Set instance
        instance = this
        isRunning = true

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        val toolkit = Toolkit.getDefaultToolkit()
        val screenSize = toolkit.screenSize

        this.iconImage = appIcon

        minimumSize = Dimension(800, 450)

        val savedPos = Point(AppPrefs.windowPosX, AppPrefs.windowPosY)
        val savedSize = Dimension(AppPrefs.windowSizeWidth, AppPrefs.windowSizeHeight)
        val savedBounds = Rectangle(savedPos, savedSize)

        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        var allow = false
        for (it in ge.screenDevices) {
            val insets = toolkit.getScreenInsets(it.defaultConfiguration)
            val viewRange = it.defaultConfiguration.bounds
            viewRange.width -= insets.left + insets.right
            viewRange.height -= insets.top + insets.bottom
            viewRange.x += insets.left
            viewRange.y += insets.top
            if (viewRange.intersects(savedBounds)) {
                allow = true
            }
        }

        if (!allow || savedPos.x < 0 || savedPos.y < 0 || savedSize.width < minimumSize.width || savedSize.height < minimumSize.height) {
            this.setSize(1200, 700)
            this.setLocation(
                (screenSize.width - this.width) / 2,
                (screenSize.height - this.height) / 2
            )
        } else {
            this.size = savedSize
            this.location = savedPos
        }

        defaultCloseOperation = DO_NOTHING_ON_CLOSE

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                quit()
            }
        })

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                AppPrefs.windowSizeWidth = e.component.size.width
                AppPrefs.windowSizeHeight = e.component.size.height
            }

            override fun componentMoved(e: ComponentEvent) {
                AppPrefs.windowPosX = e.component.location.x
                AppPrefs.windowPosY = e.component.location.y
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

    private fun createMenuBar(): JMenuBar {
        val menuBar = JMenuBar()

        //Set up the lone menu.
        windowMenu = JMenu(action("main.menu_bar.window"))
        windowMenu.mnemonic = KeyEvent.VK_W
        menuBar.add(windowMenu)

        //Set up the first menu item.
        newWindowItem = JMenuItem(action("main.menu_bar.window.new") { newWindow() })
        newWindowItem.mnemonic = KeyEvent.VK_N
        newWindowItem.accelerator = KeyStroke.getKeyStroke("control N")
        windowMenu.add(newWindowItem)

        //Set up the first menu item.
        openItem = JMenuItem(action("main.menu_bar.window.open") { openFile() })
        openItem.mnemonic = KeyEvent.VK_O
        openItem.accelerator = KeyStroke.getKeyStroke("control O")
        windowMenu.add(openItem)

        //Set up the first menu item.
        settingsItem = JMenuItem(action("main.menu_bar.window.settings") { configureTheme() })
        settingsItem.mnemonic = KeyEvent.VK_O
        settingsItem.accelerator = KeyStroke.getKeyStroke("control O")
        windowMenu.add(settingsItem)

        //Set up the second menu item.
        quitItem = JMenuItem(action("main.menu_bar.window.quit") { quit() })
        quitItem.mnemonic = KeyEvent.VK_Q
        quitItem.accelerator = KeyStroke.getKeyStroke("alt F4")
        windowMenu.add(quitItem)

        helpMenu = JMenu(action("main.menu_bar.help"))
        helpMenu.mnemonic = KeyEvent.VK_H
        menuBar.add(helpMenu)

        //Set up the first menu item.
        aboutItem = JMenuItem(action("main.menu_bar.help.about") { showAbout() })
        aboutItem.mnemonic = KeyEvent.VK_A
        aboutItem.accelerator = KeyStroke.getKeyStroke("F1")
        helpMenu.add(aboutItem)

        //Set up the first menu item.
        newIssueItem = JMenuItem(action("main.menu_bar.help.issue.new") { openNewIssuePage() })
        newIssueItem.mnemonic = KeyEvent.VK_I
        newIssueItem.accelerator = KeyStroke.getKeyStroke("F8")
        helpMenu.add(newIssueItem)

        //Set up the first menu item.
        issueTrackerItem = JMenuItem(action("main.menu_bar.help.issue.tracker") { openIssueTracker() })
        issueTrackerItem.mnemonic = KeyEvent.VK_S
        issueTrackerItem.accelerator = KeyStroke.getKeyStroke("control F8")
        helpMenu.add(issueTrackerItem)
        return menuBar
    }

    /**
     * Opens the issues tracker page in the default browser.
     */
    private fun openIssueTracker() {
        Desktop.getDesktop().browse(URI(appIssues))
    }

    private fun openNewIssuePage() {
        Desktop.getDesktop().browse(URI(appNewIssue))
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

    private fun openFile() {
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
    private fun quit() {
        val allUnsavedChanges = desktop.allFrames.filter { it is Editor && it.unsavedChanges }
        if (allUnsavedChanges.isNotEmpty()) {
            val answer = JOptionPane.showConfirmDialog(
                this, "There are unsaved changes. Quit anyway?", "Quit",
                JOptionPane.YES_NO_OPTION
            )
            if (answer == JOptionPane.NO_OPTION) {
                return
            }
        }
        dispose()
    }

    //Quit the application.
    fun restart() {
        isRestart = true
        quit()
    }

    override fun dispose() {
        super.dispose()
        isRunning = false
    }

    companion object {
        var isRunning: Boolean = true
            internal set
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

    override fun onLanguageChanged(language: Language) {
//        windowMenu.text = language["main.menu_bar.window"]
//        newWindowItem.text = language["main.menu_bar.window.new_window"]
//        openItem.text = language["main.menu_bar.window.open"]
//        settingsItem.text = language["main.menu_bar.window.settings"]
//        quitItem.text = language["main.menu_bar.window.quit"]
//
//        helpMenu.text = language["main.menu_bar.help"]
//        aboutItem.text = language["main.menu_bar.help.about"]
//        newIssueItem.text = language["main.menu_bar.help.new_issue"]
//        issueTrackerItem.text = language["main.menu_bar.help.new_issue"]
    }
}