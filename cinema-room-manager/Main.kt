package cinema

fun main() {
    // Кинозал
    var hall = mutableListOf(
        mutableListOf<Char>(' ', '1', '2', '3', '4', '5', '6', '7', '8', '9'),
        mutableListOf<Char>('1', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
        mutableListOf<Char>('2', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
        mutableListOf<Char>('3', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
        mutableListOf<Char>('4', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
        mutableListOf<Char>('5', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
        mutableListOf<Char>('6', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
        mutableListOf<Char>('7', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
        mutableListOf<Char>('8', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
        mutableListOf<Char>('9', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'S'),
    )

    println("Enter the number of rows:")
    val numRows: Int = readln().toInt() // Количество рядов в зале
    println("Enter the number of seats in each row:")
    val numSeats: Int = readln().toInt() // Количество мест в зале

    val totalIncome = getTotalIncome(numRows, numSeats) // Максимальная выручка с зала
    val allCountTickets = allCountsTicket(numRows, numSeats) // Общее количество билетов
    var enter = -1 // Выбор пункта в меню
    var row = 0 // пользовательский ряд в кинозале
    var seat = 0 // пользовательское место в кинозале
    var countTickets = 0 // количество купленных билетов
    var revenue = 0 // выручка с продажи билетов

    while(enter != 0) { // пока пользователь не введёт 0
        println(
            """
                    1. Show the seats
                    2. Buy a ticket
                    3. Statistics
                    0. Exit
            """.trimIndent()
        )
        try {
            enter = readln().toInt() // выбор в пункте меню
        } catch (e: Exception) {
            println(e.message)
        }
        if (enter == 1) { // показать наличие мест в зале
            println(changeDisplay(hall, numRows, numSeats, row, seat))
            continue
        } else if (enter == 2) {
            //countTickets++
        } else if (enter == 3) { // показать статистику кинозала
            println(
                "Number of purchased tickets: $countTickets\n" +
                        "Percentage: ${String.format("%.2f", percentTickets(allCountTickets, countTickets))}%\n" +
                        "Current income: \$${revenue}\n" +
                        "Total income: \$${totalIncome}"
            )
            println()
            continue
        } else if (enter == 0) break
        while(true) {
            try {
                println("Enter a row number:")
                row = readln().toInt()
                println("Enter a seat number in that row:")
                seat = readln().toInt()
                if((row == 0) || (seat == 0) || (row > numRows) || (seat > numSeats)) {
                    println("Wrong input!")
                    continue
                }
                if (!checkPlace(hall, numRows, numSeats, row, seat)){
                    println("That ticket has already been purchased!")
                    continue
                }
                else break
            } catch (e: Exception) {
                println("Wrong input!")
            }
        }
        println("Ticket price: \$${buyTicket(numRows, numSeats, row)}")
        countTickets++
        revenue += buyTicket(numRows, numSeats, row)
        println()
        hall = changeDisplay(hall, numRows, numSeats, row, seat)
    }
}
fun checkPlace(list: MutableList<MutableList<Char>>, rows: Int, seats: Int, placeRow: Int, placeSeat: Int): Boolean{
    for(i in 0..rows){
        for(j in 0..seats){
            if(list[placeRow][placeSeat] == 'B'){
                return false
            }
        }
    }
    return true
}
// Получаем количество купленных билетов в процентах
fun percentTickets(all_tickets: Int, all_buy_tickets: Int): Double = (all_buy_tickets * 100) / (all_tickets).toDouble()
// Получаем количество возможных купленных билетов
fun allCountsTicket(rows: Int, seats: Int) = rows * seats

// getTotalIncome Общий доход, показывающий, сколько денег получит театр, если все билеты будут проданы.
fun getTotalIncome(countRows: Int, countSeats: Int): Int{
    val totalIncome: Int = if (countRows * countSeats <= 60) {
        countRows * countSeats * 10
    } else {
        if (countRows % 2 != 0) countRows / 2 * 10 * countSeats + ((countRows - (countRows / 2)) * 8 * countSeats)
        else countRows / 2 * 10 * countSeats + countRows / 2 * 8 * countSeats
    }
    return totalIncome
}

// buyTicket - выводит цену билета, в зависимости от количества мест в кинозале и переднего/заднего рядов
// если мест больше 60, то цена за передней ряд(всегда меньше на 1) стоит 10$
// цена заднего 8$
fun buyTicket(countRow: Int, countSeat: Int, row: Int): Int{
    val lastRow = countRow - countRow / 2;
    val ticketPrice = if((countRow * countSeat) < 60) 10
    else {
        if(row < lastRow) 10 else 8
    }
    return ticketPrice
    //println("Ticket price: $$ticketPrice")
}

// ChangeDisplay - Изменяет и выводит таблицу купленных билетов в кинозале
// Двумерный list <char>- Таблица зала
// Возвращает лист с изменной расстановкой мест
// rows - количество рядов (строк листа)
// seats - кол-во сидений (ячеек листа)
// placeRow - ряд, выбранный игроком
// placeSeat - место, выбранное игроком
fun changeDisplay(list: MutableList<MutableList<Char>>, rows: Int, seats: Int, placeRow: Int, placeSeat: Int): MutableList<MutableList<Char>> {
    for(i in 0..rows){
        for(j in 0..seats) {
            list[placeRow][placeSeat] = 'B'
        }
    }
    list[0][0] = ' '
    println("Cinema: ")
    for(i in 0..rows){
        for(j in 0..seats){
            print("${list[i][j]} ")
        }
        println()
    }
    return list
}