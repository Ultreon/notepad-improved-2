@file:Suppress("unused")

package me.qboi.texteditor

import me.qboi.texteditor.resource.Res
import java.awt.Image

val appBannerRef: String = Res.require("/images/banner.png")
val appBanner: Image = Res.loadImage("/images/banner.png")
val appIconRef: String = Res.require("/icons/app.png")
val appIcon: Image = Res.loadImage("/icons/app.png")
