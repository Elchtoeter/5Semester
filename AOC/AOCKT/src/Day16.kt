import java.io.File
import java.lang.Math.abs
import java.text.CharacterIterator

fun main() {
    val input = File("day16.txt").readLines().first().map{Character.digit(it,10)}
    //println(input)
    input.forEach { println(it) }

    var value = input
    repeat(100){
        value = value.indices.map { i ->
            var sum = 0
            for ((j , v) in value.withIndex()) {
                when ((j + 1) % (4 * i + 4) / (i + 1)) {
                    1 -> sum += v
                    3 -> sum -= v
                }
            }
            kotlin.math.abs(sum) %10
        }
    }

    println(value.take(8).joinToString(""))
}