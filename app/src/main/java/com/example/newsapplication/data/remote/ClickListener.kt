package com.example.newsapplication.data.remote

interface ClickListener {

    fun onClick(article: Article, position: Int)

    fun shareClick(article: Article, position: Int)

}