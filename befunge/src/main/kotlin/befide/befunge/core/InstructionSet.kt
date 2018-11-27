package befide.befunge.core

import befide.befunge.core.state.Data

interface InstructionSet<V, D : Data, M : Enum<M>> {
    /**
     * @return whether this instruction set handled the instruction. If true, the step will be completed
     */
    fun MutableInterpreter<V, D, M>.handle(): Boolean

    fun step(inter: MutableInterpreter<V, D, M>): Boolean = inter.handle()

    operator fun plus(o: InstructionSet<V, D, M>): MultiInstructionSet<V, D, M> =
            MultiInstructionSet(this, o)

    operator fun plus(o: MultiInstructionSet<V, D, M>): MultiInstructionSet<V, D, M> =
            MultiInstructionSet(listOf(this) + o.sets)
}

class MultiInstructionSet<V, D : Data, M : Enum<M>>
(vararg val sets: InstructionSet<V, D, M>)
    : InstructionSet<V, D, M> {

    constructor(sets: List<InstructionSet<V, D, M>>) : this(*sets.toTypedArray())

    override fun MutableInterpreter<V, D, M>.handle(): Boolean {
        for (set in sets) if (set.step(this)) return true
        return false
    }

    override operator fun plus(o: InstructionSet<V, D, M>): MultiInstructionSet<V, D, M> =
            MultiInstructionSet(sets.toList() + listOf(o))
}