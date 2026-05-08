package com.example.criapp.services

import com.example.criapp.CriAppDropdown
import com.example.criapp.R

class SpinnerHelper {
    companion object {

        fun configCriAppSpinner(dropdown: CriAppDropdown, optionsResId: Int) {
            val options = dropdown.context.resources.getStringArray(optionsResId).toList()
            dropdown.setup(options)
        }
    }
}