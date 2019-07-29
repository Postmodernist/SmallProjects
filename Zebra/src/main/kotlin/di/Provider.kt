package di

import core.*
import interfaces.*

class Provider {

    private val contradictor: Contradictor = ContradictorImpl()
    private val cook: Cook = CookImpl()
    private val copier: Copier = CopierImpl()
    private val horadricCube: HoradricCube = HoradricCubeImpl()
    private val matcher: Matcher = MatcherImpl()
    private val merger: Merger = MergerImpl()
    private val reducer: Reducer = ReducerImpl()
    private val relaxer: Relaxer = RelaxerImpl()
    private val simplifier: Simplifier = SimplifierImpl()

    init {
        (contradictor as ContradictorImpl).inject(merger, relaxer)
        (cook as CookImpl).inject(matcher, merger)
        (horadricCube as HoradricCubeImpl).inject(matcher, relaxer, contradictor, copier)
        (reducer as ReducerImpl).inject(matcher, merger, copier)
        (relaxer as RelaxerImpl).inject(matcher)
        (simplifier as SimplifierImpl).inject(horadricCube, merger, contradictor)
    }

    fun provideCook(): Cook = cook

    fun provideSimplifier(): Simplifier = simplifier

    fun provideMatcher(): Matcher = matcher

    fun provideMerger(): Merger = merger

    fun provideReducer(): Reducer = reducer
}
