import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.fix
import arrow.data.ListK
import arrow.data.fix
import arrow.instances.either.applicative.applicative
import arrow.instances.listk.applicative.applicative
import arrow.instances.option.applicative.applicative
import arrow.typeclasses.Applicative
import kotlin.random.Random

fun <F> Applicative<F>.randomUser(f: (Int) -> User): Kind<F, User> = just(f(Random.nextInt(1000)))

class UserFetcher<F>(AP: Applicative<F>) : Applicative<F> by AP {
    fun genUser() = randomUser(::User)
}

fun main(args: Array<String>) {
    val list = ListK.applicative().randomUser(::User).fix()  // [User(342)]
    println(list)

    val option = Option.applicative().randomUser(::User).fix()  // Some(User(765))
    println(option)

    val either = Either.applicative<Unit>().randomUser(::User).fix()  // Right(User(221))
    println(either)

    val list2 = with(ListK.applicative()) {
        // Lots of code

        // Multiple calls

        randomUser(::User)
    }.fix()
    println(list2)

    val option2 = Option.applicative().run {
        tupled(randomUser(::User), randomUser(::User))
    }.fix()
    println(option2)

    val option3 = UserFetcher(Option.applicative()).genUser().fix()
    println(option3)
}
