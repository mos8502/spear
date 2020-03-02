package hu.nemi.spear.sample.android.data

interface RestModelMapper {
    fun RestQuoteOfTheDay.toModel(): String

    companion object: RestModelMapper {
        override fun RestQuoteOfTheDay.toModel(): String =
            contents.quotes.first().quote
    }
}