package examtry

object Demo{

  @main
  def run: Unit = {
    val address = new Address("","","","","","")
  }

  @main
  def run1(): Unit={
    val address = Address.create("","","","","","")
    println(address)
  }

}
