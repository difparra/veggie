package com.diegoparra.veggie.auth

/*
    TO IMPLEMENT THIS MODULE, IT IS NECESSARY TO:

    Kotlin:
        Implement and provide authCallbacks with hilt.
        With these callbacks, user can save info in the database they want.

    Ui:
        Theme must contain following attributes:
            paddingSmall
            paddingStandard
            paddingBig
            appBarHeight
            appBarElevation
            alphaSecondaryText
        And some styles also implemented in the theme

    TODO:
        Inject appIcon and appTitle with hilt, and in signInOptionsFragment, replace icon with
        the one provided with hilt from external module.
        Check R.string.default_web_client_id in authModule, as well as FacebookStrings
        <!-- Facebook -->
            <string name="facebook_app_id">315214526875872</string>
            <string name="fb_login_protocol_scheme">fb315214526875872</string>
        Because is configuration that I guess should be added in the external module.
        
 */