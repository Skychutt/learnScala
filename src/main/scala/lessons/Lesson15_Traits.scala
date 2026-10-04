package lessons

/** 第 15 课：trait。一个类只能继承一个类，但可以混入很多特质。 */
object Lesson15_Traits {

  trait Speaker {
    def speak(): String
  }

  trait Walker {
    def walk(): String = "走路"
  }

  trait Named {
    def name: String
    def label: String = s"[$name]"
  }

  class Robot(val name: String) extends Speaker with Walker with Named {
    override def speak(): String = s"$label 哔哔"
  }

  trait Logger {
    def log(msg: String): Unit = println(s"    LOG: $msg")
  }

  trait TimestampLogger extends Logger {
    override def log(msg: String): Unit =
      super.log(s"${java.time.LocalTime.now()} $msg")
  }

  trait UpperLogger extends Logger {
    override def log(msg: String): Unit = super.log(msg.toUpperCase)
  }

  class Service extends Logger {
    def runTask(): Unit = log("task done")
  }

  def run(): Unit = {
    Teach.why("Java 的接口只能描述「能做什么」。Scala 的 trait 既能描述抽象方法，也能带默认实现，还能混入多份能力。这是 Scala 组织代码最常用的手段之一。")

    Teach.section("1. 当接口用，也可以带默认实现")
    val r = Robot("R2")
    println("    " + r.speak())
    println("    " + r.walk())
    Teach.say("class 只能 extends 一个类，但可以 with 很多个 trait。")
    Teach.say("Speaker 是抽象的；Walker、Named 已经有实现，混进去就能用。")

    Teach.section("2. 混入顺序：从右往左叠加")
    val svc = new Service with TimestampLogger with UpperLogger
    println("    混入两个 Logger 之后：")
    svc.runTask()
    Teach.say("调用链大约是：UpperLogger → TimestampLogger → Logger。")
    Teach.say("new Service with A with B  不必先定义一个新类，临时混入即可。")

    Teach.section("3. 什么时候用 class / abstract class / trait")
    Teach.say("要独立实例、独立状态、要构造器参数（老习惯）→ class")
    Teach.say("一组相关类型的公共父级，且可能有构造逻辑 → abstract class")
    Teach.say("可插拔的能力（会飞、会叫、可日志）→ trait，用 with 叠上去")
    Teach.vsJava("Java 8+ 接口也能有 default 方法，但 Scala trait 的叠加规则更系统。")

    Teach.summary(
      "一个类 extends 一个类，with 多个 trait",
      "trait 里既可以有抽象成员，也可以有已实现方法",
      "能力用 trait 混入；「一种东西」用 class 建模"
    )
    Teach.practice("写 trait Flyable 和 Swimmable，做一个 Duck 混入两者，再 new Duck with Logger 打一条日志。")
  }
}
