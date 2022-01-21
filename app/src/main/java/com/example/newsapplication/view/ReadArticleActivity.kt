package com.example.newsapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newsapplication.R
import kotlinx.android.synthetic.main.activity_read_article.*
//import kotlinx.android.synthetic.main.fragment_read_article.*
//import kotlinx.android.synthetic.main.fragment_read_article.webView

class ReadArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_article)

        val bundle = intent.getBundleExtra("bundle")
        val url = bundle!!.getString("url")

        webView.clearCache(true)
        webView.settings.javaScriptEnabled;
        url?.let { webView.loadUrl(it) };

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}