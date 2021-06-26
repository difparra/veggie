package com.diegoparra.veggie

/*
 *  Scoping viewModels to navGraphs:
 *      Be very careful when scoping fragments to nav_graph_main, especially those dependent on userId,
 *      or data that could change but for simplicity is being called once (e.g. addressListFragment).
 *      This is because when scoped to main nav graph, viewModel will not be destroyed and created on
 *      as long as the navGraph is alive, almost during all the app lifecycle, and if for example user
 *      signOut and signIn with a different account, that change won't be listened, and next time navigating
 *      to address it will display the ones for the previous user.
 *
 */

/*
    NOTES ABOUT COROUTINES:
        CoroutineScope {}:  Used in suspend functions to get the coroutine scope,
        so that launch can be called inside suspend function

        launch {}:  Start a new coroutine, which mean that start a new job that will be
        executed concurrently (in parallel).
        launch can be called inside another coroutine, and job will be executed in parallel.
        This could be useful for getting info from individual products, they can be loaded
        concurrently.       *** Used in getCartProductsUseCase and getMainProductsUseCase
        The parent coroutine will only complete until all its children are completed. In other words
        calling a suspend function will only complete and continue with the next line, until
        all the code inside the suspend function and internal suspend functions is complete.

        If new coroutines are launched inside another coroutine, but, for example, the second
        needs to wait until the first complete, they can be launched with async {} and then
        using .await(), that will return the value of the completed coroutine job.


        SUMMARY:

        launch{}: To call coroutines, jobs that work concurrently.
        launch{} inside launch{} (another coroutine):
            Will only complete and continue the main flow until all its children are completed.
            Execute children jobs concurrently.
            If needed some result in another job, async{} could be used instead, along with .await()
 */

/*
    NOTES ABOUT REMOTE CONFIG:

        fetchTimeMillis:
            ->  Values won't be fetched if minimumFetchInterval has not been reached.
                After calling fetchAndActivate, lastFetchTime didn't changed if minimumFetchInterval was not reached.
            ->  LastFetchTime will survive even when app closes, there is no need to save in shared/datastore prefs.
        If key doesn't exist in remoteConfigServer:
            ->  Will get the default value in xml file, or another default value as 0 if getting double.
            ->  It is not likely it will throw an exception.
        No internet:
            ->  Will be throw exception, such as FirebaseRemoteClientException
 */

/*
    NOTE:   https://www.youtube.com/watch?v=B8ppnjGPAGE
            liveDataCoroutineBuilder can assign Dispatchers to LiveData,
            so I can switchMap on another dispatcher and use LiveData instead of StateFlow
            liveData(Dispatchers.IO) { emit(__) }
            The problem with liveData is that map run on MainThread, so it is better to use switchMap
            and return a liveData created with a builder.
            Flow map is in the coroutine context so function can be normally called and then call asLiveData.
 */

/*
    adjustViewBounds:   XML attribute
        Important in imageViews to scale image
 */


/*
    Be careful when calling combine(List<flows>) because if the list is empty,
    combine will return a flow that does nothing, that will never emit,
    will not even emit an empty list, and if it is like app crashes or freezes.
 */

/*
    Important: In the dialogs with shared ui, do not use findNavController referring to a view, as
    the views will not have a specific NavController associated. Use instead the findNavController
    from the fragment, so pay attention carefully to the imports.
 */