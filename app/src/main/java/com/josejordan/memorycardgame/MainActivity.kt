package com.josejordan.memorycardgame

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private val buttons = mutableListOf<Button>()
    private val cardPairs = mutableListOf<Pair<Int, Int>>()
    private var firstCard: Button? = null
    private var secondCard: Button? = null
    private var isProcessing = false
    private var attempts = 0
    private lateinit var attemptCounter: TextView
    private var pairsFound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        attemptCounter = findViewById(R.id.attempt_counter)
        gridLayout = findViewById(R.id.gridLayout)

        setupCards()
        shuffleCards()
        setupButtonClickListeners()
    }

        private fun setupButtonClickListeners() {
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.setOnClickListener { onCardClick(button) }
            buttons.add(button)
        }
    }

    private fun setupCards() {
        val cardImages = arrayOf(
            R.drawable.card1,
            R.drawable.card2,
        )
        for (card in cardImages) {
            cardPairs.add(Pair(card, card))
            cardPairs.add(Pair(card, card))
        }
        cardPairs.shuffle()
    }

    private fun shuffleCards() {
        cardPairs.shuffle()
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.tag = cardPairs[i].first
                       button.setBackgroundResource(R.drawable.card_back)
        }
    }

    private fun onCardClick(button: Button) {
        if (isProcessing || button == firstCard) return
        button.setBackgroundResource((button.tag as Int))
        if (firstCard == null) {
            firstCard = button
        } else {
            attempts++
            attemptCounter.text = "Intentos: $attempts"
            secondCard = button
            isProcessing = true
            checkMatch()
        }
    }

    private fun checkMatch() {
        if (firstCard?.tag == secondCard?.tag) {
            firstCard?.isEnabled = false
            secondCard?.isEnabled = false
            firstCard = null
            secondCard = null
            isProcessing = false
            pairsFound++

            if (pairsFound == cardPairs.size / 2) {
                showVictoryMessage()
            }

        } else {
            Handler(Looper.getMainLooper()).postDelayed({

                firstCard?.setBackgroundResource(R.drawable.card_back)
                secondCard?.setBackgroundResource(R.drawable.card_back)
                firstCard = null
                secondCard = null
                isProcessing = false
            }, 1000)
        }
    }

    private fun showVictoryMessage() {
        Toast.makeText(this, "¡Has ganado!", Toast.LENGTH_LONG).show()
    }

    fun resetGame(view: View) {
        shuffleCards()
        for (button in buttons) {
            button.setBackgroundResource(R.drawable.card_back)
            button.isEnabled = true
        }
        firstCard = null
        secondCard = null
        isProcessing = false
        pairsFound = 0
        attempts = 0
        attemptCounter.text = "Intentos: $attempts"
    }

}
