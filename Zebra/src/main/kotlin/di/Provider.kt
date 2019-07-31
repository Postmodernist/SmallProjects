package di

import core.*
import interfaces.*

class Provider {

    private val contradictor: Contradictor = ContradictorImpl()
    private val cook: Cook = CookImpl()
    private val copier: Copier = CopierImpl()
    private val fitter: Fitter = FitterImpl()
    private val horadricCube: HoradricCube = HoradricCubeImpl()
    private val matcher: Matcher = MatcherImpl()
    private val merger: Merger = MergerImpl()
    private val reducer: Reducer = ReducerImpl()
    private val relaxer: Relaxer = RelaxerImpl()
    private val simplifier: Simplifier = SimplifierImpl()

    init {
        (contradictor as ContradictorImpl).inject(merger, relaxer)
        (horadricCube as HoradricCubeImpl).inject(contradictor, copier, matcher, relaxer)
        (reducer as ReducerImpl).inject(copier, fitter, merger, simplifier)
        (relaxer as RelaxerImpl).inject(matcher)
        (simplifier as SimplifierImpl).inject(horadricCube, matcher, merger)
    }

    fun provideCook(): Cook = cook

    fun provideSimplifier(): Simplifier = simplifier

    fun provideMatcher(): Matcher = matcher

    fun provideMerger(): Merger = merger

    fun provideReducer(): Reducer = reducer
}
