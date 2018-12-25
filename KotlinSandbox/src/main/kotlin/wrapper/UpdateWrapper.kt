package wrapper

enum class Upgradable {
    KEY_1, KEY_2;

    var upgrade = false
}

fun <R> fun0Wrap(key: Upgradable, f1: () -> R, f2: () -> Pair<Boolean, R>): R =
        if (!key.upgrade) f1() else f2().let { key.upgrade = it.first; it.second }
