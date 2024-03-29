@file:Suppress("unused")

package com.ultreon.notepadimproved

import com.ultreon.notepadimproved.resource.Res
import java.awt.Image

val appBannerRef: String = Res.require("/images/banner.png")
val appBanner: Image = Res.loadImage("/images/banner.png")
val appIconRef: String = Res.require("/icons/app.png")
val appIcon: Image = Res.loadImage("/icons/app.png")
val appIcon16XRef: String = Res.require("/icons/app-16x.png")
val appIcon16X: Image = Res.loadImage("/icons/app-16x.png")
