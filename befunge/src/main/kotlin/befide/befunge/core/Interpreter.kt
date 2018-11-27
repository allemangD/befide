package befide.befunge.core

import befide.befunge.events.Event
import java.io.PipedReader
import java.io.PipedWriter
import java.util.*

interface Pointer<V, M : Enum<M>> {
    val pos: V
    val delta: V
    val mode: M
}

interface MutablePointer<V, M : Enum<M>>
    : Pointer<V, M> {

    override var pos: V
    override var delta: V
    override var mode: M
}

interface Data {
    val char: Char?
}

data class FungeChange<V, D : Data>
(val funge: Funge<V, D>,
 val pos: V,
 val from: D,
 val to: D)

interface Funge<V, D : Data> {
    val size: V

    val data: List<List<D>>
    val src: String

    fun next(pos: V, delta: V): V

    operator fun get(pos: V): D

    val onChange: Event<FungeChange<V, D>>
}

interface MutableFunge<V, D : Data>
    : Funge<V, D> {

    override var data: List<List<D>>
    override var src: String

    operator fun set(pos: V, data: D)
}

data class IpChange<V, M : Enum<M>>
(val from: Pointer<V, M>, val to: Pointer<V, M>)

enum class StackOp { Push, Pop }

data class StackChange<D : Data>
(val op: StackOp, val data: D)

interface Interpreter<V, D : Data, M : Enum<M>> {
    val ip: Pointer<V, M>
    val funge: Funge<V, D>
    val stack: List<D>

    val onIpChange: Event<IpChange<V, M>>
    val onStackChange: Event<StackChange<D>>

    val stdin: PipedReader
    val stdout: PipedWriter

    fun step()
    fun reset()
}