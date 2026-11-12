package customer

case class Address(street: String, area: String, city: String, region: String, country: String, postcode: String)

object Address:
    def apply(
        street: String,
        area: String,
        city: String,
        region: String,
        country: String,
        postcode: String
    ): Either[String, Address] =
        if street.trim.isEmpty then Left("Street cannot be empty")
        else if city.trim.isEmpty then Left("City cannot be empty")
        else if country.trim.isEmpty then Left("Country cannot be empty")
        else if postcode.trim.isEmpty then Left("Postcode cannot be empty")
        else Right(new Address(street, area, city, region, country, postcode))
