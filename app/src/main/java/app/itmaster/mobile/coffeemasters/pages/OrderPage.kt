package app.itmaster.mobile.coffeemasters.pages

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.itmaster.mobile.coffeemasters.data.DataManager
import app.itmaster.mobile.coffeemasters.data.ItemInCart
import app.itmaster.mobile.coffeemasters.ui.theme.Alternative1
import app.itmaster.mobile.coffeemasters.ui.theme.Alternative2
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "settings")
@Composable
fun OrderPage(dataManager: DataManager, showSnackbar: (String) -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ItemsView(dataManager)
        Form(dataManager, showSnackbar,LocalContext.current )
    }
}

@Composable
fun ItemsView(dataManager: DataManager) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
    ) {
        Column (modifier = Modifier
            .background(Alternative2)) {
            Title("ITEMS")
            LazyColumn(modifier = Modifier.height(500.dp)) {
                itemsIndexed(dataManager.cart) { index, item ->
                    Item(item) {
                        dataManager.cartRemove(item.product)
                    }
                    if (index < dataManager.cart.lastIndex) {
                        Divider(
                            color = Alternative1, thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Item(itemInCart: ItemInCart, onRemove: (ItemInCart)->Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Row {
            Text(
                text = "${itemInCart.quantity}x",
                color = Alternative1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(text = itemInCart.product.name)
        }
        Row {
            Text(
                text = "$${(itemInCart.product.price * itemInCart.quantity).toDecimalString()}",
                modifier = Modifier.padding(end = 8.dp)
            )
            Image(
                Icons.Outlined.Delete,
                colorFilter = ColorFilter.tint(Alternative1),
                contentDescription = "Delete icon",
                modifier = Modifier
                    .padding(end = 8.dp, bottom = 8.dp)
                    .clickable { onRemove(itemInCart) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form(dataManager: DataManager, showSnackbar: (String) -> Unit, context: Context) {
    val coroutineScope = rememberCoroutineScope()
    var text by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(1) {
        coroutineScope.launch {
            text = readTextFromDataStore(context)
        }
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Alternative2)
    ) {
        Column {
            Title("NAME")

            OutlinedTextField(
                modifier = Modifier
                    .padding(16.dp, 0.dp, 16.dp, 16.dp)
                    .fillMaxWidth(),
                value = text,
                onValueChange = {
                    text = it
                },
                label = { Text("Name for order") }
            )
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.End),
                colors = ButtonDefaults.buttonColors(Alternative1),
                onClick = {
                    if(dataManager.cart.isNotEmpty() && text != "") {
                        dataManager.cart = emptyList()
                        dataManager.newOrder(text)
                        showSnackbar("Order #${dataManager.order.orderNumber} " +
                                "submitted to ${dataManager.order.name}")
                        coroutineScope.launch {
                            saveTextToDataStore(context, text)
                        }
                    } else {
                        showSnackbar("Items or name are missing")
                    }
                }
            ) {
                Text("Submit", color = Color.White)
            }
        }
    }
}

suspend fun saveTextToDataStore(context: Context, text: String) {
    val dataStoreKey = stringPreferencesKey("text_key")
    context.dataStore.edit { settings ->
        settings[dataStoreKey] = text
    }
}

// Function to read text from DataStore
suspend fun readTextFromDataStore(context: Context): String {
    val dataStoreKey = stringPreferencesKey("text_key")
    val preferences = context.dataStore.data.first()
    return preferences[dataStoreKey] ?: ""
}

@Composable
fun Title(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium,
        color = Alternative1,)
}