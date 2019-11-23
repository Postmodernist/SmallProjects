fun main() {
    listOf(
        "Monitor-4.1.8",
        "Monitor-3.0.0",
        "Monitor-4.1.85",
        "Monitor-4.1.8-dev-9-g316cd31",
        "Monitor-4.1.10",
        "Monitor-3.02.77",
        "Monitor-4.1.8.1",
        "Monitor-3.0.100",
        "Monitor-3.2.0",
        "Monitor-3.0.77",
        "Monitor-4.1.8-dev",
        "Monitor-4.1.8-dev-80-g316cd31",
        "Monitor-4.1.8-dev-100-g316cd31",
        "Monitor-3.0002",
        "Monitor-4.1.8-rc",
        "Monitor-4.8.100",
        "Monitor-4.10.1",
        "Monitor-3.0.99.99",
        "Monitor-3.002",
        "Monitor-4.8.9999999999999",
        "Monitor-8.11111111.9999999999999",
        "Monitor-9"
    )
        .sortedWith(SemanticStringComparator())
        .forEach { println(it) }
}