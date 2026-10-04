package lessons

/** 第 12 课：class、主构造器、辅助构造器、封装。 */
object Lesson12_Classes {

  class Person(val name: String, var age: Int) {
    println(s"    正在构造 Person($name, $age)")

    def this(name: String) = {
      this(name, 0)
      println("    辅助构造器：年龄默认 0")
    }

    def greet(): String = s"我是 $name，今年 $age 岁"

    def haveBirthday(): Unit = { age += 1 }
  }

  class BankAccount(private var balance: Int) {
    def deposit(amount: Int): Unit = {
      require(amount > 0, "金额必须为正")
      balance += amount
    }
    def withdraw(amount: Int): Boolean = {
      if (amount > 0 && amount <= balance) {
        balance -= amount
        true
      } else false
    }
    def current: Int = balance
  }

  def run(): Unit = {
    Teach.why("现实世界的东西有数据、有行为。class 把它们捆在一起。参数写在类名后面，那就是主构造器——比 Java 少写很多样板代码。")

    Teach.section("1. 主构造器参数怎么写")
    Teach.say("val name  → 对外只读字段")
    Teach.say("var age   → 对外可改字段")
    Teach.say("amount: Int（无 val/var）→ 只是构造器参数，不会自动变成字段")
    Teach.say("private var balance → 字段存在，但外面看不见")

    Teach.section("2. 构造与方法")
    val p1 = Person("Alice", 20)
    println("    " + p1.greet())
    p1.haveBirthday()
    println("    过生日后：" + p1.greet())
    val p2 = Person("Bob")
    println("    " + p2.greet())
    Teach.say("Scala 3 里 new 可以省略：Person(\"Alice\", 20) 和 new Person(...) 都行。")
    Teach.say("类体里的代码（比如那句 println）每次构造都会执行。")

    Teach.section("3. 辅助构造器")
    Teach.say("def this(...)  第一句必须调用 this(...) 或另一个辅助构造器，最终要接到主构造器。")
    Teach.say("能用默认参数解决的，不一定要写辅助构造器。Person 的 age 其实也可以写成 age: Int = 0。")

    Teach.section("4. 封装：别让外面直接改余额")
    val acc = BankAccount(100)
    acc.deposit(50)
    println(s"    存 50 后余额 = ${acc.current}")
    println(s"    取 30 成功？ ${acc.withdraw(30)}，余额 ${acc.current}")
    println(s"    取 999 成功？ ${acc.withdraw(999)}，余额 ${acc.current}")
    Teach.say("// acc.balance  编译错误，因为是 private")
    Teach.pitfall("require(条件, 消息) 条件失败会抛 IllegalArgumentException，适合检查明显非法的参数。")

    Teach.summary(
      "class 名字(val 只读, var 可改, 无修饰=仅参数)",
      "辅助构造器第一句必须 this(...)",
      "不想被外面乱改的状态：private var + 公开方法"
    )
    Teach.practice("写 Rectangle(width, height)，提供 area、perimeter；宽高必须为正，否则 require 失败。")
  }
}
