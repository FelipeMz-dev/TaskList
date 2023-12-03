package com.felipemz_dev.tasklist.core

import android.app.Activity
import com.google.ads.consent.ConsentForm
import com.google.ads.consent.ConsentFormListener
import com.google.ads.consent.ConsentInfoUpdateListener
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import java.net.URL

class ConsentManager(private val activity: Activity) {

    private lateinit var consentForm: ConsentForm

    fun checkConsentAndLoadAds(onConsent: () -> Unit) {
        val consentInformation = ConsentInformation.getInstance(activity)
        val publisherIds = arrayOf("pub-7090484402312159")
        consentInformation.requestConsentInfoUpdate(publisherIds, object :
            ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                if (ConsentInformation.getInstance(activity).isRequestLocationInEeaOrUnknown
                    && consentStatus == ConsentStatus.UNKNOWN
                ) {
                    showConsentForm{ onConsent() }
                } else {
                    onConsent()
                }
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {
                // Manejar errores al actualizar la información de consentimiento
            }
        })
    }

    private fun showConsentForm(onConsent: () -> Unit) {
        val privacyUrl = "https://www.app-privacy-policy.com/live.php?token=augxUg7WGY7sHeN0rZJzWAnL7Oivw7aI"
        consentForm = ConsentForm.Builder(activity, URL(privacyUrl))
            .withListener(object : ConsentFormListener() {
                override fun onConsentFormLoaded() {
                    consentForm.show()
                }

                override fun onConsentFormError(errorDescription: String) {
                    // Manejar errores al cargar el formulario de consentimiento
                }

                override fun onConsentFormOpened() {
                    // El formulario de consentimiento se ha abierto
                }

                override fun onConsentFormClosed(
                    consentStatus: ConsentStatus?,
                    userPrefersAdFree: Boolean?
                ) {
                    onConsent()
                }
            })
            .build()

        consentForm.load()
    }
}