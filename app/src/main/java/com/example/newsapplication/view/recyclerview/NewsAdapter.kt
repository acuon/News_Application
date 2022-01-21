package com.example.newsapplication.view.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapplication.R
import com.example.newsapplication.data.remote.Article
import com.example.newsapplication.data.remote.ClickListener
import kotlinx.android.synthetic.main.item_article.view.*

class ArticleAdapter(
    private var articleList: ArrayList<Article>,
    private val clickListener: ClickListener
) : RecyclerView.Adapter<ArticleViewHolder>() {

    private val placeHolderImage =
        "https://www.albertadoctors.org/images/ama-master/feature/Stock%20photos/News.jpg"

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ArticleViewHolder {
        val itemView: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_article, viewGroup, false)
        return ArticleViewHolder(itemView, clickListener)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onBindViewHolder(articleViewHolder: ArticleViewHolder, itemIndex: Int) {

        val data = articleList[itemIndex]
        articleViewHolder.setData(data)
//        val article: Article = articleList[itemIndex]
//        setPropertiesForArticleViewHolder(articleViewHolder, article)
//        articleViewHolder.cardView.setOnClickListener {
//            clickListener.onClick(article, itemIndex)
//        }
//        articleViewHolder.shareArticle.setOnClickListener {
//            clickListener.shareClick(article, itemIndex)
//        }

    }

//    private fun setPropertiesForArticleViewHolder(
//        articleViewHolder: ArticleViewHolder,
//        article: Article
//    ) {
//        checkForUrlToImage(article, articleViewHolder)
//        articleViewHolder.title.text = article?.title
////        articleViewHolder.description.text = article?.description
//        var date: String = article!!.publishedAt
//        date = date.substring(0, 10)
//        articleViewHolder.publishDate.text = date
//    }

//    private fun checkForUrlToImage(article: Article, articleViewHolder: ArticleViewHolder) {
//        if (article.urlToImage == null || article.urlToImage.isEmpty()) {
//            Glide.with(articleViewHolder.urlToImage).load(placeHolderImage)
//                .into(articleViewHolder.urlToImage)
//        } else {
//            Glide.with(articleViewHolder.urlToImage).load(article.urlToImage)
//                .into(articleViewHolder.urlToImage)
//        }
//    }

    fun setArticles(articles: ArrayList<Article>) {
        articleList = articles
        notifyDataSetChanged()
    }

//    inner class ArticleViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {
//
//        val cardView: CardView by lazy { view.card_view }
//        val urlToImage: ImageView by lazy { view.article_urlToImage }
//        val title: TextView by lazy { view.article_title }
////        val description: TextView by lazy { view.article_description }
//        val publishDate: TextView by lazy { view.tvPublishDate }
//        val shareArticle: ImageView by lazy { view.shareArticle }
//
//    }


}

class ArticleViewHolder(itemView: View, private val clickListener: ClickListener) :
    RecyclerView.ViewHolder(itemView) {

    private val placeHolderImage =
        "https://www.albertadoctors.org/images/ama-master/feature/Stock%20photos/News.jpg"

    fun setData(article: Article) {
        itemView!!.apply {
            if (article.urlToImage == null || article.urlToImage.isEmpty()) {
                Glide.with(article_urlToImage).load(placeHolderImage)
                    .into(article_urlToImage)
            } else {
                Glide.with(article_urlToImage).load(article.urlToImage)
                    .into(article_urlToImage)
            }

            article_title.text = article?.title
            var date: String = article!!.publishedAt
            date = date.substring(0, 10)
            tvPublishDate.text = date

            shareArticle.setOnClickListener {
                clickListener.shareClick(article, adapterPosition)
            }

            itemView.setOnClickListener {
                clickListener.onClick(article, adapterPosition)
            }

        }
    }


}