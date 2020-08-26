package com.marcocaloiaro.eatwell.restaurantscreen.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.marcocaloiaro.eatwell.R
import com.marcocaloiaro.eatwell.base.BaseFragment
import com.marcocaloiaro.eatwell.customviews.EatWellDialog
import com.marcocaloiaro.eatwell.customviews.EatWellDialogListener
import com.marcocaloiaro.eatwell.data.model.Status
import com.marcocaloiaro.eatwell.restaurantscreen.utils.RestaurantUtils
import com.marcocaloiaro.eatwell.restaurantscreen.viewmodel.RestaurantViewModel
import com.marcocaloiaro.eatwell.restaurantscreen.model.restaurantdetail.Venue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_restaurant_detail.*


@AndroidEntryPoint
class RestaurantDetailFragment : BaseFragment(),
    EatWellDialogListener {

    companion object {
        private const val CALL_PHONE_PERMISSION_CODE = 4
    }

    private var callPermissionGranted: Boolean = false

    private val restaurantViewModel: RestaurantViewModel by viewModels {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()

        val restaurantId = arguments?.getString(getString(R.string.restaurant_id_key))

        restaurantId?.let {
            restaurantViewModel.fetchRestaurantDetails(it, clientId, clientSecret)
        }

        showRestaurantDetails()

    }

    override fun onDestroyView() {
        alertDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        (activity as AppCompatActivity).supportActionBar?.show()
        super.onDestroyView()
    }

    private fun setUpListeners() {
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun showRestaurantDetails() {
        restaurantViewModel.restaurantDetails.observe(viewLifecycleOwner, Observer { resource ->
            when(resource.status) {
                Status.SUCCESS -> {
                    resource.data?.let {
                        bindData(it)
                    }
                    progressBar.visibility = View.INVISIBLE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    progressBar.visibility = View.INVISIBLE
                    Snackbar.make(requireView(), getString(R.string.restaurant_details_fetch_error), Snackbar.LENGTH_SHORT).show()
                }
            }

        })
    }

    private fun bindData(restaurant: Venue) {
        restaurantName.text = restaurant.name
        restaurantCategory.text = restaurant.categories?.get(0)?.name ?: getString(R.string.category_fetch_error)
        ratingBar.rating = restaurant.rating?.toFloat() ?: (0.0).toFloat()
        restaurantRating.text = restaurant.rating?.toString() ?: getString(R.string.rating_fetch_error)
        restaurantPriceTier.text = RestaurantUtils.buildRestaurantPriceTier(
            restaurant.price?.tier,
            restaurant.price?.currency
        ) ?: getString(R.string.tier_fetch_error)
        restaurant.url?.let {
            restaurantWebsite.setTextColor(Color.BLUE)
            handleWebsiteClick(it)
        }

        restaurantStatus.text = restaurant.hours?.status ?: getString(R.string.hours_fetch_error)
        when(restaurant.hours?.isOpen) {
            true -> restaurantStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGreen))
            else -> restaurantStatus.setTextColor(Color.RED)
        }

        phoneIcon.setOnClickListener {
            handlePhoneCall(restaurant.contact?.formattedPhone)
        }
        RestaurantUtils.buildImageUri(restaurant.photos)?.let {
            Glide.with(this).load(it).into(restaurantImage)
            return
        }
        Glide.with(this).load(R.drawable.ic_no_result).into(restaurantImage)

    }

    private fun handlePhoneCall(formattedPhone: String?) {
        checkCallPermissions()
        if (callPermissionGranted) {
            val intent =
                Intent(Intent.ACTION_CALL, Uri.parse("tel:$formattedPhone"))
            startActivity(intent)
            return
        }
    }

    private fun checkCallPermissions() {
        activity?.let {
            when {
                permissionsGranted() -> {
                    callPermissionGranted = true
                }
                else -> requestPermissions(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    CALL_PHONE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        callPermissionGranted = false
        when (requestCode) {
            CALL_PHONE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    callPermissionGranted = true
                    checkCallPermissions()
                } else {
                    handlePermissionNotGranted()
                }
            }
        }
    }

    private fun handlePermissionNotGranted() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
            showPermissionRequiredDialog()
            return
        }
        Snackbar.make(requireView(), getString(R.string.call_permission_not_granted_error), Snackbar.LENGTH_SHORT).show()
    }

    private fun showPermissionRequiredDialog() {
        alertDialog = EatWellDialog(requireContext(), EatWellDialog.DialogType.CALL_PERMISSION_REQUIRED_DIALOG, this)
        (alertDialog as EatWellDialog).show()
    }

    private fun permissionsGranted(): Boolean {
        activity?.let {
            return (ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
        }
        return false
    }

    private fun handleWebsiteClick(website: String) {
        restaurantWebsite.setOnClickListener {
            val uri = Uri.parse(website)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onDialogButtonClicked(type: EatWellDialog.DialogType) {
        if (type == EatWellDialog.DialogType.CALL_PERMISSION_REQUIRED_DIALOG) {
            requestPermissions(
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PHONE_PERMISSION_CODE
            )
        }
    }
}