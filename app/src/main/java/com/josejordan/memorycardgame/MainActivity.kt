package com.josejordan.memorycardgame

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.GridLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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

        val newGameButton = findViewById<Button>(R.id.new_game_button)
        newGameButton.setOnClickListener {
            resetGame()
            showSettingsDialog()
        }

        val resetButton = findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            resetGame()
        }


        shuffleCards()
        setupButtonClickListeners()
        showSettingsDialog()
    }

    private fun setupButtonClickListeners() {
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.setOnClickListener { onCardClick(button) }
            buttons.add(button)
        }
    }
    private fun setupCards(cardCount: Int) {
        gridLayout.removeAllViews()
        val cardImages = arrayOf(
            R.drawable.card1,
            R.drawable.card2,
            R.drawable.card3,
            R.drawable.card4,
            R.drawable.card5,
            R.drawable.card6,
            R.drawable.card7,
            R.drawable.card8,

        )

        cardPairs.clear()

        if (cardCount <= cardImages.size * 2) {
            for (i in 0 until cardCount / 2) {
                cardPairs.add(Pair(cardImages[i % cardImages.size], cardImages[i % cardImages.size]))
                cardPairs.add(Pair(cardImages[i % cardImages.size], cardImages[i % cardImages.size]))
            }
            cardPairs.shuffle()
        } else {
           Toast.makeText(this, getString(R.string.number_invalid), Toast.LENGTH_SHORT).show()
        }
    }

    private fun shuffleCards() {
        // Elimina todos los botones del GridLayout antes de agregar nuevos
        gridLayout.removeAllViews()

        cardPairs.shuffle()
        for (i in 0 until cardPairs.size) {
            val button = Button(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = resources.getDimensionPixelSize(R.dimen.card_width)
                    height = resources.getDimensionPixelSize(R.dimen.card_height)
                    setMargins(5, 5, 5, 5)
                }
                tag = cardPairs[i].first
                setBackgroundResource(R.drawable.card_back)
                setOnClickListener { onCardClick(this) }
            }
            gridLayout.addView(button)
        }
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.settings_dialog, null)
        builder.setView(view)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group)
        val applyButton = view.findViewById<Button>(R.id.apply_button)

        val dialog = builder.create()
        applyButton.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.radio_4 -> {
                    setupCards(4)
                    gridLayout.columnCount = 2
                    gridLayout.rowCount = 2
                }
                R.id.radio_6 -> {
                    setupCards(6)
                    gridLayout.columnCount = 3
                    gridLayout.rowCount = 2
                }
                R.id.radio_16 -> {
                    setupCards(16)
                    gridLayout.columnCount = 4
                    gridLayout.rowCount = 4
                }
            }
            shuffleCards()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun onCardClick(button: Button) {
        if (isProcessing || button == firstCard) return
        button.setBackgroundResource((button.tag as Int))
        if (firstCard == null) {
            firstCard = button
        } else {
            attempts++
            attemptCounter.text = getString(R.string.intentos_label, attempts)
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
       Toast.makeText(this, getString(R.string.you_win), Toast.LENGTH_LONG).show()
    }

    private fun resetGame() {
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
        attemptCounter.text = getString(R.string.intentos_label, attempts)
    }

}
