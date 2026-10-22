import java.time.LocalDate
import java.time.Period


enum Currency {
  case CNY, EUR, USD,  GBP
}

case class Address private ( street:String, area:String, city:String, region:String,
                             country:String, postcode:String) {
  require(List(street,city,country,postcode).forall(_.trim.nonEmpty),"Street, city, country and postcode are all valid non empty values")
}

object Address {

  def create(street: String, area: String, city: String, region: String,
             country: String, postcode: String): Either[String, Address] = {
    if (street.trim.isEmpty) {
      Left("Street should be valid non empty values")
    }

    else if (city.trim.isEmpty) {
      Left("City should be valid non empty values")

    } else if (country.trim.isEmpty) {
      Left("Country should be valid non empty values")

    } else if (postcode.trim.isEmpty) {
      Left("Postcode should be valid non empty values")

    } else {
      Right(new Address(street, area, city, region, country, postcode))
    }
  }
}


case class PhoneNumber private(countryCode:String,areaCode:String,number:String) {

  require(countryCode.matches("[0-9]+"),"Countrycode should be non empty and all digit")
  require(areaCode.matches("[0-9]+"),"Areacode should be non empty and all digit")
  require(number.matches("[0-9]{6,}"),"Number should contains at least 6 digit")

}

object PhoneNumber{
  def create(countryCode:String,areaCode:String,number:String): Either[String,PhoneNumber] = {

    if(countryCode.trim.isEmpty){ Left("Countrycode should be valid and non empty")

    } else if(areaCode.trim.isEmpty){ Left("Areacode should be valid and non empty")

    }else if(number.length<6){ Left("Number should contains at least 6 digits")

    }else Right(new PhoneNumber(countryCode, areaCode, number))


  }
}

case class Customers private(name:String,email:String,dob:String,phoneNumber: PhoneNumber,address: Address) {

  require()
  require()

}



object ExchangeRate {
  
  import Currency.*
  
  private val Map(
    (EUR, USD) -> BigDecimal("1.16206"),
    (USD, EUR) -> BigDecimal("0.86035"),
    (EUR, GBP) -> BigDecimal("0.88181"),
    (GBP, EUR) -> BigDecimal("1.13298"),
    (EUR, CNY) -> BigDecimal("8.24901"),
    (CNY, EUR) -> BigDecimal("0.12118"),
    (USD, GBP) -> BigDecimal("0.75878"),
    (GBP, USD) -> BigDecimal("1.31669"),
    (USD, CNY) -> BigDecimal("7.09860"),
    (CNY, USD) -> BigDecimal("0.14085"),
    (CNY, GBP) -> BigDecimal("0.10688"),
    (GBP, CNY) -> BigDecimal("9.34663")
  )
}


case class Money(currency: Currency,amount:Double) {

}

object Money(){




}


case class Item() {
  
}


