package com.diegoparra.veggie

/*
 *  Scoping viewModels to navGraphs:
 * Be very careful when scoping fragments to nav_graph_main, especially those dependent on userId,
 * or data that could change but for simplicity is being called once (e.g. addressListFragment).
 * This is because when scoped to main nav graph, viewModel will not be destroyed and created on
 * as long as the navGraph is alive, almost during all the app lifecycle, and if for example user
 * signOut and signIn with a different account, that change won't be listened, and next time navigating
 * to address it will display the ones for the previous user.
 */