import model.Entry.RuleSet
import model.Entry.Value
import model.Relation
import model.Rule

fun <T : Enum<T>> value(v: T) = Value(v.ordinal)

fun value(v: Int) = Value(v)

fun rule(relation: Relation, id: Int): RuleSet =
    RuleSet(hashSetOf(Rule(relation, id)))
