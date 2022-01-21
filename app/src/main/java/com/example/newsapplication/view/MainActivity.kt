package com.example.newsapplication.view

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newsapplication.data.remote.ApiService
import com.example.newsapplication.data.remote.Article
import com.example.newsapplication.data.remote.ClickListener
import com.example.newsapplication.data.remote.ResponseModel
import com.example.newsapplication.view.recyclerview.ArticleAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent
import com.example.newsapplication.R


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, ClickListener {

    private val ENDPOINT_URL by lazy { "https://newsapi.org/v2/" }
    private lateinit var topHeadlinesEndpoint: ApiService
    private lateinit var newsApiConfig: String
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleList: ArrayList<Article>
    private lateinit var userKeyWordInput: String

    // RxJava related fields
    private lateinit var topHeadlinesObservable: Observable<ResponseModel>
    private lateinit var compositeDisposable: CompositeDisposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Network request
        val retrofit: Retrofit = generateRetrofitBuilder()
        topHeadlinesEndpoint = retrofit.create(ApiService::class.java)
        newsApiConfig = resources.getString(R.string.news_api_key)
        swipe_refresh.setOnRefreshListener(this)
        swipe_refresh.setColorSchemeResources(R.color.purple_200)
        articleList = ArrayList()
        articleAdapter = ArticleAdapter(articleList, this)
        //When the app is launched of course the user input is empty.
        userKeyWordInput = ""
        //CompositeDisposable is needed to avoid memory leaks
        compositeDisposable = CompositeDisposable()
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = articleAdapter

        checkUserKeywordInput()
    }

    override fun onStart() {
        super.onStart()
//        checkUserKeywordInput()
    }

    override fun onDestroy() {
        super.onDestroy()
//        compositeDisposable.clear()
    }

    override fun onRefresh() {
//        checkUserKeywordInput()
    }

    private fun checkUserKeywordInput() {
        if (userKeyWordInput.isEmpty()) {
            queryTopHeadlines()
        } else {
            getKeyWordQuery(userKeyWordInput)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.menu_main, menu)
            //Creates input field for the user search
            setUpSearchMenuItem(menu)
        }
        return true
    }

    private fun setUpSearchMenuItem(menu: Menu) {
        val searchManager: SearchManager =
            (getSystemService(Context.SEARCH_SERVICE)) as SearchManager
        val searchView: SearchView = ((menu.findItem(R.id.action_search)?.actionView)) as SearchView
        val searchMenuItem: MenuItem = menu.findItem(R.id.action_search)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Type any keyword to search..."
        searchView.setOnQueryTextListener(onQueryTextListenerCallback())
        searchMenuItem.icon.setVisible(false, false)
    }

    //Gets immediately triggered when user clicks on search icon and enters something
    private fun onQueryTextListenerCallback(): SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }

            override fun onQueryTextChange(userInput: String?): Boolean {
                return checkQueryText(userInput)
            }
        }
    }

    private fun checkQueryText(userInput: String?): Boolean {
        if (userInput != null && userInput.length > 1) {
            userKeyWordInput = userInput
            getKeyWordQuery(userInput)
        } else if (userInput != null && userInput == "") {
            userKeyWordInput = ""
            queryTopHeadlines()
        }
        return false
    }


    private fun getKeyWordQuery(userKeywordInput: String) {
        swipe_refresh.isRefreshing = true
        if (userKeywordInput.isNotEmpty()) {
            topHeadlinesObservable =
                topHeadlinesEndpoint.getUserQueryNews(newsApiConfig, userKeywordInput)
            subscribeObservableOfArticle()
        } else {
            queryTopHeadlines()
        }
    }

    private fun queryTopHeadlines() {
        swipe_refresh.isRefreshing = true
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        topHeadlinesObservable = topHeadlinesEndpoint.getTrendingNews(currentDate)
        subscribeObservableOfArticle()
    }

    private fun subscribeObservableOfArticle() {
        articleList.clear()
        compositeDisposable.add(
            topHeadlinesObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.fromIterable(it.articles)
                }
                .subscribeWith(createArticleObserver())
        )
    }

    private fun createArticleObserver(): DisposableObserver<Article> {
        return object : DisposableObserver<Article>() {
            override fun onNext(article: Article) {
                if (!articleList.contains(article)) {
                    articleList.add(article)
                }
            }

            override fun onComplete() {
                showArticlesOnRecyclerView()
            }

            override fun onError(e: Throwable) {
                Log.e("createArticleObserver", "Article error: ${e.message}")
            }
        }
    }

    private fun showArticlesOnRecyclerView() {
        if (articleList.size > 0) {
            empty_text.visibility = View.GONE
            retry_fetch_button.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
            articleAdapter.setArticles(articleList)
        } else {
            recycler_view.visibility = View.GONE
            empty_text.visibility = View.VISIBLE
            retry_fetch_button.visibility = View.VISIBLE
            retry_fetch_button.setOnClickListener { checkUserKeywordInput() }
        }
        swipe_refresh.isRefreshing = false
    }

    private fun generateRetrofitBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(ENDPOINT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //Add RxJava2CallAdapterFactory as a Call adapter when building your Retrofit instance
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    override fun onClick(article: Article, position: Int) {
//        Toast.makeText(this, "article no $position clicked", Toast.LENGTH_SHORT).show()

        val args = Bundle()
        args.putString("author", article.author)
        args.putString("content", article.content)
        args.putString("description", article.description)
        args.putString("publishedAt", article.publishedAt)
//        args.putString("source", article.source) since source is a data class
        args.putString("title", article.title)
        args.putString("url", article.url)
        args.putString("urlToImage", article.urlToImage)

        val intent = Intent(this, ReadArticleActivity::class.java)
        intent.putExtra("bundle", args)
        startActivity(intent)

//        val readArticle = ReadArticle()
//        readArticle.arguments = args
//
//        supportFragmentManager.beginTransaction()
//            .add(android.R.id.content, readArticle).addToBackStack("").commit()

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
    }

    override fun shareClick(article: Article, position: Int) {
//        Toast.makeText(this, "share clicked", Toast.LENGTH_SHORT).show()
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        share.putExtra(Intent.EXTRA_SUBJECT, article.title)
        share.putExtra(Intent.EXTRA_TEXT, article.url)
        startActivity(Intent.createChooser(share, "Share link!"))
    }
}