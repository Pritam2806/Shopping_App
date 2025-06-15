package eu.tutorials.shoppinglist

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Navigation (
    navController : NavController
)  {
    // *********************************************************************************************
    val context = LocalContext.current
    val emailAuth = EmailPassword(context)
    // *********************************************************************************************

    NavHost(
        navController = navController as NavHostController,      // casting it to the NavHostController
        startDestination = "SignInScreen",    // ****Iske andar ek route hota hai****
    )  {
        composable ("SignInScreen")  {
            SignInScreen(
                onSignIn =  { email, password ->
                    emailAuth.signIn(
                        email,
                        password,
                        { navController.navigate("ShoppingScreen") },
                        { Toast.makeText(context, "Sign In Failed", Toast.LENGTH_SHORT).show() }
                    )
                },
                onSignUp = { email, password -> emailAuth.createAccount(email, password) }
            )
        }

        composable ("ShoppingScreen")  {
            shoppingList(
                onButtonClicked = { navController.navigate("SignInScreen") },
                emailAuth = EmailPassword(LocalContext.current)
            )
        }
    }
}