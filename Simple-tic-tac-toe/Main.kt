/*
    Благодарен с решением проблемы связанной с IDE:
    Ильнуру Галимову (Инженер технической поддержки JetBrains )
*/
import kotlin.math.abs

// символы для полей X и O
const val X = 'X'
const val O = 'O'

fun main(){
    var field = "         " // пустое игровое поле // 9 пробелов
    printField(field)
    var numberXod = 0
    while(!checkWin(field))
    {
        if (numberXod == 9){ // ничья
            println("Draw")
            break
        } // если все ходы были сделаны
        val playerXod = withoutSpaceXod()
        val statusEmpty = cellEmpty(field,playerXod) // Возвращает true, если строка-координата соответствует символам ' ' или '_'
        val statusRange = checkRange(playerXod) // true - если символы входят в диапазон 1..3
        val statusEnter = checkEnter(playerXod) // true - если есть символы числа

        if (!statusEnter) { // Если нет символов-цифр
            println("You should enter numbers!")
            numberXod--
            //statusEnter = checkEnter(xod)
        } else if(!statusRange) { // Если ход пользователя выходит за диапазон чисел 1..3
            println("Coordinates should be from 1 to 3!")
            numberXod--
            //statusEmpty = cellEmpty(str,xod)
        } else if (!statusEmpty){ // если ячейка не пуста
            println("This cell is occupied! Choose another one!")
            numberXod--
            //statusEmpty = cellEmpty(str,xod)
        } else {
            field = changeField(field,playerXod,numberXod)
            printField(field)
        }
        numberXod++
    }

}

// checkWin - проверяет поле на комбинацию XXX или OOO
// проверяет на
fun checkWin(str: String):Boolean {

    var xFlag = 0
    var oFlag = 0

    // X выигрывает, когда в сетке есть три X подряд (включая диагонали).
    if (str[0] == X && str[1] == X && str[2] == X) xFlag = 1
    else if (str[3] == X && str[4] == X && str[5] == X) xFlag = 1
    else if (str[6] == X && str[7] == X && str[8] == X) xFlag = 1

    else if (str[0] == X && str[3] == X && str[6] == X) xFlag = 1
    else if (str[1] == X && str[4] == X && str[7] == X) xFlag = 1
    else if (str[2] == X && str[5] == X && str[8] == X) xFlag = 1

    else if (str[0] == X && str[4] == X && str[8] == X) xFlag = 1
    else if (str[2] == X && str[4] == X && str[6] == X) xFlag = 1

    // O выигрывает, когда в сетке есть три O подряд (включая диагонали).
    if(str[0] == O && str[1] == O && str[2] == O) oFlag = 1
    else if (str[3] == O && str[4] == O && str[5] == O) oFlag = 1
    else if (str[6] == O && str[7] == O && str[8] == O) oFlag = 1

    else if (str[0] == O && str[3] == O && str[6] == O) oFlag = 1
    else if (str[1] == O && str[4] == O && str[7] == O) oFlag = 1
    else if (str[2] == O && str[5] == O && str[8] == O) oFlag = 1

    else if (str[0] == O && str[4] == O && str[8] == O) oFlag = 1
    else if (str[2] == O && str[4] == O && str[6] == O) oFlag = 1


    if(xFlag == 1) { // X выигрывает, когда в сетке есть три X подряд (включая диагонали).
        println("X wins")
    } else if (oFlag == 1) { // O выигрывает, когда в сетке есть три O подряд (включая диагонали).
        println("O wins")
    }
    return ((xFlag == 1) || (oFlag == 1))
}
// printField - выводит 9 элементов строки в формате 3x3
fun printField(str: String){
    val space = ' '
    println("---------")
    println("|${space}${str[0]}${space}${str[1]}${space}${str[2]}${space}|")
    println("|${space}${str[3]}${space}${str[4]}${space}${str[5]}${space}|")
    println("|${space}${str[6]}${space}${str[7]}${space}${str[8]}${space}|")
    println("---------")
}

// withoutSpaceXod- возвращает ход игрока с пробелом без пробела
fun withoutSpaceXod(): String {
    var str = readln()
    str = str.replace("\\s".toRegex(), "")
    return str
}

fun changeField(oldField: String, cell: String, numberXod: Int):String {
    var newField = ""
    var playerXod = 0
    val symbolXod = if (numberXod % 2 == 0) 'X' else 'O'
    when(cell){
        "11" -> playerXod = 0
        "12" -> playerXod = 1
        "13" -> playerXod = 2
        "21" -> playerXod = 3
        "22" -> playerXod = 4
        "23" -> playerXod = 5
        "31" -> playerXod = 6
        "32" -> playerXod = 7
        "33" -> playerXod = 8
    }
    for(i in oldField.indices){
        newField += when(i){
            playerXod -> symbolXod
            else -> oldField[i]
        }
    }

    //println(newField)
    return newField
}

// checkEnter - проверяет, были ли введены символы
// xod: String - строка, которую проверяем
// Возвращает true - если введены символы "цифры"
fun checkEnter(xod: String): Boolean{
    for (i in xod){
        // if ((i != 'X' || i != 'O' || i != '_' || i != ' ')) return false //&& (i in '0'..'9')) return false
        if (!i.isDigit()) return false
    }
    return true
}

// checkRange - проверяет, входит ли строка-координата в диапазон 1..3
// xod: String - строка, с которой сравниваем с нужными значениями поля-координат
// Возвращает true - если координаты входят в диапазон 1..3
fun checkRange(xod: String): Boolean{
    return (xod == "11" || xod == "12" || xod == "13"
            || xod == "21" || xod == "22" || xod == "23"
            || xod == "31" || xod == "32" || xod == "33")
}

// cellEmpty - узнаёт, какие ячейки в строке равны символам ' ' или '_'
// str: String - строка, в которой ищем ячейку с символом ' ' или '_'
// xod: String - строка-координата
// Возвращает true, если строка-координата соответствует символам ' ' или '_', иначе else
fun cellEmpty(str: String, xod: String): Boolean{
    if (xod == "11" && (str[0] == ' ' || str[0] == '_')){
        return true
    } else if (xod == "12" && (str[1] == ' ' || str[1] == '_')){
        return true
    } else if (xod == "13" && (str[2] == ' ' || str[2] == '_')){
        return true
    } else if (xod == "21" && (str[3] == ' ' || str[3] == '_')){
        return true
    } else if (xod == "22" && (str[4] == ' ' || str[4] == '_')){
        return true
    } else if (xod == "23" && (str[5] == ' ' || str[5] == '_')){
        return true
    } else if (xod == "31" && (str[6] == ' ' || str[6] == '_')){
        return true
    } else if (xod == "32" && (str[7] == ' ' || str[7] == '_')){
        return true
    } else if (xod == "33" && (str[8] == ' ' || str[8] == '_')){
        return true
    }
    return false
}