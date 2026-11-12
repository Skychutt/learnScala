package data
import customer.*
import inventory.{Item, Rating, Order, OrderStatus}
import money.{Currency, Money}

import java.time.LocalDate
import scala.io.Source
import scala.util.{Try, Using}

object DataManager:
    private def loadFile[T](fileName: String)(parseLine: String => Either[String, T]): List[T] =
        val lines = Using(Source.fromFile(fileName))(_.getLines().toList).getOrElse(Nil)
        val results = lines.drop(1).filter(_.trim.nonEmpty).map(parseLine)
        val errors = results.collect { case Left(s) => s }
        if errors.nonEmpty then
            println(s"Encountered the following errors while processing data from $fileName:")
            errors.foreach(s => println("\t" + s))
        results.collect { case Right(c) => c }

    def loadCustomers(fileName: String): List[Customer] = loadFile(fileName)(parseCustomer)

    private def parseCustomer(line: String): Either[String, Customer] =
        val parts = line.split(",", -1).map(_.trim)
        if parts.length < 12 then Left(s"Customer line has too few values '$line'")
        else
            val phoneParts = parts(4).split(" ")
            val phone = if phoneParts.length == 3 then PhoneNumber(Some(phoneParts(0)), phoneParts(1), phoneParts(2))
            else if phoneParts.length == 2 then PhoneNumber(None, phoneParts(0), phoneParts(1))
            else Left(s"Unable to read phone number '${parts(4)}'")
            for
                dob <- Try(LocalDate.parse(parts(2))).toEither.left.map(_ => s"Invalid date of birth '${parts(2)}'")
                currency <- Try(Currency.valueOf(parts(5))).toEither.left.map(_ => s"Unknown currency '${parts(5)}'")
                p <- phone
                a <- Address(parts(6), parts(7), parts(8), parts(9), parts(10), parts(11))
            yield Customer(parts(0), parts(1), dob, parts(3), p, currency, a)

    def loadItems(fileName: String): Map[String, Item] =
        loadFile(fileName)(parseItem).map(i => i.stockID -> i).toMap

    private def parseItem(line: String): Either[String, Item] =
        val parts = line.split(",", -1).map(_.trim)
        if parts.length < 7 then Left(s"Item line has too few values '$line'")
        else
            for
                r <- Try(Rating.valueOf(parts(3))).toEither.left.map(_ =>
                    s"Unknown rating '${parts(3)}' for item ${parts(0)}"
                )
                c <- Try(Currency.valueOf(parts(4))).toEither.left.map(_ =>
                    s"Unknown currency '${parts(4)}' for item ${parts(0)}"
                )
                m <- Try(Money(c, parts(5).toInt, parts(6).toInt)).toEither.left.map(_ =>
                    s"Invalid price for item ${parts(0)}"
                )
            yield Item(parts(0), parts(1), parts(2), m, r)

    def loadStock(fileName: String, items: Map[String, Item]): Map[Item, Int] =
        loadFile(fileName)(line => parseStock(line, items)).toMap

    private def parseStock(line: String, items: Map[String, Item]): Either[String, (Item, Int)] =
        val parts = line.split(",", -1).map(_.trim)
        (items.get(parts(0)), Try(parts(1).toInt).toOption) match
            case (Some(i), Some(q)) => Right((i, q))
            case (None, _)          => Left(s"Unknown item '${parts(0)}' in stock line '$line'")
            case (_, None)          => Left(s"Unable to read the quantity in stock line '$line'")

    def loadOrders(fileName: String, items: Map[String, Item], customers: Map[String, Customer]): List[Order] =
        loadFile(fileName)(line => parseOrder(line, items, customers))

    private def parseOrder(
        line: String,
        items: Map[String, Item],
        customers: Map[String, Customer]
    ): Either[String, Order] =
        val parts = line.split(",", -1).map(_.trim)
        for
            customer <- customers.get(parts(1)).toRight(s"Order ${parts(0)}: unknown customer '${parts(1)}'")
            status <- Try(OrderStatus.valueOf(parts(2))).toEither.left.map(_ =>
                s"Order ${parts(0)}: unknown status '${parts(2)}'"
            )
            count <-
                Try(parts(3).toInt).toEither.left.map(_ => s"Order ${parts(0)}: invalid number of items '${parts(3)}'")
            pairs = parts.slice(4, 4 + count * 2).grouped(2).toList
            found = pairs.map(p => (p(0), items.get(p(0)), Try(p(1).toInt).toOption))
            _ <- found.collectFirst { case (id, None, _) => s"Order ${parts(0)}: unknown item '$id'" }.toLeft(())
            _ <- found.collectFirst { case (id, _, None) =>
                s"Order ${parts(0)}: invalid quantity for item '$id'"
            }.toLeft(())
        yield Order(parts(0), customer, status, found.collect { case (_, Some(i), Some(q)) => (i, q) })
