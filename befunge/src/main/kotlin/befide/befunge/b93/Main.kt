package befide.befunge.b93

import befide.befunge.core.util.readAll
import java.io.PipedReader
import java.io.PipedWriter
import kotlin.concurrent.thread
import kotlin.concurrent.timer


fun runSync() {
    val int = Interpreter93(System.`in`.reader(), System.out.writer())

    // interactive calculator
    int.funge.src = "\" :b ]%/*-+[ a\">:#,_&~~\$a3*0p& .@"

    int.run {}

    println()
}

fun runThreaded() {
    val stdout = PipedReader()
    val stdin = PipedWriter()

    val int = Interpreter93(PipedReader(stdin), PipedWriter(stdout))

    // incrementer
    int.funge.src = ":.&+"

    val input = timer("input", period = 50L) {
        generateSequence {
            System.`in`.read().takeUnless { it == -1 }
        }.forEach(stdin::write)
    }

    val output = timer("output", period = 500L) {
        print(stdout.readAll())
    }

    val stepper = thread {
        int.run { Thread.sleep(50L) }
    }

    // input, output, and computation happen concurrently at this point.

    stepper.join()

    input.cancel()
    output.cancel()

    println(stdout.readAll())

    int.stdout?.close()
    stdout.close()
    int.stdin?.close()
    stdin.close()
}

fun main(args: Array<String>) {
    println("synchronous:")
    runSync()

    println()

    println("threaded:")
    runThreaded()
}