package customer

case class PhoneNumber(countryCode: Option[String], areaCode: String, number: String)

object PhoneNumber:
    def apply(countryCode: Option[String], areaCode: String, number: String): Either[String, PhoneNumber] =
        if countryCode.exists(c => !allDigits(c)) then
            Left(s"Country code can only contain digits '${countryCode.get}'")
        else if areaCode.isEmpty || !allDigits(areaCode) then Left(s"Area code must be digits '$areaCode'")
        else if !allDigits(number) then Left(s"Number can only contain digits '$number'")
        else if number.length < 6 then Left(s"Phone numbers must contain at least 6 digits '$number'")
        else Right(new PhoneNumber(countryCode, areaCode, number))

    private def allDigits(s: String): Boolean = s.forall(_.isDigit)
