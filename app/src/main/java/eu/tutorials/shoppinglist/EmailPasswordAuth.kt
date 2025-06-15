package eu.tutorials.shoppinglist

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class EmailPassword(private val baseContext: Context)  {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Account Created. Please Sign In!!",
                        Toast.LENGTH_LONG,
                    ).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(baseContext, "Successfully Signed In", Toast.LENGTH_SHORT,).show()
                    onSuccess()
                    val user = auth.currentUser
                    updateUI(user)
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT,).show()
                    onFailure()
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    fun signOut() {
        Toast.makeText(baseContext, "Successfully Signed Out!!", Toast.LENGTH_SHORT,).show()
        // [START auth_sign_out]
        auth.signOut()
        // [END auth_sign_out]
    }

    private fun updateUI(user: FirebaseUser?) {
    }

    fun deleteUser() {
        // [START delete_user]
        val user = auth.currentUser!!

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User account deleted.")
                    Toast.makeText(baseContext, "Account Deleted Successfully", Toast.LENGTH_LONG).show()
                }
            }
        // [END delete_user]
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}