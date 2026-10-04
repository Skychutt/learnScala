package workshheet

class Date(var years:Int,var months:Int,var dates:Int) {

  def printDate1(): Unit ={
  printf("%04d-%02d-%02d", years, months, dates)
  println()
  }

  def printDate2(): Unit={
  printf("%02d/%02d/%04d", dates, months, years)}

}

object DateTest {

  def main(args: Array[String]): Unit={
    var d = new Date(2026,9,24)
    d.printDate1()
    d.printDate2()
  }

}

