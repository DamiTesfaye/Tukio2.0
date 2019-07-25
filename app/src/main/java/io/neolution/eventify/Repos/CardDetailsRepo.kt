package io.neolution.eventify.Repos

import android.content.Context
import co.paystack.android.model.Card

class CardDetailsRepo(val context: Context) {

    private val cardDetailsPreference = context.getSharedPreferences("cardDetails", Context.MODE_PRIVATE)
    private val firstTimePromoting = context.getSharedPreferences("firstPromoted", Context.MODE_PRIVATE)

    fun saveUserCardDetails(card: Card) : Boolean{
        val editor = cardDetailsPreference.edit()

        editor.putString("userCardNumber", card.number)
        editor.putString("userCardCVC", card.cvc)
        editor.putInt("userExpMonth", card.expiryMonth)
        editor.putInt("userExpYear", card.expiryYear)

        return editor.commit()
    }

    fun getUserCardDetails(): Card{
        val userCardNumber = cardDetailsPreference.getString("userCardNumber", "")
        val userCardCVC = cardDetailsPreference.getString("userCardCVC", "")
        val userExpMonth = cardDetailsPreference.getInt("userExpMonth", 0)
        val userExpYear = cardDetailsPreference.getInt("userExpYear", 0)

        val card = Card.Builder(userCardNumber, userExpMonth, userExpYear, userCardCVC)
        return card.build()

    }

    fun getFirstTimePromoting(): Boolean{
        val firstTime = firstTimePromoting.getString("firstTime", "")
        return firstTime.isEmpty()
    }

    fun firstTimePromoting(): Boolean{
        val editor = firstTimePromoting.edit()
        editor.putString("firstTime", "firstTime")
        return editor.commit()
    }

}