package di

import core.*
import interfaces.*

class Provider {

    private val matcher: Matcher = MatcherImpl()
    private val merger: Merger = MergerImpl()
    private val cook: Cook = CookImpl(matcher, merger)
    private val horadricCube: HoradricCube = HoradricCubeImpl(matcher, merger)
    private val simplifier: Simplifier = SimplifierImpl(cook, horadricCube, merger)

    fun provideSimplifier(): Simplifier = simplifier
}