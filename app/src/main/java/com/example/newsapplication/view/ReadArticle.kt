package com.example.newsapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.newsapplication.R
import kotlinx.android.synthetic.main.fragment_read_article.*

class ReadArticle : Fragment(R.layout.fragment_read_article) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        ivBack.setOnClickListener {
//            requireActivity().fragmentManager!!.popBackStack();
//        }

        /*
        val author: String,
        val content: String,
        val description: String,
        val publishedAt: String,
        val source: Source,
        val title: String,
        val url: String,
        val urlToImage: String
         */

        val author = arguments?.getString("author")
        val content = arguments?.getString("content")
        val description = arguments?.getString("description")
        val publishedAt = arguments?.getString("publishedAt")
        val title = arguments?.getString("title")
        val url = arguments?.getString("url")
        val urlToImage = arguments?.getString("urlToImage")

//        Glide.with(ivArticleImage).load(urlToImage).into(ivArticleImage)
//        tvTitle.text = title
//        tvDescription.text = description
//        tvContent.text = content
//        webView.settings.javaScriptEnabled;
//        url?.let { webView.loadUrl(it) };

    }

}