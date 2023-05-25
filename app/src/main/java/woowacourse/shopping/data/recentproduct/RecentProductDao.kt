package woowacourse.shopping.data.recentproduct

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.domain.Product
import com.example.domain.RecentProduct
import java.time.LocalDateTime
import java.time.ZoneOffset

class RecentProductDao(context: Context) {
    private val recentDb: SQLiteDatabase = RecentProductDbHelper(context).writableDatabase

    private fun getCursor(selection: String? = ""): Cursor {
        return recentDb.query(
            RecentProductContract.TABLE_NAME,
            arrayOf(
                RecentProductContract.TABLE_COLUMN_PRODUCT_ID,
                RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL,
                RecentProductContract.TABLE_COLUMN_PRODUCT_NAME,
                RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE,
                RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME
            ),
            selection, arrayOf(), null, null, ""
        )
    }

    fun getMostRecentProduct(): RecentProduct? {
        val all = getAll()
        return if (all.isEmpty()) null else all[0]
    }

    fun getRecentProduct(productId: Int): RecentProduct? {
        val cursor = getCursor("${RecentProductContract.TABLE_COLUMN_PRODUCT_ID} = $productId")
        var recentProduct: RecentProduct? = null

        with(cursor) {
            while (moveToNext()) {
                val productId =
                    getInt(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_ID))
                val productImageUrl =
                    getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL))
                val productName =
                    getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_NAME))
                val productPrice =
                    getInt(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE))
                val viewedDateTime =
                    getLong(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME))
                recentProduct = RecentProduct(
                    productId = productId, productImageUrl = productImageUrl,
                    productName = productName, productPrice = productPrice,
                    viewedDateTime = LocalDateTime.ofEpochSecond(
                        viewedDateTime,
                        0,
                        ZoneOffset.UTC
                    )
                )
            }
        }
        cursor.close()
        return recentProduct
    }

    fun getAll(): List<RecentProduct> {
        val cursor: Cursor = getCursor()
        var recentProducts: MutableList<RecentProduct> = mutableListOf()

        with(cursor) {
            while (moveToNext()) {
                val productId =
                    getInt(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_ID))
                val productImageUrl =
                    getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL))
                val productName =
                    getString(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_NAME))
                val productPrice =
                    getInt(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE))
                val viewedDateTime =
                    getLong(getColumnIndexOrThrow(RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME))

                recentProducts.add(
                    RecentProduct(
                        productId = productId,
                        productImageUrl = productImageUrl,
                        productName = productName,
                        productPrice = productPrice,
                        viewedDateTime = LocalDateTime.ofEpochSecond(
                            viewedDateTime,
                            0,
                            ZoneOffset.UTC
                        )
                    )
                )
            }
        }

        cursor.close()
        recentProducts = recentProducts.sortedBy { it.viewedDateTime }.reversed().toMutableList()
        return if (recentProducts.size >= 10) recentProducts.subList(
            0,
            SHOW_COUNT
        ) else recentProducts
    }

    fun addColumn(product: Product, viewedDateTime: LocalDateTime) {
        val deleteQuery =
            """
                DELETE FROM ${RecentProductContract.TABLE_NAME}
                WHERE ${RecentProductContract.TABLE_COLUMN_PRODUCT_ID} = ${product.id};
            """.trimIndent()
        recentDb.execSQL(deleteQuery)

        val values = ContentValues().apply {
            put(RecentProductContract.TABLE_COLUMN_PRODUCT_ID, product.id)
            put(RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL, product.imageUrl)
            put(RecentProductContract.TABLE_COLUMN_PRODUCT_NAME, product.name)
            put(RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE, product.price)
            put(
                RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME,
                viewedDateTime.toEpochSecond(ZoneOffset.UTC)
            )
        }
        recentDb.insert(RecentProductContract.TABLE_NAME, null, values)
    }

    fun deleteColumn(productId: Int) {
        recentDb.delete(
            RecentProductContract.TABLE_NAME,
            RecentProductContract.TABLE_COLUMN_PRODUCT_ID + "=" + productId, null
        )
    }

    fun createTable() {
        recentDb.execSQL(
            """
                CREATE TABLE ${RecentProductContract.TABLE_NAME} (
                    ${RecentProductContract.TABLE_COLUMN_PRODUCT_ID} INTEGER,
                    ${RecentProductContract.TABLE_COLUMN_PRODUCT_IMAGE_URL} TEXT,
                    ${RecentProductContract.TABLE_COLUMN_PRODUCT_NAME} TEXT,
                    ${RecentProductContract.TABLE_COLUMN_PRODUCT_PRICE} INTEGER,
                    ${RecentProductContract.TABLE_COLUMN_VIEWED_DATE_TIME} LONG
                )
            """.trimIndent()
        )
    }

    fun deleteTable() {
        recentDb.execSQL(
            """
                DROP TABLE IF EXISTS ${RecentProductContract.TABLE_NAME};
            """.trimIndent()
        )
    }

    companion object {
        private const val SHOW_COUNT = 10
    }
}