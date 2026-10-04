package practice



object Try {
  @main
  def lesson1(): Unit = {
    val name: String = "小明"
    var age: Int = 18
    age += 1
    println(s"${name}今年${age}岁")
  }
}
