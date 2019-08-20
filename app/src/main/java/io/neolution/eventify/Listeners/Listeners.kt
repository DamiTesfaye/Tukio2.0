package io.neolution.eventify.Listeners

import co.paystack.android.model.Card

/**
 * Created by Big-Nosed Developer on the Edge of Infinity.
 */
interface OnGuestAddedListener {
    fun onGuestAdded(name: String, bio: String)
}

interface OnChipSelected{
    fun onChipSelected(chipText: String)

    fun onChipDeselected(chipText: String)
}

interface OnEventTagsSelected{
    fun onTagsSelected (chipTexts: MutableList<String>)
}

interface OnUpdatesAdded{
    fun onDoneBtnClicked(updateTitle: String, updateDescription: String)
}

interface OnHomeFragmentsAttached{
    fun onHomeFragmentAttached()

    fun onExploreFragmentAttached()

    fun onUpdateFragmentAttached()

    fun onTrendsFragmentAttached()

    fun onProfileFragmentAttached()
}

interface OnEventTypeSelected{
    fun onEventTypeClicked(eventType: String)
}

interface OnAddReminderClicked{
    fun OnAddReminderButtonClicked(timeInMillis: Long)
}

interface OnAudienceRangeSelected{
    fun onAudienceRangeSelected(amountPaid: Int)
}

interface OnCardDetailsInputted{
    fun onCardInputted(card: Card)
}

interface OnNameBioInputted{
    fun onNameBioDone(name: String, bio: String)
}

interface OnShareEventClicked{
    fun onShareButtonClick(eventTitle: String, eventId: String, eventLocation: String,
                           eventDate: String)
}

interface OnEditProfileClicked{
    fun onEditButtonClicked()
}

interface OnAuthLevelClicked{
    fun goSignUp()

    fun goSignIn()

    fun recoverPassword()
}