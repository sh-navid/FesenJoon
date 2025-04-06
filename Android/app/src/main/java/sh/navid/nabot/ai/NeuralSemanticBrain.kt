@file:Suppress("MemberVisibilityCanBePrivate")

package sh.navid.nabot.ai

import kotlin.random.Random

class Neuron<T>(val id: Int, val value: T, val type: RelationType) {
    var x: Float = Random.nextFloat() // FIXME: move this to view model
    var y: Float = Random.nextFloat() // FIXME: move this to view model

    override fun toString(): String {
        return "$id ($value) [$x,$y]"
    }
}

enum class RelationType {
    Node,
    Type,
    Command
}

class Relation<T>(
    val a: Int,
    val b: Int,
    var w: Int = 1,
    val type: RelationType = RelationType.Node
) {
    override fun toString(): String {
        return "$a = $w => $b"
    }
}

class LiveNet<T : Any> {
    var nodes = ArrayList<Neuron<T>>()
    var network = ArrayList<Relation<T>>()
    private var count = 1

    fun getTypes(): List<String> {
        return nodes.filter { it.type == RelationType.Type }.map { it.value } as List<String>
    }

    fun addNode(value: T, type: RelationType = RelationType.Node): Neuron<T> {
        // Check if a node with the same value already exists
        val existingNode = nodes.find { it.value == value }
        return if (existingNode == null) {
            // If not, create a new node, add it to the list, and return it
            count += 1
            val newNode = Neuron(count, value, type)
            nodes.add(newNode)
            newNode
        } else {
            // If it exists, return the existing node
            existingNode
        }
    }

    fun addRelation(relation: Relation<T>): Relation<T> {
        val existingNode = network.find { it.a == relation.a && it.b == relation.b }
        return if (existingNode == null) {
            network.add(relation)
            relation
        } else {
            existingNode.w += 1
            existingNode
        }
    }

    fun addRelation(idA: Int, idB: Int, type: RelationType = RelationType.Node) {
        addRelation(Relation(idA, idB, w = 1, type))
    }

    fun addRelation(nodeA: Neuron<T>, nodeB: Neuron<T>, type: RelationType = RelationType.Node) {
        addRelation(nodeA.id, nodeB.id, type)
    }

    // Enhanced this function thanks to perplexity
    fun identify(text: String): Data {
        val d = Data(Data.clean(text))
        val tokens = d.tokenize()
        val counts = mutableMapOf<String, Int>()

        // Check direct associations
        for (t in tokens) {
            nodes.firstOrNull { it.value == t }?.let { n1 ->
                network.filter { it.a == n1.id && it.type == RelationType.Type }.forEach { r ->
                    nodes.filter { it.id == r.b }.forEach { n2 ->
                        counts[n2.value.toString()] =
                            counts.getOrDefault(n2.value.toString(), 0) + 1
                    }
                }
            }
        }

        // Check indirect associations (relations between words)
        for (i in tokens.indices) {
            nodes.firstOrNull { it.value == tokens[i] }?.let { n1 ->
                for (j in i + 1 until tokens.size) {
                    nodes.firstOrNull { it.value == tokens[j] }?.let { n2 ->
                        network.filter { it.a == n1.id && it.b == n2.id && it.type == RelationType.Node }.forEach { _ ->
                            // If there's a relation between two words, check their connections to types
                            network.filter { it.a == n2.id && it.type == RelationType.Type }.forEach { r ->
                                nodes.filter { it.id == r.b }.forEach { n3 ->
                                    counts[n3.value.toString()] =
                                        counts.getOrDefault(n3.value.toString(), 0) + 1
                                }
                            }
                        }
                    }
                }
            }
        }

        val maxEntry = counts.maxByOrNull { it.value }
        d.type = maxEntry?.key?.let { Integer.parseInt(it) } ?: -1

        return d
    }

}

class Brain {
    private var liveNet = LiveNet<String>()

    fun get(): LiveNet<String> {
        return liveNet
    }

    init {
        for (x in Data.clean(dataSet)) {
            val tokens = x.tokenize()

            val typeNode = liveNet.addNode(x.type.toString(), RelationType.Type)

            var a: Neuron<String>? = null

            for (t in tokens) {
                val node = liveNet.addNode(t)
                liveNet.addRelation(node, typeNode, RelationType.Type)

                if (a != null) {
                    liveNet.addRelation(a, node, RelationType.Node)
                }

                a = node
            }
        }
    }

    fun identify(text: String): Data {
        return liveNet.identify(text)
    }
}