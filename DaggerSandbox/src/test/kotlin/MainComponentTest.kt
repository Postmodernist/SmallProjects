import classes.Bar
import classes.FooA
import classes.FooB
import com.google.common.truth.Truth.assertThat
import di.DaggerMainComponent
import di.MainComponent
import org.junit.Before
import org.junit.Test

class MainComponentTest {

    private lateinit var component: MainComponent

    @Before
    fun setup() {
        component = DaggerMainComponent.create()
    }

    @Test
    fun testComponent01() {
        assertThat(component.strings()).containsExactly("ABC", "DEF")
    }

    @Test
    fun testComponent02() {
        val bar = Bar().apply { component.inject(this) }
        assertThat(bar.strings).containsExactly("ABC", "DEF")
    }

    @Test
    fun testComponent03() {
        val bar = Bar().apply { component.inject(this) }
        assertThat(bar.foos).containsExactly(FooA(), FooB())
    }
}