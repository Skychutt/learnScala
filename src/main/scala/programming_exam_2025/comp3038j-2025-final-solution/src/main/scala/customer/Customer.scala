package customer

import money.Currency
import java.time.LocalDate

case class Customer(
    customerId: String,
    name: String,
    dob: LocalDate,
    email: String,
    phone: PhoneNumber,
    currency: Currency,
    address: Address
)
