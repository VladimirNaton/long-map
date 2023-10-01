package de.comparus.opensource.longmap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V> {

    private static Integer START_CAPACITY = 16;
    private static Integer NOT_FREE_INDEXES = 0;

    private Long size = 0L;

    private Node<Long, V>[] nodes;

    public LongMapImpl() {
        nodes = new Node[START_CAPACITY];
    }


    @Getter
    @Setter
    @EqualsAndHashCode
    private static class Node<Long, V> {
        private Node<Long, V> next;
        private Long key;
        private V value;
    }

    @Override
    public V put(long key, V value) {
        if (value != null) {
            Node<Long, V> node = getValueForKey(key, nodes);
            if (node == null) {
                nodes[getIndex(key)] = setNode(key, value);
                NOT_FREE_INDEXES++;
                resize();
                size++;
            } else {
                Node<Long, V> nodeForSave = setNode(key, value);
                if (compareNode(node, nodeForSave) || node.getKey().equals(nodeForSave.getKey())) {
                    V oldValue = node.getValue();
                    node.setValue(value);
                    return oldValue;
                } else {
                    node.setNext(setNode(key, value));
                    size++;
                    resize();
                }
            }
        }
        return value;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        START_CAPACITY = 16;
        size = 0L;
        nodes = new Node[START_CAPACITY];
        NOT_FREE_INDEXES = 0;

    }

    @Override
    public V remove(long key) {
        Node<Long, V> node = nodes[getIndex(key)];
        if (node != null) {
            if (Objects.equals(node.getKey(), key)) {
                if (node.getNext() != null) {
                    Node<Long, V> nodeNext = node.getNext();
                    nodes[getIndex(key)] = nodeNext;
                } else {
                    nodes[getIndex(key)] = null;
                    NOT_FREE_INDEXES--;
                }
                size--;
                return node.getValue();
            } else {
                Node<Long, V> nodeNext;
                while (true) {
                    if (node.getNext() != null) {
                        nodeNext = node.getNext();
                        if (Objects.equals(nodeNext.getKey(), key)) {
                            if (nodeNext.getNext() == null) {
                                node.setNext(null);
                            } else {
                                node.setNext(nodeNext.getNext());
                            }
                            size--;
                            return nodeNext.getValue();
                        } else {
                            node = nodeNext;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public V get(long key) {
        Node<Long, V> node = getValueForKey(key, nodes);
        if (node != null) {
            return node.getValue();
        }
        return null;
    }

    @Override
    public boolean containsKey(long key) {
        Node<Long, V> node = getValueForKey(key, nodes);
        return node != null;
    }

    @Override
    public long[] keys() {
        long[] keys = new long[Math.toIntExact(size)];
        int index = 0;
        for (Node<Long, V> node : nodes) {
            if (node != null) {
                keys[index] = node.getKey();
                index++;
                if (node.getNext() != null) {
                    Node<Long, V> nodeNext = node.getNext();
                    while (true) {
                        keys[index] = nodeNext.getKey();
                        index++;
                        if (nodeNext.getNext() == null) {
                            break;
                        } else {
                            nodeNext = nodeNext.getNext();
                        }
                    }
                }
            }
        }
        return Arrays.stream(keys).sorted().toArray();
    }

    @Override
    public V[] values() {
        int index = 0;
        V[] v = null;

        for (Node<Long, V> node : nodes) {
            if (node != null) {
                v = (V[]) Array.newInstance(node.getValue().getClass(), Math.toIntExact(size));
                break;
            }
        }

        for (Node<Long, V> node : nodes) {
            if (node != null) {
                if (v != null) {
                    v[index] = node.getValue();
                    index++;
                    if (node.getNext() != null) {
                        Node<Long, V> nodeNext = node.getNext();
                        while (true) {
                            v[index] = nodeNext.getValue();
                            index++;
                            if (nodeNext.getNext() == null) {
                                break;
                            } else {
                                nodeNext = nodeNext.getNext();
                            }
                        }
                    }
                }
            }
        }
        return v;
    }

    @Override
    public boolean containsValue(V value) {
        for (Node<Long, V> node : nodes) {
            if (node != null) {
                if (node.getValue().equals(value)) {
                    return true;
                }
                if (node.getNext() != null) {
                    Node<Long, V> nodeNext = node.getNext();
                    while (true) {
                        if (nodeNext.getValue().equals(value)) {
                            return true;
                        }
                        if (nodeNext.getNext() == null) {
                            break;
                        } else {
                            nodeNext = nodeNext.getNext();
                        }
                    }
                }
            }
        }
        return false;
    }


    private Node<Long, V> getValueForKey(long key, Node<Long, V>[] nodes) {
        Node<Long, V> node = nodes[getIndex(key)];
        if (node != null) {
            if (node.getKey() == key) {
                return node;
            }
        } else {
            return null;
        }

        if (node.getNext() == null) {
            return node;
        }

        while (true) {
            Node<Long, V> nextNode = node.getNext();
            if (nextNode == null) {
                return node;
            }
            if (nextNode.getKey() == key) {
                return nextNode;
            }
            if (nextNode.getNext() != null) {
                node = node.getNext();
            } else {
                return nextNode;
            }
        }
    }


    private int getIndex(long key) {
        return (int) (key % START_CAPACITY);
    }


    private boolean compareNode(Node<Long, V> nodeOne, Node<Long, V> nodeTwo) {
        return Objects.equals(nodeOne, nodeTwo);
    }


    private void resize() {
        if (NOT_FREE_INDEXES >= Math.ceil((double) (START_CAPACITY * 75) / 100)) {
            START_CAPACITY *= 2;
            Node<Long, V>[] resize = new Node[START_CAPACITY];
            for (Node<Long, V> nd : nodes) {
                if (nd != null) {
                    saveResize(nd, resize);

                    if (nd.getNext() != null) {
                        Node<Long, V> nodeNext = nd.getNext();
                        while (true) {
                            saveResize(nodeNext, resize);

                            if (nodeNext.getNext() == null) {
                                break;
                            } else {
                                nodeNext = nodeNext.getNext();
                            }
                        }
                    }
                }
            }
            nodes = resize;
        }
    }

    private void saveResize(Node<Long, V> nd, Node<Long, V>[] resize) {
        Node<Long, V> node = getValueForKey(nd.getKey(), resize);
        if (node == null) {
            resize[getIndex(nd.getKey())] = setNode(nd.getKey(), nd.getValue());
            NOT_FREE_INDEXES++;
        } else {
            Node<Long, V> nodeForSave = setNode(nd.key, nd.getValue());
            if (compareNode(node, nodeForSave) || node.getKey().equals(nodeForSave.getKey())) {
                node.setValue(nd.getValue());
            } else {
                node.setNext(setNode(nd.getKey(), nd.value));
            }
        }
    }

    private Node<Long, V> setNode(long key, V value) {
        Node<Long, V> node = new Node<>();
        node.setKey(key);
        node.setValue(value);
        return node;
    }
}
