package woowacourse.shopping.data.cart

import com.example.domain.CartProduct
import com.example.domain.repository.CartRepository

class CartRepositoryImpl(
    private val url: String,
    private val service: CartRemoteService
) : CartRepository {

    override fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit
    ) {
        Thread {
            service.requestAllCartProducts(
                url = url,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }.start()
    }

    override fun addCartProduct(
        productId: Int,
        onSuccess: (cartId: Int) -> Unit,
        onFailure: () -> Unit
    ) {
        Thread {
            service.requestAddCartProduct(
                url = url,
                productId = productId,
                onSuccess = onSuccess,
                onFailure = onFailure,
            )
        }.start()
    }

    override fun updateCartProductQuantity(
        id: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        Thread {
            service.requestUpdateCartProductQuantity(
                url = url,
                id = id,
                quantity = quantity,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }.start()
    }

    override fun deleteCartProduct(
        id: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        Thread {
            service.requestDeleteCartProduct(
                url = url,
                id = id,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }.start()
    }
}