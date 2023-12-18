package app.itmaster.mobile.coffeemasters

import android.webkit.JavascriptInterface
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JsInterface(private val snackbarHostState: SnackbarHostState) {

    @JavascriptInterface
    fun showSnackbar() {
        CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar("Thank you for sending your feedback!")
        }
    }
}