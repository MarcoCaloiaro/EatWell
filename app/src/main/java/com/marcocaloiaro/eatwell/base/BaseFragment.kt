package com.marcocaloiaro.eatwell.base

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.marcocaloiaro.eatwell.R

open class BaseFragment : Fragment() {

    var alertDialog: AlertDialog? = null

    val clientId: String by lazy {
        resources.getString(R.string.foursquare_client_id)
    }
    val clientSecret: String by lazy {
        resources.getString(R.string.foursquare_client_secret)
    }
}