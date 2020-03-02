package hu.nemi.spear.sample.android.data

data class RestQuoteOfTheDay(val contents: RestContent)

data class RestContent(val quotes: List<RestQuote>)

data class RestQuote(val quote: String)