package lessons

/** 每课共用的打印格式，让讲解层次固定：为什么 → 演示 → 易错 → 小结 → 练习。 */
object Teach {

  def why(text: String): Unit = {
    println("【为什么学】")
    println("  " + text)
    println()
  }

  def section(title: String): Unit = {
    println()
    println(s"── $title ──")
  }

  def say(text: String): Unit = println("  " + text)

  def pitfall(text: String): Unit = println("  [易错] " + text)

  def vsJava(text: String): Unit = println("  [对照 Java] " + text)

  def summary(items: String*): Unit = {
    println()
    println("【小结】")
    items.zipWithIndex.foreach { case (item, i) =>
      println(s"  ${i + 1}. $item")
    }
  }

  def practice(text: String): Unit = {
    println()
    println("【练习】" + text)
  }
}
