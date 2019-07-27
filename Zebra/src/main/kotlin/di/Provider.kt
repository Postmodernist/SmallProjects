package di

import core.*
import interfaces.*

class Provider {

    private val matcher: Matcher = MatcherImpl()
    private val merger: Merger = MergerImpl()
    private val cook: Cook = CookImpl(matcher, merger)
    private val horadricCube: HoradricCube = HoradricCubeImpl(matcher, merger)
    private val simplifier: Simplifier = SimplifierImpl(horadricCube, merger)

    fun provideCook(): Cook = cook

    fun provideSimplifier(): Simplifier = simplifier

    fun provideMatcher(): Matcher = matcher

    fun provideMerger(): Merger = merger
}
