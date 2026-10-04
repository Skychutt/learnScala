package workshheet

import scala.io.StdIn.readInt

object Q2 {
  @main
  def main(): Unit =
    println(" enter a single integer value:")
    val num= readInt()
    var i = 1
    while (i <= num) {
      if (i % 3 == 0){
        print(s"$i ")
      }
      i += 1
  }

}
