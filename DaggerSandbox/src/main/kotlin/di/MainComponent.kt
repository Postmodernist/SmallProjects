package di

import classes.Bar
import classes.IFoo
import dagger.Component

@Component(modules = [FooModule::class])
interface MainComponent {

    fun strings(): Set<String>

    fun foos(): Set<IFoo>

    fun inject(bar: Bar)

}
