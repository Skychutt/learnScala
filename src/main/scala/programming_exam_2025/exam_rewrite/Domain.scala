



enum currency {
  case CNY, EUR, USD,  GBP
}

case class Address private ( street:String, area:String, city:String, region:String,
                             country:String, postcode:String) {


  require(List(street,city,country,postcode).forall(_.trim.nonEmpty),"street, city, country and postcode are all valid non empty values")

}
















case class Customers private()









