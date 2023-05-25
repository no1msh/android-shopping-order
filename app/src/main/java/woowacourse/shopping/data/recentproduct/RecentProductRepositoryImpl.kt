package woowacourse.shopping.data.recentproduct

import com.example.domain.RecentProduct
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.data.product.ProductRemoteService
import java.time.LocalDateTime

class RecentProductRepositoryImpl(
    private val url: String,
    private val productProductRemoteService: ProductRemoteService,
    private val recentProductDao: RecentProductDao
) : RecentProductRepository {

    override fun getAll(): List<RecentProduct> {
        return recentProductDao.getAll()
    }

    override fun getMostRecentProduct(): RecentProduct? {
        return recentProductDao.getMostRecentProduct()
    }

    override fun getRecentProduct(productId: Int): RecentProduct? {
        return recentProductDao.getRecentProduct(productId)
    }

    override fun addRecentProduct(productId: Int, viewedDateTime: LocalDateTime) {
        productProductRemoteService.requestProduct(
            url = url,
            id = productId,
            onSuccess = { if (it != null) recentProductDao.addColumn(it, viewedDateTime) },
            onFailure = {}
        )
    }
}