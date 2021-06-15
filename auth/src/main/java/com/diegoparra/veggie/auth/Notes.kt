package com.diegoparra.veggie.auth

/*
    TO IMPLEMENT THIS MODULE, IT IS NECESSARY TO:



    Kotlin:
        Complete AuthModule using hilt.
        - An implementation of authCallbacks must be created and provided according to the needing.
            With these callbacks, user can save info in the database they want.
        - Provide the googleSignInClient, getting IdToken from strings where google auth is configured.

    Google and facebook auth:
        Complete configuration guides for google:
            https://firebase.google.com/docs/auth/android/google-signin
            https://developers.google.com/identity/sign-in/android/sign-in

            Save SHA-1 and SHA-256 keys for the app in firebase.
                Using gradle tasks.

            Add dependencies:
                implementation platform('com.google.firebase:firebase-bom:28.1.0')
                implementation 'com.google.firebase:firebase-auth'
                implementation 'com.google.android.gms:play-services-auth:19.0.0'
            Create googleSignInClient:
                @Provides
                fun providesGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
                    val gso = GoogleSignInOptions
                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                    return GoogleSignIn.getClient(context, gso)
                }

        Complete configuration guides for facebook:
            https://firebase.google.com/docs/auth/android/facebook-login
            https://developers.facebook.com/

            Some strings will be needed to add, such as:
                <string name="facebook_app_id">315214526875872</string>
                <string name="fb_login_protocol_scheme">fb315214526875872</string>
            That are used in the manifest.xml file, but all of those instructions will be found in
            developers.facebook site.


    //  --------------------------------------------------------------------------------------------

    Ui:
        Theme is based on Base.Theme.Veggie found in coreModule.
        Some of the attributes that can be changed are:
            paddingSmall, Standard & Big
            appBarHeight & Elevation
            alphaSecondaryText
            type/font
            colors

    //  --------------------------------------------------------------------------------------------

    TODO:
        Inject appIcon and appTitle with hilt, and use them to set the theme in signInOptionsFragment.
        AppIcon
            requireContext().packageManager.getApplicationIcon(requireContext().packageName)
            requireContext().applicationInfo.icon
        
 */