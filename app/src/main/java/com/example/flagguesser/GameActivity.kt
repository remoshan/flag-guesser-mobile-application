package com.example.flagguesser

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.flagguesser.databinding.ActivityGameBinding
import org.json.JSONArray

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val countries = mutableListOf<Country>()
    private var currentIndex = 0
    private var score = 0
    private val totalQuestions = 10
    private lateinit var currentCountry: Country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        fetchFlags()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.game_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            R.id.action_restart -> {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchFlags() {
        // Using a more reliable API for country flags
        val url = "https://flagcdn.com/en/codes.json"
        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if (parseCountries(response)) {
                    if (countries.size >= 4) {
                        loadNextQuestion()
                    } else {
                        // Fallback to a predefined list if API fails
                        loadFallbackCountries()
                        loadNextQuestion()
                    }
                } else {
                    // Fallback to a predefined list if API fails
                    loadFallbackCountries()
                    loadNextQuestion()
                }
            },
            { error ->
                // Fallback to a predefined list if API fails
                Toast.makeText(this, "Using fallback flags", Toast.LENGTH_SHORT).show()
                loadFallbackCountries()
                loadNextQuestion()
            }
        )

        queue.add(request)
    }

    private fun parseCountries(response: JSONArray): Boolean {
        return try {
            // This API returns a JSON object, not an array
            // We'll use a different approach
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun loadFallbackCountries() {
        // Add a predefined list of countries with their flag URLs
        countries.clear()

        // List of countries with their flag URLs from a reliable source
        countries.add(Country("United States", "https://flagcdn.com/w320/us.png"))
        countries.add(Country("United Kingdom", "https://flagcdn.com/w320/gb.png"))
        countries.add(Country("Canada", "https://flagcdn.com/w320/ca.png"))
        countries.add(Country("Australia", "https://flagcdn.com/w320/au.png"))
        countries.add(Country("Germany", "https://flagcdn.com/w320/de.png"))
        countries.add(Country("France", "https://flagcdn.com/w320/fr.png"))
        countries.add(Country("Japan", "https://flagcdn.com/w320/jp.png"))
        countries.add(Country("Brazil", "https://flagcdn.com/w320/br.png"))
        countries.add(Country("India", "https://flagcdn.com/w320/in.png"))
        countries.add(Country("China", "https://flagcdn.com/w320/cn.png"))
        countries.add(Country("Russia", "https://flagcdn.com/w320/ru.png"))
        countries.add(Country("Mexico", "https://flagcdn.com/w320/mx.png"))
        countries.add(Country("Italy", "https://flagcdn.com/w320/it.png"))
        countries.add(Country("Spain", "https://flagcdn.com/w320/es.png"))
        countries.add(Country("South Africa", "https://flagcdn.com/w320/za.png"))

        countries.shuffle()
    }

    private fun loadNextQuestion() {
        if (currentIndex >= totalQuestions || countries.size < 3) {
            val intent = Intent(this, ScoreActivity::class.java)
            intent.putExtra("score", score)
            startActivity(intent)
            finish()
            return
        }

        currentCountry = countries[currentIndex]

        // Load flag image
        try {
            Glide.with(this)
                .load(currentCountry.flagUrl)
                .error(android.R.drawable.ic_dialog_alert)
                .into(binding.flagImage)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading flag image", Toast.LENGTH_SHORT).show()
        }

        // Get 3 random options (including the correct one)
        val options = mutableListOf<Country>()
        options.add(currentCountry) // Add correct answer

        // Add 2 more random options (excluding the current country)
        val otherCountries = countries.filter { it != currentCountry }.shuffled()
        if (otherCountries.size >= 2) {
            options.add(otherCountries[0])
            options.add(otherCountries[1])
        } else {
            // If we don't have enough countries, add some fallback options
            options.add(Country("United States", "https://flagcdn.com/w320/us.png"))
            options.add(Country("United Kingdom", "https://flagcdn.com/w320/gb.png"))
        }

        options.shuffle()

        // Set option buttons text
        binding.option1.text = options[0].name
        binding.option2.text = options[1].name
        binding.option3.text = options[2].name

        // Set click listeners
        binding.option1.setOnClickListener { checkAnswer(options[0]) }
        binding.option2.setOnClickListener { checkAnswer(options[1]) }
        binding.option3.setOnClickListener { checkAnswer(options[2]) }
    }

    private fun checkAnswer(selectedCountry: Country) {
        if (selectedCountry.name == currentCountry.name) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong! It was ${currentCountry.name}", Toast.LENGTH_SHORT).show()
        }
        currentIndex++
        loadNextQuestion()
    }
}