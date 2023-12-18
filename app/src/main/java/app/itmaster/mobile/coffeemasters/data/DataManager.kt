package app.itmaster.mobile.coffeemasters.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DataManager: ViewModel() {
    var menu: List<Category> by mutableStateOf(listOf())
    var cart: List<ItemInCart> by mutableStateOf(listOf())
    var order by mutableStateOf(Order("",0))
    init {
        fetchData()
    }

    fun fetchData() {
        // Ejecuta el getMenu en una corutina (algo así como un thread)
        viewModelScope.launch {
            menu = API.menuService.getMenu()
        }
    }

    fun cartAdd(product: Product) {
        // Verificar si el producto ya está en el carrito
        val existingItem = cart.find { it.product.id == product.id }

        // Si el producto ya está en el carrito, aumentar la cantidad
        if (existingItem != null) {
            val updatedCart = cart.map {
                if (it.product.id == product.id) {
                    it.copy(quantity = it.quantity + 1)
                } else {
                    it
                }
            }
            cart = updatedCart
        } else {
            // Si el producto no está en el carrito, agregarlo con cantidad 1
            cart = cart + ItemInCart(product, 1)
        }
    }

    fun cartRemove(product: Product) {
        // Verificar si el producto está en el carrito
        val existingItem = cart.find { it.product.id == product.id }

        // Si el producto está en el carrito y su cantidad es mayor a 1, reducir la cantidad
        if (existingItem != null && existingItem.quantity > 1) {
            val updatedCart = cart.map {
                if (it.product.id == product.id) {
                    it.copy(quantity = it.quantity - 1)
                } else {
                    it
                }
            }
            cart = updatedCart
        } else {
            // Si la cantidad es 1 o el producto no está en el carrito, eliminar el producto del carrito
            cart = cart.filterNot { it.product.id == product.id }
        }
    }

    fun newOrder(name: String){
        this.order.name = name
        this.order.orderNumber ++
    }
}