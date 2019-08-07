package di

import classes.FooA
import classes.FooB
import classes.IFoo
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.IntoSet

@Module
class FooModule {

    @Provides
    @IntoSet
    fun provideOneString(): String {
        return "ABC"
    }

    @Provides
    @IntoSet
    fun provideAnotherString(): String {
        return "DEF"
    }

    @Provides
    @IntoSet
    fun provideFooA(): IFoo {
        return FooA()
    }

    @Provides
    @IntoSet
    fun provideFooB(): IFoo {
        return FooB()
    }
}