package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private var searchText: String? = null
    private lateinit var editTextSearch: EditText
    private var lastFailedRequest: String? = null
    private lateinit var imageViewBack: ImageView
    private lateinit var imageViewReset: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageViewError: ImageView
    private lateinit var textViewError: TextView
    private lateinit var buttonUpDate: Button
    private lateinit var linerLayoutError: LinearLayout
    private lateinit var textViewYouSearch: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    private val translateBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(translateBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackAPI::class.java)

    private val searchResults = ArrayList<Track>()
    private val searchHistoryTracks = ArrayList<Track>()
    val adapter = TrackAdapter(onTrackClick = {onTrackClick(it)})
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageViewBack = findViewById(R.id.imageViewBack)
        imageViewReset = findViewById(R.id.imageViewReset)
        recyclerView = findViewById(R.id.recyclerView)
        imageViewError = findViewById(R.id.imageViewError)
        textViewError = findViewById(R.id.textViewError)
        buttonUpDate = findViewById(R.id.buttonUpDate)
        linerLayoutError = findViewById(R.id.linerLayoutError)
        editTextSearch = findViewById(R.id.editTextSearch)
        textViewYouSearch = findViewById(R.id.textViewYouSearch)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        adapter.tracks = searchResults
        recyclerView.adapter = adapter

        searchHistory = SearchHistory(getSharedPreferences("app_prefs", MODE_PRIVATE))
        searchHistoryTracks.addAll(searchHistory.getTracks())

        editTextSearch.setOnFocusChangeListener { view, hasFocus ->
            if (searchHistoryTracks.isNotEmpty() && hasFocus) {
                visibilitySearchView()
                upDateAdapterInHistory()
                adapter.notifyDataSetChanged()
            } else {
                goneSearchView()
            }
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            searchHistoryTracks.clear()
            adapter.notifyDataSetChanged()
            goneSearchView()
        }

        imageViewBack.setOnClickListener {
            finish()
        }

        imageViewReset.setOnClickListener {
            editTextSearch.setText("")
            searchResults.clear()
            upDateAdapterInHistory()
            adapter.notifyDataSetChanged()
            hideKeyboard()
            errorClose()
        }

        editTextSearch.setOnClickListener {
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    imageViewReset.visibility = View.INVISIBLE
                    checkEmptyHistory()
                } else {
                    imageViewReset.visibility = View.VISIBLE
                    searchText = s.toString()
                    goneSearchView()

                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        editTextSearch.addTextChangedListener(simpleTextWatcher)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT)
            editTextSearch.setText(searchText)
        }

        buttonUpDate.setOnClickListener {
            performSearch(lastFailedRequest)
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                upDateAdapterInSearch()
                performSearch(searchText)
            }
            false
        }
    }

    fun performSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            showErrorNothingFound(true)
            return
        }

        trackService.findTrack(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                if (response.code() == 404) {
                    showErrorNothingFound(true)
                } else if (!response.isSuccessful) {
                    showErrorCommunicationProblems(true)
                    lastFailedRequest = query
                } else {
                    val searchResponse = response.body()
                    if (searchResponse != null && searchResponse.results.isNotEmpty()) {
                        searchResults.clear()
                        searchResults.addAll(searchResponse.results.map {
                            Track(
                                it.trackId,
                                it.trackName,
                                it.artistName,
                                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                                it.artworkUrl100,
                                it.collectionName,
                                it.releaseDate,
                                it.primaryGenreName,
                                it.country)
                        })
                        adapter.notifyDataSetChanged()
                        showErrorCommunicationProblems(false)
                    } else {
                        showErrorNothingFound(true)
                    }
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showErrorCommunicationProblems(true)
                lastFailedRequest = query
            }
        })
    }

    private fun onTrackClick(track: Track) {
        searchHistory.addTrack(track)
        searchHistoryTracks.clear()
        searchHistoryTracks.addAll(searchHistory.getTracks())
        adapter.notifyDataSetChanged()
        startActivityAudioPlayer(track)
    }

    fun errorClose() {
        linerLayoutError.visibility = View.GONE
    }

    fun showErrorNothingFound(show: Boolean) {
        linerLayoutError.visibility = if (show) View.VISIBLE else View.GONE
        textViewError.text = getString(R.string.Nothing_found)
        imageViewError.setImageResource(R.drawable.nothing_was_found)
        buttonUpDate.visibility = View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    fun showErrorCommunicationProblems(show: Boolean) {
        linerLayoutError.visibility = if (show) View.VISIBLE else View.GONE
        textViewError.text = getString(R.string.Communication_problems)
        imageViewError.setImageResource(R.drawable.communication_problems)
        buttonUpDate.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT)
        editTextSearch.setText(searchText)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    private fun goneSearchView() {
        recyclerView.visibility = View.GONE
        textViewYouSearch.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun visibilitySearchView() {
        recyclerView.visibility = View.VISIBLE
        textViewYouSearch.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
    }

    private fun upDateAdapterInHistory() {
        adapter.tracks = searchHistoryTracks
    }

    private fun upDateAdapterInSearch() {
        adapter.tracks = searchResults
    }

    private fun checkEmptyHistory() {
        if (searchHistoryTracks.isNotEmpty()) {
            visibilitySearchView()
            upDateAdapterInHistory()
            errorClose()
            adapter.notifyDataSetChanged()
        } else {
            goneSearchView()
        }
    }

    private fun startActivityAudioPlayer(track: Track) {
        val jsonTrack = Gson().toJson(track)

        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("SELECTED_TRACK", jsonTrack)
        startActivity(intent)
    }

}