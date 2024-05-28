package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible

class SearchActivity : AppCompatActivity() {
    private var searchText: String? = null
    private lateinit var editTextSearch: EditText
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imageViewBack: ImageView = findViewById(R.id.imageViewBack)
        val imageViewReset: ImageView = findViewById(R.id.imageViewReset)
        editTextSearch = findViewById(R.id.editTextSearch)


        imageViewBack.setOnClickListener {
            finish()
        }

        imageViewReset.setOnClickListener {
            editTextSearch.setText("")
            hideKeyboard()
        }

        editTextSearch.setOnClickListener {
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    imageViewReset.visibility = View.INVISIBLE
                } else {
                    imageViewReset.visibility = View.VISIBLE
                    searchText = s.toString()
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
}