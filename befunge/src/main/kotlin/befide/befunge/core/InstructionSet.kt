package befide.befunge.core

import befide.befunge.core.state.Data

interface InstructionSet<V, D : Data, M : Enum<M>> {
    fun MutableInterpreter<V, D, M>.handle()

    fun step(inter: MutableInterpreter<V, D, M>) = inter.handle()
}