package com.alvarengadev.marketplacelist.utils.settings

import android.content.Context
import com.alvarengadev.marketplacelist.BuildConfig
import com.alvarengadev.marketplacelist.R
import com.alvarengadev.marketplacelist.utils.enums.TypeOptionSettings
import kotlin.collections.ArrayList

class SettingsUtils {

    private var context: Context? = null

    fun getInstance(context: Context?) {
        this.context = context
    }

    data class Options(
        val title: String?,
        val description: String?,
        val typeOptionSettings: TypeOptionSettings? = null
    )

    fun getListSettings(): ArrayList<Options> {
        val listOptions = ArrayList<Options>()
        listOptions.add(
            Options(
                context?.getString(R.string.item_settings_option_title_general),
                context?.getString(R.string.item_settings_option_description_general),
                TypeOptionSettings.SETTINGS_GENERAL
            )
        )
        listOptions.add(
            Options(
                context?.getString(R.string.item_settings_option_title_about),
                null,
                TypeOptionSettings.SETTINGS_ABOUT
            )
        )
        return listOptions
    }

    fun getListSettingsGeneral(
        theme: String,
        currency: String
    ): ArrayList<Options> {
        val listOptions = ArrayList<Options>()
        listOptions.add(
            Options(
                context?.getString(R.string.item_general_option_title_theme),
                context?.getString(R.string.item_general_option_description, theme),
                TypeOptionSettings.GENERAL_THEME
            )
        )
        listOptions.add(
            Options(
                context?.getString(R.string.item_general_option_title_currency),
                context?.getString(R.string.item_general_option_description, currency),
                TypeOptionSettings.GENERAL_CURRENCY
            )
        )
        return listOptions
    }

    fun getListSettingsAbout(): ArrayList<Options> {
        val listOptions = ArrayList<Options>()
        listOptions.add(
            Options(
                context?.getString(R.string.item_about_option_title_privacy_policy),
                context?.getString(R.string.item_about_option_description_privacy_policy),
                TypeOptionSettings.ABOUT_PRIVACY_POLICY
            )
        )
        listOptions.add(
            Options(
                context?.getString(R.string.item_about_option_title_version),
                BuildConfig.VERSION_NAME
            )
        )
        return listOptions
    }
}