package com.diegoparra.veggie.user.ui.utils

/*
                Options:

                FIRST:
                Use findNavController().previousBackStackEntry?.savedStateHandle
                From loginFlow, setLoginSuccessful on savedStateHandle from prevBackStackEntry.
                And in prevBackStackEntry, observe changes.
                **  The problem with this approach is that I am not sure I will be able to get
                the prevBackStackEntry, as navController are different on different navGraphs.

                SECOND:
                SharedActivityViewModel

                FIRST AND SECOND REQUIRES:
                LOGIN_SUCCESSFUL_ARGUMENT in UserFragment.
                    When start navigation to loginFlow set to false.
                    If loginFlow completes successfully set true.
                With this argument, fragment can decide if stay or .navigateUp = popFragmentFromBackStack

                THIRD:
                Deeplink -> But is not correct, as backStack will become a mess.
                I must navigate cleaning the backStack.

                ------------------------------------------------------------------------------------

                findNavController is correct, as it is the same, so I can get the backstack and
                saved state handle and everything is ok.

                Navigation and options:
                    UserFragment:   Should observe its own savedStateHandle and verify if login
                    was successful

                    SignInOptions:  Should set login successful or not on the previous savedStateHandle

                NestedNavigation:
                    SignInOptions -> Email (contains both: signIn and signUp in viewPager but same destination)
                                  -> Google*
                                  -> Facebook*
                                * Not fragments, they are activities instead.

                Options:
                    1.  From email fragment get prevStateHandle (SignInOptions) and set successful on it.
                        SignInOptions will observe and then set on user.
                        User will observe and take pertinent action.

                        Conditions:
                        - Could not navigate directly to user. Need to navigate up and go first to
                        signInOptions, so that its savedStateHandle will activate and call the other
                        one in user/cart.

                        Option 2:
                        Set an action to navigate from email to signInOptions, with arguments
                        (login_successful). As the value gets through the state handle that live data
                        will be immediately activate and will call loginSuccessful on userFragment.
                        * It could be more safe, as I would not forget to set loginSuccessful, as it
                        is necessary in the directions navigation.
                        * Will also not need to observe currentSavedStateHandle. Value will be received
                        on savedStateHandle and immediately dispatched to the prevSavedStateHandle.
                        * But, will require to navigate to signInOptions popping up destinations.
                        * Will be a little less efficient, as I think it is not using the fragment
                        on the back stack, but creating a new one and as pop up was set.


                    2.  HiltNavGraphViewModel

                        In the viewModel save the previousDestination or the savedStateHandle.
                        val b = findNavController().previousBackStackEntry?.destination?.id

                        Then it savedStateHandle of user/cart could be accessed from anywhere.
                        val prevDestination = findNavController().getBackStackEntry(id_destination)
                        prevDestination.savedStateHandle.set("login_successful", true)

                        ** Note: Should Check if destination is get from parent graph
                        val dest = findNavController().graph.parent?.get(R.id.nav_user)


                        Now, if using the navGraphViewModel, I could get the previous destination from
                        any fragment and set the login_successful variable in user/cart.

                        Could be the best approach, but could still have some cons:
                        shared viewModel implies:

                        Should contains methods:
                            signInEmail
                            signUpEmail
                            signInGoogle
                            signInFacebook
                            **  No problem with the above.

                            But, for validation
                            email should be different for signIn and signUp.


                            with navigation:
                            send original destination.



                    Final option:
                        Set argument on global action.

                        LOGIN SUCCESSFUL

                        user -> observe in stateHandle
                            false -> navigate up, pop destination findNavController().popBackStack()
                            else -> do nothing
                                null -> coming from parent navGraph
                                true -> login was successful and should show data

                        signInOptions -> set
                            false -> when starting
                            true -> when successful



             */


/*
val navController = findNavController()
val currentBackStackEntry = findNavController().currentBackStackEntry
val currentSavedStateHandle = currentBackStackEntry?.savedStateHandle
val previousBackStackEntry = findNavController().previousBackStackEntry
val prevSavedStateHandle = previousBackStackEntry?.savedStateHandle


/*//  In start fragment
findNavController().currentBackStackEntry?.savedStateHandle?.set(
"original_destination",
findNavController().previousBackStackEntry?.destination
)


findNavController().getBackStackEntry(R.id.nav_sign_in)


//  When navigating
@IdRes val originalDestination = findNavController().getBackStackEntry(R.id.nav_sign_in).savedStateHandle.get<Int>("original_destination")
val savedStateHandle = originalDestination?.let { findNavController().getBackStackEntry(originalDestination).savedStateHandle }
savedStateHandle?.set("login_successful", true)
*/
 */