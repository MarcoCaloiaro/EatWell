package com.marcocaloiaro.eatwell.customviews

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.marcocaloiaro.eatwell.R
import kotlinx.android.synthetic.main.custom_alert_dialog.*

class EatWellDialog(context: Context, private val type: DialogType, private val eatWellDialogListener: EatWellDialogListener) : AlertDialog(context) {

    enum class DialogType {
        LOCATION_PERMISSION_REQUIRED_DIALOG, CALL_PERMISSION_REQUIRED_DIALOG,
        LOCATION_DISABLED_DIALOG, CONNECTION_DISABLED_DIALOG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_alert_dialog)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setDialogProperties()

        setDialogContent()

        setUpListener()

    }

    private fun setDialogProperties() {
        when (type) {
            DialogType.LOCATION_DISABLED_DIALOG -> setCancelable(false)
            DialogType.CALL_PERMISSION_REQUIRED_DIALOG -> setCancelable(false)
            DialogType.LOCATION_PERMISSION_REQUIRED_DIALOG -> setCancelable(false)
            DialogType.CONNECTION_DISABLED_DIALOG -> setCancelable(false)
        }
    }

    private fun setUpListener() {
        dialogActionButton.setOnClickListener {
            when (type) {
                DialogType.LOCATION_PERMISSION_REQUIRED_DIALOG -> {
                    this.dismiss()
                    eatWellDialogListener.onDialogButtonClicked(DialogType.LOCATION_PERMISSION_REQUIRED_DIALOG)
                }
                DialogType.LOCATION_DISABLED_DIALOG -> {
                    this.dismiss()
                    eatWellDialogListener.onDialogButtonClicked(DialogType.LOCATION_DISABLED_DIALOG)
                }
                DialogType.CONNECTION_DISABLED_DIALOG -> {
                    this.dismiss()
                    eatWellDialogListener.onDialogButtonClicked(DialogType.CONNECTION_DISABLED_DIALOG)
                }
                DialogType.CALL_PERMISSION_REQUIRED_DIALOG -> {
                    this.dismiss()
                    eatWellDialogListener.onDialogButtonClicked(DialogType.CALL_PERMISSION_REQUIRED_DIALOG)
                }
            }
        }

    }

    private fun setDialogContent() {
        when(type) {
            DialogType.LOCATION_PERMISSION_REQUIRED_DIALOG -> bindDataToDialog(context.resources.getString(R.string.dialog_location_permission_required_title),
                context.resources.getString(R.string.dialog_location_permission_required_message),
                context.resources.getString(R.string.dialog_positive_button))
            DialogType.LOCATION_DISABLED_DIALOG -> bindDataToDialog(context.resources.getString(R.string.dialog_location_disabled_title),
                context.resources.getString(R.string.dialog_location_disabled_message),
                context.resources.getString(R.string.dialog_positive_button))
            DialogType.CONNECTION_DISABLED_DIALOG -> bindDataToDialog(context.resources.getString(R.string.dialog_connection_disabled_title),
                context.resources.getString(R.string.dialog_connection_disabled_message),
                context.resources.getString(R.string.dialog_positive_button))
            DialogType.CALL_PERMISSION_REQUIRED_DIALOG -> bindDataToDialog(context.resources.getString(R.string.dialog_call_permission_required_title),
                context.resources.getString(R.string.dialog_call_permission_required_message),
                context.resources.getString(R.string.dialog_positive_button))
        }
    }

    private fun bindDataToDialog(title: String, message: String, buttonText: String) {
        dialogTitle.text = title
        dialogMessage.text = message
        dialogActionButton.text = buttonText
    }
}