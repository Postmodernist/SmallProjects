class Node:

    def __init__(self, _id: int):
        self.id = _id
        self.edges = []


class Edge:

    def __init__(self, out_node: Node):
        self.out_node = out_node


def prune_tree(node: Node):
    """ Keep only subtree of the node and prune the rest """

    def copy_subtree(node_: Node):
        for edge in node_.edges:
            subtree[edge.out_node.id] = edge.out_node
            copy_subtree(edge.out_node)

    subtree = {node.id: node}
    copy_subtree(node)
    return subtree


def print_tree(node: Node, indent: int = 2):
    print('{}. {}'.format(' ' * (indent - 2), node.id))
    for edge in node.edges:
        print_tree(edge.out_node, indent + 2)


# Create tree
tree = {}
for i in range(9):
    tree[i] = Node(i)
tree[0].edges.append(Edge(tree[1]))
tree[0].edges.append(Edge(tree[2]))
tree[0].edges.append(Edge(tree[3]))
tree[2].edges.append(Edge(tree[4]))
tree[2].edges.append(Edge(tree[5]))
tree[3].edges.append(Edge(tree[6]))
tree[6].edges.append(Edge(tree[7]))
tree[6].edges.append(Edge(tree[8]))

print_tree(tree[0])
tree = prune_tree(tree[3])
print(tree.keys())
