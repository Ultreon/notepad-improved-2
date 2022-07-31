package me.qboi.texteditor.gson

import com.github.weisj.darklaf.settings.SettingsConfiguration
import com.google.gson.InstanceCreator
import java.lang.reflect.Type

object SettingsConfigurationInstanceCreator : InstanceCreator<SettingsConfiguration> {
    override fun createInstance(type: Type?): SettingsConfiguration {
        return SettingsConfiguration()
    }
}
