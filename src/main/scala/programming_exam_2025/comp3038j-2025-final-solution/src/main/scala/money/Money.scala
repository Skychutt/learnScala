package money

case class Money(currency: Currency, units: Int, centiunits: Int):
    private def cents: Long = units.toLong * 100 + centiunits

    def +(m: Money): Money =
        if currency != m.currency then
            throw new IllegalArgumentException("Tried to add two amounts with different currencies")
        Money.fromCents(currency, cents + m.cents)

    def -(m: Money): Money =
        if currency != m.currency then
            throw new IllegalArgumentException("Tried to subtract two amounts with different currencies")
        val c = cents - m.cents
        if c < 0 then
            throw new IllegalArgumentException("Subtraction resulting in a negative amount of money")
        Money.fromCents(currency, c)

    def *(quantity: Int): Money = Money.fromCents(currency, cents * quantity)

object Money:
    def fromCents(currency: Currency, cents: Long): Money = Money(currency, (cents / 100).toInt, (cents % 100).toInt)
