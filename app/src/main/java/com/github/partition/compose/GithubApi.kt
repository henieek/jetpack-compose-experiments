package com.github.partition.compose

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
  @GET("search/repositories")
  suspend fun search(@Query("q") phrase: String): SearchResponse
}

data class ItemResponse(
  @SerializedName("full_name") val fullName: String
)

data class SearchResponse(
  @SerializedName("items") val items: List<ItemResponse>
)