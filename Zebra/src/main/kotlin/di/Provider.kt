package di

import core.*
import interfaces.*

class Provider {

    private val contradictor: Contradictor = ContradictorImpl()
    private val cook: Cook = CookImpl()
    private val horadricCube: HoradricCube = HoradricCubeImpl()
    private val matcher: Matcher = MatcherImpl()
    private val merger: Merger = MergerImpl()
    private val relaxer: Relaxer = RelaxerImpl()
    private val simplifier: Simplifier = SimplifierImpl()

    init {
        (contradictor as ContradictorImpl).inject(merger, relaxer)
        (cook as CookImpl).inject(matcher, merger)
        (horadricCube as HoradricCubeImpl).inject(matcher, relaxer, contradictor)
        (simplifier as SimplifierImpl).inject(horadricCube, merger)
        (relaxer as RelaxerImpl).inject(matcher)
    }

    fun provideCook(): Cook = cook

    fun provideSimplifier(): Simplifier = simplifier

    fun provideMatcher(): Matcher = matcher

    fun provideMerger(): Merger = merger
}
