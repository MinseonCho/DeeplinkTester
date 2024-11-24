interface NavDestination {
    val route: String

    data object Page : NavDestination {
        override val route: String = "page"
    }

    data object History : NavDestination {
        override val route: String = "history"
    }
}
