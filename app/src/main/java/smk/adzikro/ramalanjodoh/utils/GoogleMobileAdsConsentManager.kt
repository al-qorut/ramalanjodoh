package smk.adzikro.ramalanjodoh.utils

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import smk.adzikro.ramalanjodoh.ui.activities.MainActivity

class GoogleMobileAdsConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    fun interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED


    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener,
    ) {
        // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
        val debugSettings =
            ConsentDebugSettings.Builder(activity)
                // .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("ABCDEF012345")//MainActivity.TEST_DEVICE_HASHED_ID
                .build()

        val params = ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()

        // [START request_consent_info_update]
        // Requesting an update to consent information should be called on every app launch.
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // Called when consent information is successfully updated.
                // [START_EXCLUDE silent]
                loadAndShowConsentFormIfRequired(activity, onConsentGatheringCompleteListener)
                // [END_EXCLUDE]
            },
            { requestConsentError ->
                // Called when there's an error updating consent information.
                // [START_EXCLUDE silent]
                onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
                // [END_EXCLUDE]
            },
        )
        // [END request_consent_info_update]
    }

    private fun loadAndShowConsentFormIfRequired(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener,
    ) {
        // [START load_and_show_consent_form]
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
            // Consent gathering process is complete.
            // [START_EXCLUDE silent]
            onConsentGatheringCompleteListener.consentGatheringComplete(formError)
            // [END_EXCLUDE]
        }
        // [END load_and_show_consent_form]
    }

    /** Helper method to call the UMP SDK method to show the privacy options form. */
    fun showPrivacyOptionsForm(
        activity: MainActivity,
        onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener,
    ) {
        // [START present_privacy_options_form]
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
        // [END present_privacy_options_form]
    }

    companion object {
        @Volatile private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
                }
    }
}