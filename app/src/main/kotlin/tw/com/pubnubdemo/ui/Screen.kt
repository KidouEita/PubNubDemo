package tw.com.pubnubdemo.ui

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object ChatScreen : Screen("chat_screen")
    object HomeScreen : Screen("home_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}