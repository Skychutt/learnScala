package examtry

case class Address(
                            street: String,    // 街道
                            area: String,      // 区域：题目不要求非空
                            city: String,      // 城市
                            region: String,    // 地区：题目不要求非空
                            country: String,   // 国家
                            postcode: String   // 邮编用字符串，保留字母、空格和前导零
                          ) {
  require(List(street,area,city,region,country,postcode).forall(_.trim.nonEmpty),"street,area,city,region,country,postcode can not be empty")

}

object Address {
  def create(street: String,
             area: String,
             city: String,
             region: String,
             country: String,
             postcode: String): Either[String,Address] = {
    if(street.trim.isEmpty){Left ("street can not be empty")}
    else if (city.trim.isEmpty){Left ("city can not be empty")}
    else if (area.trim.isEmpty){Left ("area can not be empty")}
    else if (region.trim.isEmpty){Left ("region can not be empty")}
    else if (country.trim.isEmpty){Left ("country can not be empty")}
    else if(postcode.trim.isEmpty){Left ("postcode can not be empty")}
    else Right(new Address(street, area, city, region, country, postcode))
  }
}
