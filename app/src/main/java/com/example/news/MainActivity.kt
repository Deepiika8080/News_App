package com.example.news

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest


class MainActivity : AppCompatActivity(), NewsItemClicked {
    private lateinit var mAdapter: NewsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchData()

        mAdapter = NewsListAdapter(this)
        recyclerView.adapter = mAdapter
    }
    private fun fetchData(){
       // val url = "https://newsdata.io/api/1/latest?apikey=pub_5718176d38e27cbf94f6b809ba95161ff685e&q=pizza"
        val url = "https://newsdata.io/api/1/latest?apikey=pub_5718176d38e27cbf94f6b809ba95161ff685e&country=in&q=sports"
        //val url = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=3321943a651944a088dcccdf83f26673"

        Log.d("im","imThere")
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener{
                    response ->
                // Handle the response
                Log.d("response",response.toString() )
                val newsJsonArray = response.getJSONArray("results")
                val newsArray = ArrayList<News>()

                for(i in 0 until newsJsonArray.length()) {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.optString("title", "No Title"),
                        newsJsonObject.optString("source_name", "Unknown Author"),
                        newsJsonObject.optString("link", ""),
                        newsJsonObject.optString("image_url", "")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            {
                    error ->
                // Handle the error
                if (error.networkResponse != null) {
                    Log.e("VolleyError", "Status code: ${error.networkResponse.statusCode}")
                    Log.e("VolleyError", "Data: ${String(error.networkResponse.data)}")
                }
                Toast.makeText(this, "Failed to fetch news. Please try again.", Toast.LENGTH_SHORT).show()
            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

    override fun onItemClicked(item: News) {

        val builder = CustomTabsIntent.Builder()
        val intent = builder.build()

        intent.launchUrl(this@MainActivity, Uri.parse(item.link))
    }
}