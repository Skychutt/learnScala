package money

object CurrencyConverter:
    import Currency.*
    private val rates: Map[(Currency, Currency), Double] =
        Map(
            (EUR, USD) -> 1.16206,
            (EUR, GBP) -> 0.88181,
            (EUR, CNY) -> 8.24901,
            (USD, EUR) -> 0.86035,
            (USD, GBP) -> 0.75878,
            (USD, CNY) -> 7.09860,
            (GBP, EUR) -> 1.13298,
            (GBP, USD) -> 1.31669,
            (GBP, CNY) -> 9.34663,
            (CNY, EUR) -> 0.12118,
            (CNY, USD) -> 0.14085,
            (CNY, GBP) -> 0.10688
        )

    def convert(money: Money, currency: Currency): Money =
        if money.currency == currency then money
        else
            val rate = rates.getOrElse(
                (money.currency, currency),
                throw new IllegalArgumentException(s"No exchange rate for ${money.currency} to $currency")
            )
            Money.fromCents(currency, math.round((money.units * 100L + money.centiunits) * rate))
