package io.neolution.eventify.View.CustomViews

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import co.paystack.android.model.Card
import io.neolution.eventify.Listeners.OnCardDetailsInputted
import io.neolution.eventify.R
import io.neolution.eventify.Repos.CardDetailsRepo

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
class CardDetailsDialog(): DialogFragment() {

    lateinit var onCardDetailsInputted: OnCardDetailsInputted
    lateinit var dialogRepoCard: CardDetailsRepo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.card_details_dilog, container, false)

        val cardNumberET = view.findViewById<EditText>(R.id.card_details_number_et)
        val cardExpiryYearET = view.findViewById<EditText>(R.id.card_details_expiry_year_et)
        val cardExpiryMonthET = view.findViewById<EditText>(R.id.card_details_expiry_month_et)
        val cardCvcET = view.findViewById<EditText>(R.id.card_details_cvc_et)
        val doneBTN = view.findViewById<Button>(R.id.card_details_done_btn)

        dialogRepoCard = CardDetailsRepo(context!!)
        val card = dialogRepoCard.getUserCardDetails()

        if (!card.cvc.isNullOrEmpty() && !card.number.isNullOrEmpty() && card.expiryMonth != 0 && card.expiryYear != 0){
            if (card.isValid){
                cardNumberET.setText(card.number)
                cardExpiryMonthET.setText(card.expiryMonth.toString())
                cardExpiryYearET.setText(card.expiryYear.toString())
                cardCvcET.setText(card.cvc)
            }
        }

        val amountPaid = arguments!!.getInt("amountPaid")
        val dialogLabelText = "Please input your atm card details as specified. \n The amount for your selected audience range is $amountPaid"

        val dialogLabel = view.findViewById<TextView>(R.id.card_details_label)
        dialogLabel.text = dialogLabelText


        doneBTN.setOnClickListener {

            val cardNumber = cardNumberET.text.toString()
            val cardExpiryYear = cardExpiryYearET.text.toString().toInt()
            val cardExpiryMonth = cardExpiryMonthET.text.toString().toInt()
            val cardCvc = cardCvcET.text.toString()



            if (cardCvc.isNotEmpty() && cardNumber.isNotEmpty() && cardExpiryMonth != null && cardExpiryYear != null){
                val cardD  = Card.Builder(cardNumber, cardExpiryMonth, cardExpiryYear, cardCvc).build()
                val done = dialogRepoCard.saveUserCardDetails(cardD)
                if (done){
                    onCardDetailsInputted.onCardInputted(card = cardD)
                }

            }

        }

        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        onCardDetailsInputted = context as OnCardDetailsInputted
    }
}