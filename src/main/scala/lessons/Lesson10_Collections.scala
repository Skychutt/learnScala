package lessons

import scala.collection.mutable

/** 第 10 课：元组、List / Array / Vector / Set / Map。先分清可变与不可变。 */
object Lesson10_Collections {

  def run(): Unit = {
    Teach.why("几乎所有作业都在处理一堆数据。Scala 默认给你不可变集合：改操作会返回新集合，旧的还在。需要原地修改时再显式用 mutable。")

    Teach.section("1. 元组：临时打包几个值")
    val pair = (1, "Scala")
    val triple = (1, "Scala", true)
    println(s"  pair._1 = ${pair._1}, pair._2 = ${pair._2}   ← 下标从 1 开始")
    val (id, lang) = pair
    println(s"  解构更好读：id=$id lang=$lang")
    println(s"  三元组 $triple")
    Teach.pitfall("元组元素用 _1 _2，不是 _0。超过两三个字段就应该改用 case class。")

    Teach.section("2. List：不可变链表，最常用")
    val list = List(1, 2, 3)
    val list2 = 0 :: list
    println(s"  list     = $list")
    println(s"  0 :: list = $list2   原 list 没变")
    println(s"  空列表 Nil 与 List() 相等？ ${Nil == List()}")
    Teach.say(":: 往头部加，很快。随机按下标访问比较慢。数据量大又要随机访问时用 Vector。")
    Teach.say("List(1,2) :+ 3 是往尾部追加，对长 List 较慢。")

    Teach.section("3. Array：可变、定长")
    val arr = Array("a", "b", "c")
    arr(0) = "A"
    println(s"  Array = ${arr.mkString("[", ", ", "]")}")
    Teach.vsJava("就是 Java 数组。长度固定，下标从 0 开始。打印不要直接 println(arr)，会看到无意义的地址，用 mkString。")

    Teach.section("4. Vector：不可变，随机访问也快")
    val vec = Vector(10, 20, 30).updated(1, 99)
    println(s"  把下标 1 改成 99 → $vec")

    Teach.section("5. Set：不重复")
    val set = Set(1, 2, 2, 3)
    println(s"  Set(1,2,2,3) = $set    包含 2？ ${set.contains(2)}")

    Teach.section("6. Map：键值对")
    val scores = Map("Alice" -> 90, "Bob" -> 80)
    println(s"  $scores")
    println(s"  scores(\"Alice\") = ${scores("Alice")}")
    println(s"  scores.get(\"Carol\") = ${scores.get("Carol")}  ← Option，找不到是 None")
    println(s"  加上 Carol = ${scores + ("Carol" -> 70)}")
    Teach.pitfall("scores(\"Carol\") 找不到会抛 NoSuchElementException。安全写法是 get 或 getOrElse。")

    Teach.section("7. 可变集合要显式 import")
    val buf = mutable.ArrayBuffer(1, 2)
    buf += 3
    buf ++= List(4, 5)
    println(s"  ArrayBuffer = $buf")
    val m = mutable.Map("a" -> 1)
    m("b") = 2
    println(s"  mutable.Map = $m")
    Teach.say("默认的 List/Set/Map 都不可变。可变的在 scala.collection.mutable。")

    Teach.summary(
      "元组适合临时打包；字段一多就用 case class",
      "默认 List/Set/Map 不可变，操作返回新集合",
      "Map 取值用 get；Array 要 mkString 再打印"
    )
    Teach.practice("用 Map 记下三门课成绩，算出平均分；再把其中一门用 + 更新成分数 100，看旧 Map 还在不在。")
  }
}
