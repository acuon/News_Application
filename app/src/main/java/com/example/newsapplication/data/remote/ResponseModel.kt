package com.example.newsapplication.data.remote

data class ResponseModel(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)