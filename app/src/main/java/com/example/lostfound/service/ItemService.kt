package com.example.lostfound.service

import android.content.Context
import com.example.lostfound.api.RetrofitClient
import com.example.lostfound.model.Item
import com.example.lostfound.util.ItemSort
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ItemService(context: Context) {

    private val api = RetrofitClient.api
    private val localStorage = LocalStorageService(context.applicationContext)
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    interface ItemCallback<T> {
        fun onSuccess(data: T)
        fun onError(message: String)
    }

    fun getCachedItems(): List<Item> =
        ItemSort.newestFirst(localStorage.getCachedItems())

    fun getCachedItemsAsync(callback: ItemCallback<List<Item>>) {
        executor.execute {
            try {
                callback.onSuccess(getCachedItems())
            } catch (e: Exception) {
                callback.onError(e.message ?: "Failed to read cache")
            }
        }
    }

    fun getAllItems(callback: ItemCallback<List<Item>>) {
        api.getItems().enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if (response.isSuccessful) {
                    val items = ItemSort.newestFirst(response.body() ?: emptyList())
                    executor.execute { localStorage.cacheItems(items) }
                    callback.onSuccess(items)
                } else {
                    val cached = ItemSort.newestFirst(
                        executor.submit<List<Item>> { localStorage.getCachedItems() }.get()
                    )
                    if (cached.isNotEmpty()) {
                        callback.onSuccess(cached)
                    } else {
                        callback.onError("Failed to load items (${response.code()})")
                    }
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                val cached = ItemSort.newestFirst(
                    executor.submit<List<Item>> { localStorage.getCachedItems() }.get()
                )
                if (cached.isNotEmpty()) {
                    callback.onSuccess(cached)
                } else {
                    callback.onError(t.message ?: "Network error")
                }
            }
        })
    }

    fun getItemById(id: String, callback: ItemCallback<Item>) {
        api.getItem(id).enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (response.isSuccessful && response.body() != null) {
                    val item = response.body()!!
                    executor.execute { localStorage.addRecentlyViewed(item) }
                    callback.onSuccess(item)
                } else {
                    callback.onError("Failed to load item (${response.code()})")
                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                callback.onError(t.message ?: "Network error")
            }
        })
    }

    fun createItem(item: Item, callback: ItemCallback<Item>) {
        api.createItem(item).enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError("Failed to create item (${response.code()})")
                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                callback.onError(t.message ?: "Network error")
            }
        })
    }

    fun updateItem(id: String, item: Item, callback: ItemCallback<Item>) {
        api.updateItem(id, item).enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError("Failed to update item (${response.code()})")
                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                callback.onError(t.message ?: "Network error")
            }
        })
    }

    fun deleteItem(id: String, callback: ItemCallback<Item>) {
        api.deleteItem(id).enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: Item(id = id))
                } else {
                    callback.onError("Failed to delete item (${response.code()})")
                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                callback.onError(t.message ?: "Network error")
            }
        })
    }
}
